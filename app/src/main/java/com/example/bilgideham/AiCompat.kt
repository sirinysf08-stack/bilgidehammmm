package com.example.bilgideham

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG_AI = "AI_COMPAT"
private const val DEFAULT_MODEL = "gemini-2.0-flash"

private val vertexAiService by lazy { Firebase.vertexAI }

// Güvenlik ayarı olmadan, hızlı model
private fun getGenerativeModel(modelName: String) = vertexAiService.generativeModel(modelName = modelName)

suspend fun aiGenerateText(prompt: String): String =
    withContext(Dispatchers.IO) {
        try {
            val model = getGenerativeModel(DEFAULT_MODEL)
            val response = model.generateContent(prompt)
            response.text?.trim() ?: "İçerik alınamadı."
        } catch (e: Exception) {
            Log.e(TAG_AI, "Hata: ${e.message}")
            val err = e.localizedMessage?.lowercase() ?: ""
            when {
                err.contains("quota") || err.contains("exhausted") ->
                    "⚠️ Sistem yoğun. Lütfen 5 saniye sonra tekrar bas."
                err.contains("billing") ->
                    "⚠️ Ödeme planı hatası."
                else ->
                    "⚠️ Bağlantı hatası. Tekrar dene."
            }
        }
    }

suspend fun dictionaryExplainText(word: String): String {
    if (word.isBlank()) return "Lütfen kelime yaz."
    return aiGenerateText("""
        Sen Öğretmensin. Kelime: "$word".
        GÖREV: Anlamını 5. sınıf çocuğuna anlat. 2 örnek cümle ver.
    """.trimIndent())
}

suspend fun atlasLookupText(query: String): String {
    if (query.isBlank()) return "Lütfen yer ismi yaz."
    return aiGenerateText("""
        Sen Atlassın. Yer: "$query".
        GÖREV: 5-6 maddelik özet bilgi (Başkent, Nüfus, Özellik). 5. sınıf seviyesi.
    """.trimIndent())
}

suspend fun solveQuestionText(questionOrPrompt: String): String {
    if (questionOrPrompt.isBlank()) return "Soru boş."
    return aiGenerateText("Sen öğretmensin. Soru: $questionOrPrompt\nGÖREV: Adım adım çöz.")
}

suspend fun chatWithBuddy(userMessage: String): String {
    return aiGenerateText("Rol: İngilizce Arkadaşı (A2). Mesaj: $userMessage\nCevap ver (Kısa İngilizce).")
}

suspend fun fixComposition(text: String): String {
    return aiGenerateText("Rol: Türkçe Öğretmeni. Metin: $text\nGÖREV: Düzelt ve iyileştir.")
}