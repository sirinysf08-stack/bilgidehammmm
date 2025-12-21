package com.example.bilgideham

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.Locale

// Firestore İşlemleri
object QuestionRepository {
    private val db = Firebase.firestore
    private const val COLLECTION_NAME = "questions"

    fun mapLessonToDbTag(lessonTitle: String): String {
        val t = lessonTitle.lowercase(Locale("tr", "TR"))

        return when {
            t.contains("deneme") -> "Deneme"
            t.contains("paragraf") -> "Paragraf"

            // --- İNGİLİZCE SEVİYELERİ ---
            t.contains("a1") -> "IngilizceA1"
            t.contains("a2") -> "IngilizceA2"
            t.contains("b1") -> "IngilizceB1"
            t.contains("b2") -> "IngilizceB2"
            t.contains("c1") -> "IngilizceC1"
            t.contains("c2") -> "IngilizceC2"
            t.contains("ingilizce") || t.contains("english") || t.contains("ing") -> "Ingilizce"

            t.contains("matematik") || t.contains("mat") -> "Matematik"
            t.contains("fen") -> "Fen"
            t.contains("sosyal") -> "Sosyal"
            t.contains("türkçe") || t.contains("turkce") -> "Turkce"
            t.contains("din") -> "Din"
            t.contains("arapça") || t.contains("arapca") || t.contains("arap") -> "Arapca"
            else -> lessonTitle
        }
    }

    private fun sha256Short(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.take(12).joinToString("") { "%02x".format(it) }
    }

    /** Aynı soruyu cihaz tarafında da takip edebilmek için Firestore doküman id'sini yeniden üretir. */
    fun computeDocIdForQuestion(q: QuestionModel): String {
        val stableKey = (q.question.trim() + "|" + q.optionA + "|" + q.optionB + "|" + q.optionC + "|" + q.optionD).trim()
        return sha256Short(stableKey)
    }

    private fun normalizeCorrectAnswer(q: QuestionModel): String {
        val raw = (q.correctAnswer ?: "").trim().uppercase()
        if (raw in listOf("A", "B", "C", "D")) return raw
        return when {
            q.optionA.trim().equals(raw, ignoreCase = true) -> "A"
            q.optionB.trim().equals(raw, ignoreCase = true) -> "B"
            q.optionC.trim().equals(raw, ignoreCase = true) -> "C"
            q.optionD.trim().equals(raw, ignoreCase = true) -> "D"
            else -> "A"
        }
    }

    suspend fun saveQuestionsToFirestore(questions: List<QuestionModel>): Int {
        if (questions.isEmpty()) return 0
        var savedCount = 0
        val batch = db.batch()

        for (q in questions) {
            val dbTag = mapLessonToDbTag(q.lesson ?: "Genel")
            val docId = computeDocIdForQuestion(q)

            val docRef = db.collection(COLLECTION_NAME).document(dbTag).collection("items").document(docId)
            val fixedCorrect = normalizeCorrectAnswer(q)

            val data = hashMapOf(
                "question" to q.question,
                "optionA" to q.optionA,
                "optionB" to q.optionB,
                "optionC" to q.optionC,
                "optionD" to q.optionD,
                "correctAnswer" to fixedCorrect,
                "explanation" to (q.explanation ?: ""),
                "lesson" to dbTag
            )
            batch.set(docRef, data)
            savedCount++
        }
        return try {
            batch.commit().await()
            savedCount
        } catch (_: Exception) {
            0
        }
    }

    /**
     * Öğrenci tarafında tekrarları azaltmak için:
     * - Daha fazla soru çekip (oversample) cihaz tarafında karıştırır.
     * - excludeDocIds içinde olan dokümanları filtreler.
     */
    suspend fun getQuestionsFromFirestore(
        lessonTitle: String,
        limit: Int,
        excludeDocIds: Set<String> = emptySet()
    ): List<QuestionModel> {
        val dbTag = mapLessonToDbTag(lessonTitle)
        val resultList = mutableListOf<QuestionModel>()

        val safeLimit = when {
            limit > 50 -> 50
            limit <= 0 -> 10
            else -> limit
        }

        // Oversample: tekrar filtrelenince de yeterli kalsın
        val fetchLimit = (safeLimit * 5).coerceAtLeast(50).coerceAtMost(200)

        return try {
            val snapshot = db.collection(COLLECTION_NAME)
                .document(dbTag)
                .collection("items")
                .limit(fetchLimit.toLong())
                .get()
                .await()

            for (doc in snapshot.documents) {
                if (excludeDocIds.contains(doc.id)) continue

                val q = QuestionModel(
                    question = doc.getString("question") ?: "",
                    optionA = doc.getString("optionA") ?: "",
                    optionB = doc.getString("optionB") ?: "",
                    optionC = doc.getString("optionC") ?: "",
                    optionD = doc.getString("optionD") ?: "",
                    correctAnswer = doc.getString("correctAnswer") ?: "",
                    explanation = doc.getString("explanation") ?: "",
                    lesson = lessonTitle,
                    needsImage = false,
                    imagePrompt = ""
                )

                if (q.question.isNotBlank()) {
                    resultList.add(q.copy(correctAnswer = normalizeCorrectAnswer(q)))
                }
            }

            // Cihaz tarafı karıştır + tekilleştir
            resultList
                .distinctBy { computeDocIdForQuestion(it) }
                .shuffled()
                .take(safeLimit)
        } catch (e: Exception) {
            Log.e("QuestionRepository", "getQuestionsFromFirestore hata: ${e.message}")
            emptyList()
        }
    }

    // --- YENİ EKLENEN: ÖZEL DENEME OLUŞTURUCU ---
    suspend fun createStructuredExam(examType: String): List<QuestionModel> {
        val distribution = if (examType == "MARATON") {
            listOf("Turkce" to 28, "Matematik" to 28, "Fen" to 22, "Sosyal" to 18, "IngilizceA1" to 14, "Din" to 10)
        } else {
            listOf("Turkce" to 16, "Matematik" to 16, "Fen" to 12, "Sosyal" to 10, "IngilizceA1" to 8, "Din" to 8)
        }

        val fullExamList = mutableListOf<QuestionModel>()
        for ((dbTag, count) in distribution) {
            try {
                val snapshot = db.collection(COLLECTION_NAME).document(dbTag).collection("items").limit(count.toLong()).get().await()
                val lessonTitle = when (dbTag) {
                    "Turkce" -> "Türkçe"
                    "Matematik" -> "Matematik"
                    "Fen" -> "Fen Bilimleri"
                    "Sosyal" -> "Sosyal Bilgiler"
                    "IngilizceA1" -> "İngilizce"
                    "Din" -> "Din Kültürü"
                    else -> dbTag
                }
                for (doc in snapshot.documents) {
                    val q = QuestionModel(
                        question = doc.getString("question") ?: "",
                        optionA = doc.getString("optionA") ?: "",
                        optionB = doc.getString("optionB") ?: "",
                        optionC = doc.getString("optionC") ?: "",
                        optionD = doc.getString("optionD") ?: "",
                        correctAnswer = doc.getString("correctAnswer") ?: "",
                        explanation = doc.getString("explanation") ?: "",
                        lesson = lessonTitle,
                        needsImage = false,
                        imagePrompt = ""
                    )
                    if (q.question.isNotBlank()) fullExamList.add(q.copy(correctAnswer = normalizeCorrectAnswer(q)))
                }
            } catch (_: Exception) { }
        }
        return fullExamList
    }

    suspend fun getQuestionCounts(): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        val dbTags = listOf(
            "Matematik", "Turkce", "Fen", "Sosyal",
            "Ingilizce", "IngilizceA1", "IngilizceA2", "IngilizceB1",
            "IngilizceB2", "IngilizceC1", "IngilizceC2",
            "Din", "Arapca", "Paragraf", "Deneme"
        )

        for (tag in dbTags) {
            try {
                val snapshot = db.collection(COLLECTION_NAME)
                    .document(tag)
                    .collection("items")
                    .count()
                    .get(com.google.firebase.firestore.AggregateSource.SERVER)
                    .await()

                val displayName = when (tag) {
                    "Turkce" -> "Türkçe"
                    "Ingilizce" -> "İngilizce (Genel)"
                    "IngilizceA1" -> "İngilizce (A1)"
                    "IngilizceA2" -> "İngilizce (A2)"
                    "IngilizceB1" -> "İngilizce (B1)"
                    "IngilizceB2" -> "İngilizce (B2)"
                    "IngilizceC1" -> "İngilizce (C1)"
                    "IngilizceC2" -> "İngilizce (C2)"
                    "Arapca" -> "Arapça"
                    "Din" -> "Din Kültürü"
                    "Deneme" -> "Deneme Sınavı"
                    "Fen" -> "Fen Bilimleri"
                    "Sosyal" -> "Sosyal Bilgiler"
                    else -> tag
                }
                counts[displayName] = snapshot.count.toInt()
            } catch (_: Exception) {
                counts[tag] = 0
            }
        }
        return counts
    }

    suspend fun deleteAllQuestionsFromFirestore() {
        val lessons = listOf(
            "Matematik", "Turkce", "Fen", "Sosyal",
            "Ingilizce", "IngilizceA1", "IngilizceA2", "IngilizceB1",
            "IngilizceB2", "IngilizceC1", "IngilizceC2",
            "Din", "Arapca", "Paragraf", "Deneme"
        )

        for (lessonTag in lessons) {
            try {
                val snapshot = db.collection(COLLECTION_NAME).document(lessonTag).collection("items").get().await()
                if (!snapshot.isEmpty) {
                    val batch = db.batch()
                    for (doc in snapshot.documents) batch.delete(doc.reference)
                    batch.commit().await()
                }
            } catch (e: Exception) {
                Log.e("Firestore", "$lessonTag silinirken hata: ${e.message}")
            }
        }
    }
}
