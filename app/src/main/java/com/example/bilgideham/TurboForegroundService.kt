package com.example.bilgideham

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

/**
 * TurboForegroundService için buluttaki stok sayımlarını döner.
 * Öncelik sırası:
 *  1) meta/question_counts dokümanı varsa onu okur (en hızlı).
 *  2) Yoksa, olası soru koleksiyon adlarını tarar ve ders bazında sayım yapar.
 *
 * Not: Şema farklılıklarına tolerans için hem "tek koleksiyon + lesson alanı" hem
 * "questionPools/{lesson}/(items|questions)" gibi hiyerarşileri best-effort destekler.
 */
suspend fun QuestionRepository.getQuestionCounts(): Map<String, Int> {
    val db = Firebase.firestore

    // 1) Hızlı yol: meta/question_counts -> Map<String, Int>
    runCatching {
        val snap = db.collection("meta").document("question_counts").get().await()
        if (snap.exists()) {
            val data = snap.data ?: emptyMap()
            val out = LinkedHashMap<String, Int>(data.size)
            for ((k, v) in data) {
                val n = when (v) {
                    is Number -> v.toInt()
                    is String -> v.toIntOrNull()
                    else -> null
                } ?: 0
                out[k] = n
            }
            if (out.isNotEmpty()) return out
        }
    }

    // 2) Koleksiyon tespiti (mevcut dataya göre ilk dolu olanı seç)
    val collectionCandidates = listOf(
        "questions",
        "question_pool",
        "questionPools",
        "question_pools",
        "cloud_questions",
        "questionBank"
    )

    val selectedCollection: String? = run {
        for (name in collectionCandidates) {
            val ok = runCatching {
                val qs = db.collection(name).limit(1).get().await()
                !qs.isEmpty
            }.getOrDefault(false)
            if (ok) return@run name
        }
        null
    }

    if (selectedCollection == null) return emptyMap()

    // Ders listesi (varsa Firestore lessons, yoksa sabit fallback)
    val lessons: List<String> = runCatching {
        val ls = db.collection("lessons").get().await()
        val fromDocs = ls.documents.mapNotNull { d ->
            (d.getString("title") ?: d.id).takeIf { it.isNotBlank() }
        }.distinct()
        if (fromDocs.isNotEmpty()) fromDocs else emptyList()
    }.getOrDefault(emptyList()).ifEmpty {
        listOf(
            "Türkçe",
            "Matematik",
            "Fen",
            "Sosyal Bilgiler",
            "İngilizce",
            "Arapça",
            "Paragraf",
            "Deneme",
            "Deneme Sınavı",
            "GENEL_DENEME",
            "MARATON"
        )
    }

    val root = db.collection(selectedCollection)

    // Şema tespiti: lesson alan adı (varsa)
    val lessonField: String? = runCatching {
        val sample = root.limit(1).get().await().documents.firstOrNull()?.data?.keys ?: emptySet()
        when {
            "lesson" in sample -> "lesson"
            "lessonTitle" in sample -> "lessonTitle"
            "lesson_name" in sample -> "lesson_name"
            "ders" in sample -> "ders"
            else -> null
        }
    }.getOrNull()

    suspend fun countQuery(q: Query): Int = runCatching { q.get().await().size() }.getOrDefault(0)

    suspend fun countForLesson(lesson: String): Int {
        // A) Tek koleksiyon + lesson alanı
        val flat = if (lessonField != null) {
            countQuery(root.whereEqualTo(lessonField, lesson))
        } else 0

        // B) Hiyerarşik: questionPools/{lesson}/items veya questions
        val subItems = runCatching {
            db.collection(selectedCollection).document(lesson).collection("items").get().await().size()
        }.getOrDefault(0)

        val subQuestions = runCatching {
            db.collection(selectedCollection).document(lesson).collection("questions").get().await().size()
        }.getOrDefault(0)

        // En güçlü sinyali seç
        return maxOf(flat, subItems, subQuestions)
    }

    val out = LinkedHashMap<String, Int>(lessons.size)
    for (l in lessons) {
        out[l] = countForLesson(l)
    }
    return out
}
