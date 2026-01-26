package com.example.bilgideham

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private const val TAG_AI = "AI_COMPAT"
private const val DEFAULT_MODEL = "gemini-2.0-flash"

private val vertexAiService by lazy { Firebase.vertexAI }
private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

// Güvenlik ayarı olmadan, hızlı model
private fun getGenerativeModel(modelName: String) = vertexAiService.generativeModel(modelName = modelName)

// Anonim giriş kontrolü - Vertex AI için gerekli
private suspend fun ensureSignedIn() {
    if (firebaseAuth.currentUser == null) {
        try {
            firebaseAuth.signInAnonymously().await()
            DebugLog.d(TAG_AI, "Anonim giriş başarılı")
        } catch (e: Exception) {
            Log.e(TAG_AI, "Anonim giriş hatası: ${e.message}")
        }
    }
}

suspend fun aiGenerateText(prompt: String): String =
    withContext(Dispatchers.IO) {
        try {
            // Önce oturum açık mı kontrol et
            ensureSignedIn()
            
            val model = getGenerativeModel(DEFAULT_MODEL)
            val response = model.generateContent(prompt)
            response.text?.trim() ?: "İçerik alınamadı."
        } catch (e: Exception) {
            Log.e(TAG_AI, "Hata Türü: ${e::class.simpleName}")
            Log.e(TAG_AI, "Hata Mesajı: ${e.message}")
            Log.e(TAG_AI, "Stack Trace:", e)
            val err = e.localizedMessage?.lowercase() ?: ""
            when {
                err.contains("quota") || err.contains("exhausted") ->
                    "⚠️ Sistem yoğun. Lütfen 5 saniye sonra tekrar bas."
                err.contains("billing") ->
                    "⚠️ Ödeme planı hatası."
                err.contains("permission") || err.contains("denied") ->
                    "⚠️ Firebase izin hatası. Vertex AI aktif mi?"
                err.contains("network") || err.contains("connect") ->
                    "⚠️ İnternet bağlantısı yok."
                err.contains("sign") || err.contains("auth") ->
                    "⚠️ Kimlik doğrulama hatası. Lütfen Firebase Auth'u kontrol edin."
                else ->
                    "⚠️ Hata: ${e::class.simpleName} - ${e.message?.take(50)}"
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

suspend fun atlasLookupText(query: String, levelDescription: String = "5. sınıf"): String {
    if (query.isBlank()) return "Lütfen yer ismi yaz."
    
    // Seviyeye göre detay seviyesi ve dil tonu
    val (detailLevel, languageStyle, exampleContent) = when {
        levelDescription.contains("4. sınıf") -> Triple(
            "Çok basit",
            "Çok kısa cümleler, basit kelimeler kullan. Emoji kullanabilirsin.",
            "Örnek: Tokyo Japonya'nın başkentidir. Çok büyük bir şehirdir. 🏙️"
        )
        levelDescription.contains("5") || levelDescription.contains("6") -> Triple(
            "Ortaokul alt seviye",
            "Anlaşılır ve orta uzunlukta cümleler. Basit coğrafi terimler.",
            "Örnek: Tokyo, Japonya'nın başkenti ve en büyük şehridir. Yaklaşık 14 milyon nüfusa sahiptir."
        )
        levelDescription.contains("7") || levelDescription.contains("8") -> Triple(
            "Ortaokul üst seviye",
            "Daha detaylı ve akademik bir dil. Coğrafi terimler, ekonomik ve kültürel bilgiler ekle.",
            "Örnek: Tokyo, Japonya'nın başkenti ve ekonomik merkezidir. Pasifik Ateş Çemberi üzerinde yer alır ve deprem riski yüksektir. Teknoloji ve finans sektörlerinde dünya lideridir."
        )
        levelDescription.contains("9") || levelDescription.contains("10") || levelDescription.contains("lise") -> Triple(
            "Lise seviyesi",
            "Akademik ve detaylı. Jeopolitik, ekonomik analizler, tarihsel bağlam ekle.",
            "Örnek: Tokyo, Japonya'nın siyasi ve ekonomik başkentidir. Meiji Restorasyonu sonrası modernleşmenin merkezi olmuştur. Küresel finans merkezlerinden biridir ve GSYİH'sı birçok ülkeden yüksektir."
        )
        levelDescription.contains("KPSS") || levelDescription.contains("Üniversite") -> Triple(
            "Üniversite/KPSS seviyesi",
            "Profesyonel ve kapsamlı. İstatistikler, jeopolitik analizler, karşılaştırmalar, tarihsel perspektif.",
            "Örnek: Tokyo, Japonya'nın başkenti ve küresel alfa şehirlerinden biridir. 1868 Meiji Restorasyonu ile başkent olmuştur. Büyük Tokyo Metropol Alanı 38 milyon nüfusla dünyanın en kalabalık kentsel alanıdır. Nikkei 225 borsası ve Fortune 500 şirketleri ile ekonomik güç merkezidir."
        )
        else -> Triple(
            "Ortaokul seviyesi",
            "Anlaşılır ve bilgilendirici.",
            "Örnek: Başkent, nüfus ve önemli özellikler."
        )
    }
    
    return aiGenerateText("""
        Sen bir Coğrafya Atlası'sın. Kullanıcı "$query" hakkında bilgi istiyor.
        
        ÖNEMLİ - KULLANICI SEVİYESİ: $levelDescription
        DETAY SEVİYESİ: $detailLevel
        DİL VE TON: $languageStyle
        
        ÖRNEK CEVAP TARZI:
        $exampleContent
        
        GÖREV:
        1. "$query" hakkında 5-6 maddelik özet bilgi ver
        2. Şunları içer: Başkent (varsa), Nüfus, Coğrafi Konum, Ekonomi, Kültürel Özellikler
        3. MUTLAKA $levelDescription seviyesine uygun yaz
        4. Seviyeye göre kelime seçimi ve cümle yapısı kullan
        
        ⚠️ KRİTİK: Cevabının başında "X. sınıf seviyesine uygun" gibi ifadeler KULLANMA. Direkt bilgiyi ver.
        ⚠️ Seviyeyi belirtme, sadece o seviyeye uygun dil kullan.
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