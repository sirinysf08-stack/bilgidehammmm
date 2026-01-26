package com.example.bilgideham

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.Locale

/**
 * Kullanıcı bazlı "görüldü" (seen) state yönetimi.
 *
 * Koleksiyon yapısı:
 * user_question_state/{userId}/seen/{questionDocId}
 *  - lesson: String (dbTag)
 *  - seenAt: Long
 */
object UserSeenRepository {
    private val db = Firebase.firestore

    private const val USER_STATE_COLLECTION = "user_question_state"
    private const val USER_SEEN_SUBCOLLECTION = "seen"

    // ✅ Artık QuestionRepository.mapLessonToDbTag'e bağımlı değil
    private fun mapLessonToDbTag(lessonTitle: String): String {
        val raw = lessonTitle.trim()
        if (raw.isBlank()) return "GENEL"

        val tr = Locale("tr", "TR")
        val lower = raw.lowercase(tr)

        return when {
            lower == "genel_deneme" || lower.contains("genel deneme") -> "GENEL_DENEME"
            lower.contains("maraton") -> "MARATON"

            lower.contains("türkçe") || lower.contains("turkce") -> "TURKCE"
            lower.contains("paragraf") -> "PARAGRAF"
            // "deneme sınavı" vb.
            lower.contains("deneme") -> "DENEME"

            lower.contains("matematik") -> "MATEMATIK"
            lower.contains("fen") -> "FEN"
            lower.contains("sosyal") -> "SOSYAL"
            lower.contains("ingiliz") -> "INGILIZCE"
            lower.contains("arap") -> "ARAPCA"

            else -> toSafeDbTag(raw)
        }
    }

    private fun toSafeDbTag(input: String): String {
        // Türkçe karakterleri güvenli tag'e çevir
        val s = input.trim()
            .replace('İ', 'I').replace('ı', 'I')
            .replace('Ş', 'S').replace('ş', 's')
            .replace('Ğ', 'G').replace('ğ', 'g')
            .replace('Ü', 'U').replace('ü', 'u')
            .replace('Ö', 'O').replace('ö', 'o')
            .replace('Ç', 'C').replace('ç', 'c')

        // boşluk/ayraç -> underscore, kalanını filtrele
        val up = s.uppercase(Locale.US)
            .replace(Regex("[\\s\\-]+"), "_")
            .replace(Regex("[^A-Z0-9_]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')

        return if (up.isBlank()) "GENEL" else up
    }

    /**
     * Best-effort: listedeki soruları kullanıcı için "seen" olarak işaretlemeye çalışır.
     */
    suspend fun claimAllBestEffort(
        userId: String,
        lessonTitle: String,
        questions: List<QuestionModel>
    ) {
        if (userId.isBlank() || questions.isEmpty()) return

        val dbTag = mapLessonToDbTag(lessonTitle)

        for (q in questions) {
            val docId = runCatching { QuestionRepository.computeDocIdForQuestion(q) }
                .getOrNull()
                .orEmpty()
                .trim()
            if (docId.isBlank()) continue
            runCatching { claimOne(userId = userId, questionDocId = docId, lessonDbTag = dbTag) }
        }
    }

    /**
     * Eğer caller docId'leri biliyorsa doğrudan claim edebilir.
     */
    suspend fun claimAllByDocIdsBestEffort(
        userId: String,
        lessonTitle: String,
        questionDocIds: Collection<String>
    ) {
        if (userId.isBlank() || questionDocIds.isEmpty()) return

        val dbTag = mapLessonToDbTag(lessonTitle)

        for (id in questionDocIds) {
            val docId = id.trim()
            if (docId.isBlank()) continue
            runCatching { claimOne(userId = userId, questionDocId = docId, lessonDbTag = dbTag) }
        }
    }

    /**
     * Tek bir soruyu kullanıcı için atomik "seen" yapar.
     * @return true: ilk defa işaretlendi, false: zaten vardı veya hata oldu.
     */
    suspend fun claimOne(
        userId: String,
        questionDocId: String,
        lessonDbTag: String
    ): Boolean {
        if (userId.isBlank() || questionDocId.isBlank()) return false

        val seenDocRef = db.collection(USER_STATE_COLLECTION)
            .document(userId)
            .collection(USER_SEEN_SUBCOLLECTION)
            .document(questionDocId)

        return try {
            db.runTransaction { tx ->
                val snap = tx.get(seenDocRef)
                if (snap.exists()) {
                    false
                } else {
                    tx.set(
                        seenDocRef,
                        mapOf(
                            "lesson" to lessonDbTag,
                            "seenAt" to System.currentTimeMillis()
                        )
                    )
                    true
                }
            }.await()
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Opsiyonel: bir soru kullanıcıda seen mi?
     */
    suspend fun isSeen(userId: String, questionDocId: String): Boolean {
        if (userId.isBlank() || questionDocId.isBlank()) return false
        return try {
            val snap = db.collection(USER_STATE_COLLECTION)
                .document(userId)
                .collection(USER_SEEN_SUBCOLLECTION)
                .document(questionDocId)
                .get()
                .await()
            snap.exists()
        } catch (_: Exception) {
            false
        }
    }
}
