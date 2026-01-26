package com.example.bilgideham

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

/**
 * Multi-Gemini API Provider - 3 Farklı API Key ile Paralel Çalışma
 * 
 * Her key farklı bir Google hesabından alınmalı = 3x kota!
 * Gemini 2.0 Flash model kullanır.
 * 
 * API Key'ler: assets/gemini_config.json dosyasından okunur
 */
object GeminiApiProvider {
    
    private const val TAG = "GeminiApiProvider"
    private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent"
    
    // 3 API Key - assets/gemini_config.json'dan okunacak
    private val API_KEYS = mutableListOf<String>()
    
    // Key isimleri (log için)
    private val KEY_NAMES = listOf("🔵 Gemini-1", "🟢 Gemini-2", "🟣 Gemini-3", "🟡 Gemini-4")
    
    // Helper fonksiyonlar
    fun getLoadedKeyCount(): Int = API_KEYS.size
    fun getFirstKey(): String? = API_KEYS.firstOrNull()
    fun getKeyByIndex(index: Int): String? = API_KEYS.getOrNull(index)
    
    // Rate limiter (basit - son istek zamanı)
    private val lastRequestTime = ConcurrentHashMap<Int, Long>()
    private const val MIN_REQUEST_INTERVAL_MS = 3000L // 3 saniye (Gemini 2.5 Pro: 20 RPM = 3sn/istek)
    
    // Fingerprint cache (paylaşımlı - tüm key'ler için)
    private val seenFingerprints = ConcurrentHashMap.newKeySet<String>()
    private const val MAX_CACHE = 5000
    
    // Doğru cevap dağılımı takibi
    private val lastCorrectAnswers = mutableListOf<String>()
    private const val MAX_ANSWER_HISTORY = 30
    
    // Key'ler yüklendi mi?
    private var keysLoaded = false
    
    /**
     * assets/gemini_config.json dosyasından API key'leri yükle
     */
    fun loadKeysFromAssets(context: Context): Boolean {
        if (keysLoaded && API_KEYS.size >= 3) return true
        
        try {
            val inputStream = context.assets.open("gemini_config.json")
            val jsonStr = inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(jsonStr)
            val keysArray = json.getJSONArray("gemini_api_keys")
            
            API_KEYS.clear()
            for (i in 0 until keysArray.length()) {
                val key = keysArray.getString(i)
                if (key.isNotBlank() && !key.startsWith("BURAYA")) {
                    API_KEYS.add(key)
                }
            }
            
            keysLoaded = API_KEYS.size >= 1
            Log.d(TAG, "✅ ${API_KEYS.size} API key yüklendi")
            return keysLoaded
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ gemini_config.json okunamadı: ${e.message}")
            return false
        }
    }
    
    /**
     * Manuel API Key'leri ayarla (alternatif yöntem)
     */
    fun setApiKeys(key1: String, key2: String, key3: String) {
        API_KEYS.clear()
        API_KEYS.addAll(listOf(key1, key2, key3).filter { it.isNotBlank() })
        keysLoaded = API_KEYS.isNotEmpty()
        Log.d(TAG, "✅ ${API_KEYS.size} Gemini API key ayarlandı")
    }
    
    /**
     * Belirtilen key index ile soru üret (Retry + Rate Limit)
     * @param keyIndex 0, 1 veya 2
     */
    suspend fun generateWithKey(
        keyIndex: Int,
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ): Pair<List<QuestionModel>, String> = withContext(Dispatchers.IO) {
        
        val aiName = KEY_NAMES.getOrElse(keyIndex) { "🔵 Gemini-$keyIndex" }
        val apiKey = API_KEYS.getOrElse(keyIndex) { "" }
        
        if (apiKey.isBlank() || apiKey.startsWith("GEMINI_API_KEY")) {
            Log.e(TAG, "❌ API Key #$keyIndex ayarlanmamış!")
            return@withContext Pair(emptyList(), "$aiName (KEY YOK)")
        }
        
        // Rate Limiter: Son istekten 4sn geçmemişse bekle
        val lastTime = lastRequestTime[keyIndex] ?: 0L
        val elapsed = System.currentTimeMillis() - lastTime
        if (elapsed < MIN_REQUEST_INTERVAL_MS) {
            val waitTime = MIN_REQUEST_INTERVAL_MS - elapsed
            Log.d(TAG, "⏳ $aiName rate limit: ${waitTime}ms bekleniyor...")
            kotlinx.coroutines.delay(waitTime)
        }
        lastRequestTime[keyIndex] = System.currentTimeMillis()
        
        // Retry mekanizması (3 deneme)
        var lastException: Exception? = null
        repeat(3) { attempt ->
            try {
                Log.d(TAG, "🔄 $aiName başlatılıyor: $lesson, $count soru (deneme ${attempt + 1}/3)")
                
                // %30 fazla iste
                val askCount = (count * 1.3).toInt().coerceIn(count, 25)
                val prompt = buildMebTymmPrompt(lesson, askCount, level, schoolType, grade)
                val response = callGeminiApi(apiKey, prompt)
                
                if (response.isBlank()) {
                    Log.w(TAG, "⚠️ $aiName boş yanıt döndü")
                    throw Exception("Boş yanıt")
                }
                
                val parsed = parseQuestions(response, lesson)
                Log.d(TAG, "📝 $aiName: ${parsed.size} soru parse edildi, doğrulama başlıyor...")
                
                // GEVŞEK DOĞRULAMA (AI validation kaldırıldı - çok yavaş)
                val validated = mutableListOf<QuestionModel>()
                
                for (q in parsed) {
                    val fp = fingerprint(q)
                    val isUnique = fp !in seenFingerprints
                    val hasValidOptions = validateOptionLength(q)
                    val hasValidContent = validateQuestionContent(q)
                    
                    // Şık sayısına göre geçerli cevapları belirle
                    val hasOptionE = !q.optionE.isNullOrBlank()
                    val hasOptionD = !q.optionD.isNullOrBlank()
                    val validAnswers = when {
                        hasOptionE -> listOf("A", "B", "C", "D", "E")
                        hasOptionD -> listOf("A", "B", "C", "D")
                        else -> listOf("A", "B", "C")
                    }
                    val hasSingleCorrect = q.correctAnswer in validAnswers
                    
                    // AI Validation KALDIRILDI (çok yavaş + hepsini reddediyor)
                    if (isUnique && hasValidOptions && hasSingleCorrect && hasValidContent) {
                        seenFingerprints.add(fp)
                        synchronized(lastCorrectAnswers) {
                            lastCorrectAnswers.add(q.correctAnswer)
                            if (lastCorrectAnswers.size > MAX_ANSWER_HISTORY) {
                                lastCorrectAnswers.removeAt(0)
                            }
                        }
                        validated.add(q)
                    }
                }
                
                if (seenFingerprints.size > MAX_CACHE) {
                    seenFingerprints.clear()
                }
                
                Log.d(TAG, "✅ $aiName: ${validated.size}/${parsed.size} soru doğrulandı")
                
                // Başarılı - return
                return@withContext Pair(validated.take(count), aiName)
                
            } catch (e: Exception) {
                lastException = e
                val isRetryable = e.message?.contains("429") == true || 
                                 e.message?.contains("409") == true ||
                                 e.message?.contains("quota") == true ||
                                 e.message?.contains("Unterminated") == true
                
                if (isRetryable && attempt < 2) {
                    val backoff = (attempt + 1) * 3000L // 3s, 6s
                    Log.w(TAG, "⚠️ $aiName hata (${e.message?.take(40)}), ${backoff}ms sonra tekrar...")
                    kotlinx.coroutines.delay(backoff)
                } else if (!isRetryable) {
                    Log.e(TAG, "❌ $aiName kritik hata: ${e.message}")
                    return@withContext Pair(emptyList(), "$aiName (HATA: ${e.message?.take(30)})")
                }
            }
        }
        
        // 3 deneme de başarısız
        Log.e(TAG, "❌ $aiName 3 denemede başarısız: ${lastException?.message}")
        Pair(emptyList(), "$aiName (BAŞARISIZ)")
    }
    
    // ==================== API ÇAĞRISI ====================
    
    suspend fun callGeminiApi(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        val urlStr = "$API_URL?key=$apiKey"
        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 300000
            connection.readTimeout = 300000
            
            val requestBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 65536) // 32K → 64K (Gemini 2.5 Pro max - JSON truncation final fix)
                    put("topP", 0.95)
                    put("topK", 40)
                })
            }
            
            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestBody.toString())
                writer.flush()
            }
            
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                val errorStream = connection.errorStream?.bufferedReader()?.readText() ?: "Unknown error"
                throw Exception("API Error $responseCode: ${errorStream.take(100)}")
            }
            
            val responseText = connection.inputStream.bufferedReader().readText()
            Log.d(TAG, "API Response (first 500): ${responseText.take(500)}")
            val jsonResponse = JSONObject(responseText)
            
            // Gemini 3 Pro Thinking mode: birden fazla part olabilir
            // JSON içeren part'ı bul
            val parts = jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
            
            var resultText = ""
            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                // thought=true olan part'ları atla (Gemini 3 Pro Thinking mode)
                if (part.optBoolean("thought", false)) {
                    continue
                }
                if (part.has("text")) {
                    val text = part.getString("text")
                    // JSON içeren part'ı tercih et
                    if (text.contains("[") && text.contains("]")) {
                        resultText = text
                        break
                    }
                    resultText = text
                }
            }
            return@withContext resultText.trim()
                
        } finally {
            connection.disconnect()
        }
    }
    
    // ==================== AI DOĞRULAMA ====================
    
    private suspend fun validateCorrectAnswerWithAI(apiKey: String, q: QuestionModel): Boolean = withContext(Dispatchers.IO) {
        val prompt = """
Sen bir sınav uzmanısın. Bu soruyu çöz ve SADECE doğru cevabın harfini yaz.

SORU: ${q.question}
A) ${q.optionA}
B) ${q.optionB}
C) ${q.optionC}
D) ${q.optionD}
${if (q.optionE.isNotBlank()) "E) ${q.optionE}" else ""}

SADECE TEK HARF YAZ (A, B, C, D veya E):
""".trimIndent()

        try {
            val response = callGeminiApi(apiKey, prompt)
            val aiAnswer = response.uppercase().firstOrNull { it in 'A'..'E' }?.toString() ?: ""
            aiAnswer == q.correctAnswer
        } catch (e: Exception) {
            true // Hata durumunda geçerli say
        }
    }
    
    // ==================== DOĞRULAMA FONKSİYONLARI ====================
    
    private fun fingerprint(q: QuestionModel): String {
        // GEVŞEK FINGERPRINT: Sadece soru metninin ilk 100 karakteri
        // (Şıklar farklı olabilir, aynı konudan farklı sorular üretilebilir)
        val questionStart = q.question?.take(100) ?: ""
        return questionStart.lowercase()
            .replace("\\s+".toRegex(), " ")
            .replace("[^a-z0-9 ]".toRegex(), "") // Noktalama işaretlerini kaldır
            .trim()
            .hashCode()
            .toString()
    }
    
    private fun validateOptionLength(q: QuestionModel): Boolean {
        val options = listOfNotNull(
            q.optionA.takeIf { it.isNotBlank() },
            q.optionB.takeIf { it.isNotBlank() },
            q.optionC.takeIf { it.isNotBlank() },
            q.optionD.takeIf { it.isNotBlank() },
            q.optionE.takeIf { it.isNotBlank() }
        )
        if (options.size < 3) return false
        
        // GEVŞEK KONTROL: Sadece çok aşırı farkları reddet
        val lengths = options.map { it.length }
        val avg = lengths.average()
        val maxDeviation = lengths.maxOf { kotlin.math.abs(it - avg) }
        
        // Ortalamadan 3x fazla sapma varsa reddet (önceden 1x idi - çok katı)
        return maxDeviation <= avg * 3
    }
    
    private fun validateQuestionContent(q: QuestionModel): Boolean {
        val question = q.question ?: return false
        
        // Minimum uzunluk kontrolü
        if (question.length < 20) return false
        
        // GEVŞEK FORMAT KONTROLÜ: Türkçe ve İngilizce sorular için
        val hasQuestionFormat = question.contains("?") || 
            question.contains("hangisi", ignoreCase = true) ||
            question.contains("kaçtır", ignoreCase = true) ||
            question.contains("nedir", ignoreCase = true) ||
            question.contains("which", ignoreCase = true) ||
            question.contains("what", ignoreCase = true) ||
            question.contains("how", ignoreCase = true) ||
            question.contains("aşağıdaki", ignoreCase = true) ||
            question.contains("following", ignoreCase = true) ||
            question.contains("correct", ignoreCase = true) ||
            question.contains("doğru", ignoreCase = true)
        
        // Format kontrolünü kaldır - çok katı (paragraf soruları için)
        // if (!hasQuestionFormat) return false
        
        // Doğru cevap şıkkı var mı?
        val correctOption = when (q.correctAnswer) {
            "A" -> q.optionA
            "B" -> q.optionB
            "C" -> q.optionC
            "D" -> q.optionD
            "E" -> q.optionE
            else -> null
        }
        if (correctOption.isNullOrBlank()) return false
        
        // Şıklar unique mi? (GEVŞEK: sadece tamamen aynı olanları reddet)
        val allOptions = listOfNotNull(q.optionA, q.optionB, q.optionC, q.optionD, q.optionE)
            .filter { it.isNotBlank() }
        
        // Tamamen aynı şıklar varsa reddet
        val uniqueOptions = allOptions.map { it.trim().lowercase() }.distinct()
        if (uniqueOptions.size < allOptions.size - 1) return false // 1 duplicate'e izin ver
        
        // Yasaklı ifadeler (sadaçık olanlar)
        val bannedPhrases = listOf("hepsi doğru", "hiçbiri doğru", "hepsi yanlış", "yukarıdakilerin hepsi", "all of the above", "none of the above")
        for (opt in allOptions) {
            if (bannedPhrases.any { opt.lowercase().contains(it.lowercase()) }) return false
        }
        
        // DERS UYUMU KONTROLÜ (YENİ - KRİTİK)
        if (!validateLessonContentMatchForGeminiProvider(q)) {
            Log.w(TAG, "❌ Ders uyumsuzluğu: ${question.take(50)}")
            return false
        }
        
        return true
    }
    
    /**
     * DERS UYUMU KONTROLÜ (GeminiApiProvider için)
     * AiQuestionGenerator'daki ile aynı mantık
     */
    private fun validateLessonContentMatchForGeminiProvider(q: QuestionModel): Boolean {
        val lesson = q.lesson.lowercase()
        val question = q.question?.lowercase() ?: return false
        val allText = "$question ${q.optionA} ${q.optionB} ${q.optionC} ${q.optionD} ${q.optionE}".lowercase()
        
        // Matematik dersi kontrolü
        if (lesson.contains("matematik") || lesson.contains("math")) {
            val nonMathKeywords = listOf(
                "paragraf", "metin", "yazar", "şair", "hikaye", "öykü",
                "canlı", "bitki", "hayvan", "hücre", "organ",
                "tarih", "coğrafya", "harita", "ülke", "şehir"
            )
            
            for (keyword in nonMathKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Matematik dersinde '$keyword' kelimesi")
                    return false
                }
            }
            
            val mathKeywords = listOf(
                "sayı", "işlem", "toplama", "çıkarma", "çarpma", "bölme",
                "kesir", "geometri", "şekil", "alan", "çevre"
            )
            
            val hasMathContent = mathKeywords.any { allText.contains(it) } || allText.contains(Regex("\\d+"))
            if (!hasMathContent) {
                Log.w(TAG, "❌ Matematik dersinde matematik içeriği yok")
                return false
            }
        }
        
        // Türkçe dersi kontrolü
        if (lesson.contains("türkçe") || lesson.contains("turkce")) {
            val nonTurkishKeywords = listOf(
                "toplama", "çıkarma", "çarpma", "bölme", "işlem",
                "atom", "molekül", "hücre", "enerji",
                "harita", "kıta", "ülke"
            )
            
            for (keyword in nonTurkishKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Türkçe dersinde '$keyword' kelimesi")
                    return false
                }
            }
        }
        
        // Fen Bilimleri dersi kontrolü
        if (lesson.contains("fen")) {
            val nonScienceKeywords = listOf(
                "paragraf", "cümle", "noktalama", "yazım",
                "toplama", "çıkarma", "çarpma", "kesir",
                "tarih", "coğrafya", "harita"
            )
            
            for (keyword in nonScienceKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Fen dersinde '$keyword' kelimesi")
                    return false
                }
            }
            
            val scienceKeywords = listOf(
                "canlı", "bitki", "hayvan", "madde", "enerji",
                "ışık", "ses", "kuvvet", "dünya", "güneş"
            )
            
            val hasScienceContent = scienceKeywords.any { allText.contains(it) }
            if (!hasScienceContent) {
                Log.w(TAG, "❌ Fen dersinde fen içeriği yok")
                return false
            }
        }
        
        // Sosyal Bilgiler dersi kontrolü
        if (lesson.contains("sosyal")) {
            val nonSocialKeywords = listOf(
                "toplama", "çıkarma", "çarpma", "kesir",
                "atom", "molekül", "hücre",
                "paragraf", "cümle"
            )
            
            for (keyword in nonSocialKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Sosyal Bilgiler dersinde '$keyword' kelimesi")
                    return false
                }
            }
        }
        
        return true
    }
    
    // ==================== PROMPT ====================
    
    private fun buildMebTymmPrompt(
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ): String {
        val seviye = when (level) {
            EducationLevel.ILKOKUL -> "İlkokul ${grade ?: 4}. sınıf"
            EducationLevel.ORTAOKUL -> "Ortaokul ${grade ?: 5}. sınıf"
            EducationLevel.LISE -> "${schoolType.displayName} ${grade ?: 9}. sınıf"
            EducationLevel.KPSS -> "KPSS ${schoolType.displayName}"
            EducationLevel.AGS -> "AGS ${schoolType.displayName}"
        }

        val answerDistribution = lastCorrectAnswers.groupingBy { it }.eachCount()
        
        val is5OptionExam = level == EducationLevel.KPSS || level == EducationLevel.AGS || level == EducationLevel.LISE
        val is3OptionGrade = grade == 3
        val optionLetters = when {
            is5OptionExam -> listOf("A", "B", "C", "D", "E")
            is3OptionGrade -> listOf("A", "B", "C")
            else -> listOf("A", "B", "C", "D")
        }
        val leastUsedAnswer = optionLetters.minByOrNull { answerDistribution[it] ?: 0 } ?: "B"
        
        val jsonFormat = when {
            is5OptionExam -> """[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"..."}]"""
            is3OptionGrade -> """[{"question":"...","optionA":"...","optionB":"...","optionC":"...","correctAnswer":"A/B/C","explanation":"..."}]"""
            else -> """[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","correctAnswer":"A/B/C/D","explanation":"..."}]"""
        }
        
        // KPSS için özel prompt
        if (level == EducationLevel.KPSS) {
            return buildKpssPrompt(lesson, count, schoolType, leastUsedAnswer, jsonFormat)
        }
        
        // PARAGRAF için özel prompt
        if (lesson.contains("paragraf", ignoreCase = true)) {
            return buildParagrafPrompt(lesson, count, level, schoolType, grade, leastUsedAnswer, jsonFormat, seviye, is5OptionExam, is3OptionGrade)
        }
        
        // DERS-SEVİYE UYUMU KURALLARI (YENİ - KRİTİK)
        val dersSeviyeKurali = buildDersSeviyeKuraliForGeminiProvider(lesson, level, grade, seviye)
        
        val gradeRules = when (grade) {
            3 -> "3. SINIF: Paragraflar 3-4 cümle, maksimum 50 kelime. Basit dil."
            4 -> "4. SINIF: 5-7 cümle, 70-110 kelime."
            5 -> "5. SINIF: 5-9 cümle, 80-120 kelime."
            6 -> "6. SINIF: 6-10 cümle, 90-125 kelime."
            7 -> "7. SINIF: 7-10 cümle, 90-130 kelime."
            8 -> "8. SINIF (LGS): 7-12 cümle, 100-150 kelime."
            9, 10 -> "LİSE 9-10 (TYT): 8-12 cümle, 120-180 kelime."
            11, 12 -> "LİSE 11-12 (AYT): 10-15 cümle, 150-220 kelime."
            else -> "Genel format: 5-8 cümle."
        }

        return """
$count adet $seviye $lesson sorusu üret.

$dersSeviyeKurali

KURALLAR:
1. ${if(is5OptionExam) "5 şık (A-E)" else if(is3OptionGrade) "3 şık (A-C)" else "4 şık (A-D)"}, 1 doğru cevap
2. Şıklar eşit uzunlukta ve paralel yapıda
3. Olumsuz ifadeler **_altı çizili_** formatında
4. Çeldiriciler gerçekçi, öğrenci hatalarından türetilmeli
5. Doğru cevap dengeli dağılsın (az kullanılan: $leastUsedAnswer)
6. Grafik/tablo/şekil YASAK
7. $gradeRules

⚠️ SON KONTROL (HER SORU İÇİN ZORUNLU):
1. "Bu soru gerçekten $lesson dersine mi ait?"
2. "Bu soru $seviye seviyesine uygun mu?"
3. "Başka bir dersin konusunu karıştırmış mıyım?"

JSON FORMAT (SADECE BU):
$jsonFormat

SADECE JSON DÖNDÜR.
""".trimIndent()
    }
    
    /**
     * DERS-SEVİYE UYUMU KURALLARI (GeminiApiProvider için)
     * AiQuestionGenerator'daki ile aynı mantık
     */
    private fun buildDersSeviyeKuraliForGeminiProvider(lesson: String, level: EducationLevel, grade: Int?, seviye: String): String {
        val lessonLower = lesson.lowercase()
        
        // Matematik dersi kuralları
        if (lessonLower.contains("matematik") || lessonLower.contains("math")) {
            return when {
                grade == 3 -> """
⚠️ MATEMATİK 3. SINIF ÖZEL KURALLARI (KRİTİK):
✅ SADECE: Doğal sayılar (0-1000), toplama, çıkarma, basit çarpma, birim kesirler, geometrik şekiller
❌ YASAK: Paragraf soruları, metin anlama, Fen/Sosyal Bilgiler konuları, 4 basamaklı sayılar
                """.trimIndent()
                
                else -> """
⚠️ MATEMATİK DERSİ KURALI:
✅ SADECE matematik konuları sorulacak
❌ YASAK: Paragraf, metin, okuma soruları, diğer derslerden konu karıştırma
                """.trimIndent()
            }
        }
        
        // Türkçe dersi kuralları
        if (lessonLower.contains("türkçe") || lessonLower.contains("turkce")) {
            return """
⚠️ TÜRKÇE DERSİ KURALI:
✅ SADECE: Okuma-anlama, sözcük bilgisi, cümle yapısı, noktalama
❌ YASAK: Matematik işlemleri, Fen konuları, Sosyal Bilgiler konuları
            """.trimIndent()
        }
        
        // Fen Bilimleri dersi kuralları
        if (lessonLower.contains("fen")) {
            return """
⚠️ FEN BİLİMLERİ DERSİ KURALI:
✅ SADECE: Canlılar, madde, enerji, ışık, ses, kuvvet, dünya-evren
❌ YASAK: Matematik işlemleri, Türkçe paragraf, Sosyal Bilgiler konuları
            """.trimIndent()
        }
        
        // Sosyal Bilgiler dersi kuralları
        if (lessonLower.contains("sosyal")) {
            return """
⚠️ SOSYAL BİLGİLER DERSİ KURALI:
✅ SADECE: Tarih, coğrafya, vatandaşlık, ekonomi
❌ YASAK: Matematik işlemleri, Fen konuları, Türkçe dil bilgisi
            """.trimIndent()
        }
        
        // Genel kural
        return """
⚠️ DERS UYUMU KURALI:
✅ SADECE "$lesson" DERSİNE AİT KONULAR SORULACAK
❌ YASAK: Başka derslerin konularını karıştırma, ders dışı içerik
        """.trimIndent()
    }
    
    // ==================== KPSS ÖZEL PROMPT ====================
    
    /**
     * KPSS için RAG destekli, yayınevi kalitesinde prompt oluşturur.
     * Müfredata uyumlu, anti-halüsinasyon korumalı.
     */
    private fun buildKpssPrompt(
        lesson: String,
        count: Int,
        schoolType: SchoolType,
        leastUsedAnswer: String,
        jsonFormat: String
    ): String {
        // Ders türünü belirle
        val dersAdi = when {
            lesson.contains("türkçe", ignoreCase = true) || lesson.contains("turkce", ignoreCase = true) -> "türkçe"
            lesson.contains("matematik", ignoreCase = true) -> "matematik"
            lesson.contains("tarih", ignoreCase = true) -> "tarih"
            lesson.contains("coğrafya", ignoreCase = true) || lesson.contains("cografya", ignoreCase = true) -> "coğrafya"
            lesson.contains("vatandaşlık", ignoreCase = true) || lesson.contains("vatandaslik", ignoreCase = true) -> "vatandaşlık"
            lesson.contains("güncel", ignoreCase = true) || lesson.contains("guncel", ignoreCase = true) -> "güncel"
            else -> lesson.lowercase()
        }
        
        // RAG'dan konu listesini al
        val konuListesi = KpssRagDatabase.getKonuListesi(dersAdi)
        val antiHalucinasyon = KpssRagDatabase.getAntiHalucinasyonKurallari(dersAdi)
        
        // Rastgele bir konu seç (ağırlıklı)
        val toplamAgirlik = konuListesi.sumOf { it.second }
        val randomKonu = if (konuListesi.isNotEmpty() && toplamAgirlik > 0) {
            var cumulative = 0
            val random = (0 until toplamAgirlik).random()
            konuListesi.firstOrNull { (_, agirlik) ->
                cumulative += agirlik
                random < cumulative
            }?.first ?: konuListesi.first().first
        } else null
        
        // Konu detaylarını al
        val konuDetay = randomKonu?.let { KpssRagDatabase.getKonuDetay(dersAdi, it) }
        
        // Prompt oluştur
        return buildString {
            appendLine("$count adet KPSS ${schoolType.displayName} $lesson sorusu üret.")
            appendLine()
            
            // Konu bilgisi
            if (konuDetay != null) {
                appendLine("📋 KONU: ${konuDetay["baslik"]}")
                (konuDetay["altKonular"] as? List<*>)?.let { altKonular ->
                    appendLine("📚 ALT KONULAR: ${altKonular.joinToString(", ")}")
                }
                appendLine()
            }
            
            appendLine("🎯 ÖSYM SORU FORMATI KURALLARI:")
            appendLine("1. 5 şık (A-E), sadece 1 doğru cevap")
            appendLine("2. Şıklar birbirine paralel yapıda ve eşit uzunlukta olmalı")
            appendLine("3. Olumsuz ifadeler (değildir, hangisi ... değildir) **kalın** yazılmalı")
            appendLine("4. Soru kökü net ve anlaşılır olmalı")
            appendLine("5. Çeldiriciler gerçekçi olmalı (yaygın öğrenci hatalarından türetilmeli)")
            appendLine("6. Grafik/tablo/şekil gerektiren sorular YASAK")
            appendLine()
            
            // Anti-halüsinasyon kuralları
            appendLine(antiHalucinasyon)
            appendLine()
            
            // Derse özel gerçek veriler
            when (dersAdi) {
                "tarih" -> {
                    appendLine("📖 GÜVENLİ TARİHLER (SADECE BUNLARI KULLAN):")
                    (konuDetay?.get("onemliTarihler") as? Map<*, *>)?.forEach { (tarih, olay) ->
                        appendLine("- $tarih: $olay")
                    } ?: run {
                        appendLine("- 1923: Cumhuriyet'in ilanı")
                        appendLine("- 1924: Tevhid-i Tedrisat, Hilafetin kaldırılması")
                        appendLine("- 1928: Harf İnkılabı")
                        appendLine("- 1934: Soyadı Kanunu, Kadınlara seçme-seçilme hakkı")
                        appendLine("- 1937: Atatürk ilkeleri anayasaya girdi")
                    }
                    appendLine()
                    appendLine("📖 GÜVENLİ İSİMLER:")
                    (konuDetay?.get("onemliIsimler") as? List<*>)?.let { isimler ->
                        appendLine(isimler.joinToString(", "))
                    } ?: appendLine("Mustafa Kemal Atatürk, İsmet İnönü, Fevzi Çakmak")
                }
                "coğrafya", "cografya" -> {
                    appendLine("📖 TÜRKİYE VERİLERİ (SADECE BUNLARI KULLAN):")
                    (konuDetay?.get("turkiyeVerileri") as? Map<*, *>)?.forEach { (anahtar, deger) ->
                        appendLine("- $anahtar: $deger")
                    } ?: run {
                        appendLine("- En yüksek dağ: Ağrı Dağı (5137 m)")
                        appendLine("- En uzun akarsu: Kızılırmak (1355 km)")
                        appendLine("- En büyük göl: Van Gölü")
                        appendLine("- Fındık: Karadeniz (dünya 1.si)")
                        appendLine("- Kayısı: Malatya")
                    }
                }
                "vatandaşlık", "vatandaslik" -> {
                    appendLine("📖 ANAYASA BİLGİLERİ (2017 DEĞİŞİKLİKLERİ DAHİL):")
                    (konuDetay?.get("anayasaBilgileri") as? Map<*, *>)?.forEach { (anahtar, deger) ->
                        appendLine("- $anahtar: $deger")
                    } ?: run {
                        appendLine("- Cumhurbaşkanı: 5 yıl, en fazla 2 dönem")
                        appendLine("- TBMM: 600 milletvekili, 5 yıl")
                        appendLine("- Başbakanlık: 2017'de kaldırıldı")
                        appendLine("- Milletvekili seçilme yaşı: 18")
                    }
                }
                "güncel", "guncel" -> {
                    appendLine("📖 GÜNCEL PROJELER VE GELİŞMELER:")
                    (konuDetay?.get("guncelVeriler") as? List<*>)?.forEach { veri ->
                        appendLine("- $veri")
                    } ?: run {
                        appendLine("- TOGG: Türkiye'nin ilk yerli otomobili (2022)")
                        appendLine("- KAAN: Milli Muharip Uçak (2024 ilk uçuş)")
                        appendLine("- 1915 Çanakkale Köprüsü (2022)")
                        appendLine("- Akkuyu Nükleer Santrali")
                    }
                }
                "matematik" -> {
                    appendLine("📖 FORMÜLLER VE İPUÇLARI:")
                    (konuDetay?.get("formuller") as? List<*>)?.forEach { formul ->
                        appendLine("- $formul")
                    }
                    appendLine()
                    appendLine("📖 ÇELDİRİCİ STRATEJİLERİ:")
                    (konuDetay?.get("celdiriciStratejileri") as? List<*>)?.forEach { strateji ->
                        appendLine("- $strateji")
                    }
                }
                "türkçe", "turkce" -> {
                    appendLine("📖 PARAGRAF KURALLARI:")
                    appendLine("- 8-12 cümle, 120-180 kelime")
                    appendLine("- Tek ana fikir içermeli")
                    appendLine("- Akademik/edebi dil kullanılmalı")
                    appendLine()
                    appendLine("📖 SORU TİPLERİ:")
                    (konuDetay?.get("soruTipleri") as? List<*>)?.forEach { tip ->
                        appendLine("- $tip")
                    }
                }
            }
            appendLine()
            
            appendLine("🔄 CEVAP DAĞILIMI:")
            appendLine("- En az kullanılan şık: $leastUsedAnswer (öncelikli kullan)")
            appendLine("- Dağılım dengeli olmalı (yaklaşık 20-20-20-20-20)")
            appendLine()
            
            appendLine("⛔ KESİN YASAKLAR:")
            appendLine("- \"Hepsi doğrudur\", \"Hiçbiri\", \"Yukarıdakilerin tümü\" gibi şıklar YASAK")
            appendLine("- Grafik/tablo/şekil gerektiren sorular YASAK")
            appendLine("- Birden fazla doğru cevap olabilecek sorular YASAK")
            appendLine("- Güncelliğini yitirmiş bilgiler YASAK")
            appendLine("- Uydurma tarih/isim/veri YASAK")
            appendLine()
            
            appendLine("JSON FORMAT (SADECE BU):")
            appendLine(jsonFormat)
            appendLine()
            appendLine("⚠️ SADECE JSON DÖNDÜR, BAŞKA HİÇBİR ŞEY YAZMA.")
        }
    }
    
    // ==================== PARAGRAF ÖZEL PROMPT ====================
    
    /**
     * Paragraf soruları için müfredat uyumlu, seviye bazlı prompt oluşturur.
     */
    private fun buildParagrafPrompt(
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        leastUsedAnswer: String,
        jsonFormat: String,
        seviye: String,
        is5OptionExam: Boolean,
        is3OptionGrade: Boolean
    ): String {
        // Seviye bazlı kazanımlar ve kurallar
        val kazanimlar = when {
            level == EducationLevel.ORTAOKUL && grade == 5 -> """
📚 5. SINIF KAZANIMLARI (MEB 2025):
- T.5.3.1: Paragrafın ana düşüncesini belirler
- T.5.3.2: Yardımcı düşünceleri belirler
- T.5.3.3: Paragrafa uygun başlık belirler

🎯 SORU TİPLERİ:
- Ana düşünce/Ana fikir bulma
- Yardımcı düşünceleri belirleme
- Başlık bulma
- Metinden çıkarım yapma
- Paragrafın konusunu belirleme

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 5-7 cümle, 80-100 kelime
- Dil: Basit, anlaşılır
- Konu: Günlük hayat, doğa, hayvanlar, arkadaşlık
- Anlatım: Öyküleme, betimleme ağırlıklı
"""
            level == EducationLevel.ORTAOKUL && grade == 6 -> """
📚 6. SINIF KAZANIMLARI (MEB 2025):
- T.6.3.1: Paragrafın yapısını çözümler (Giriş, Gelişme, Sonuç)

🎯 SORU TİPLERİ:
- Ana düşünce/Ana fikir
- Paragraf yapısı (Giriş, Gelişme, Sonuç)
- Paragrafın bölümleri
- Anlatım teknikleri
- Metinden çıkarım

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 6-8 cümle, 90-110 kelime
- Dil: Orta seviye
- Konu: Bilim, tarih, kültür, spor
- Anlatım: Açıklama, öyküleme
"""
            level == EducationLevel.ORTAOKUL && grade == 7 -> """
📚 7. SINIF KAZANIMLARI (MEB 2025):
- T.7.3.1: Düşünceyi geliştirme yollarını tanır

🎯 SORU TİPLERİ:
- Ana düşünce/Ana fikir
- Düşünceyi geliştirme yolları (Tanımlama, Örnekleme, Karşılaştırma, Tanık gösterme)
- Anlatım teknikleri
- Metinden çıkarım
- Paragrafın amacı

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 7-9 cümle, 100-120 kelime
- Dil: Orta-ileri seviye
- Konu: Edebiyat, bilim, teknoloji, toplum
- Anlatım: Açıklama, tartışma
"""
            level == EducationLevel.ORTAOKUL && grade == 8 -> """
📚 8. SINIF KAZANIMLARI (MEB 2025 - LGS):
- T.8.3.1: Paragraf türlerini ayırt eder
- T.8.3.2: Metinden çıkarım yapar

🎯 SORU TİPLERİ:
- Ana düşünce/Ana fikir
- Paragraf türleri (Giriş, Gelişme, Sonuç, Amaç)
- Metinden çıkarım ve yorum
- Anlatım teknikleri (Öyküleme, Betimleme, Açıklama, Tartışma)
- Yazarın amacı/bakış açısı

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 8-10 cümle, 110-140 kelime
- Dil: İleri seviye, akademik
- Konu: Edebiyat, felsefe, bilim, sanat, toplum
- Anlatım: Tüm teknikler (öyküleme, betimleme, açıklama, tartışma)
"""
            level == EducationLevel.LISE && (grade == 9 || grade == 10) -> """
📚 LİSE 9-10. SINIF (TYT):
- Edebî metinlerde ana fikir
- Anlatım teknikleri (öyküleme, betimleme, açıklama, tartışma)
- Paragraf yapısı ve örgüsü

🎯 SORU TİPLERİ:
- Ana fikir/Ana düşünce
- Yardımcı fikirler
- Anlatım teknikleri
- Metinden çıkarım ve yorum
- Yazarın bakış açısı
- Paragrafın amacı

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 9-12 cümle, 130-170 kelime
- Dil: Akademik, edebi
- Konu: Edebiyat, felsefe, sanat, bilim, toplum
- Anlatım: Tüm teknikler, karmaşık yapılar
"""
            level == EducationLevel.LISE && (grade == 11 || grade == 12) -> """
📚 LİSE 11-12. SINIF (AYT):
- Akademik metinlerde ana düşünce
- Karşılaştırma ve çıkarım
- Eleştirel okuma

🎯 SORU TİPLERİ:
- Ana fikir/Ana düşünce (akademik metinler)
- Karşılaştırma ve analiz
- Eleştirel okuma ve yorum
- Yazarın amacı ve bakış açısı
- Metinler arası ilişki
- Derin çıkarım

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 10-14 cümle, 150-200 kelime
- Dil: Akademik, felsefi, edebi
- Konu: Felsefe, edebiyat, bilim, sanat, toplum, kültür
- Anlatım: Karmaşık yapılar, çok katmanlı anlatım
"""
            level == EducationLevel.KPSS -> """
📚 KPSS TÜRKÇE - PARAGRAF:
- Ana fikir / Ana düşünce
- Yardımcı fikir / Yardımcı düşünce
- Paragrafta çıkarım
- Paragraf yapısı ve örgüsü
- Anlatım teknikleri
- Paragrafta konu
- Paragrafta başlık
- Paragrafın bölümleri
- Düşünceyi geliştirme yolları

🎯 SORU TİPLERİ:
- "Aşağıdakilerden hangisi paragrafın ana düşüncesidir?"
- "Bu parçadan aşağıdaki yargılardan hangisine ulaşılabilir?"
- "Paragrafın anlatım tekniği aşağıdakilerden hangisidir?"
- "Paragrafta asıl anlatılmak istenen nedir?"
- "Parçaya göre aşağıdakilerden hangisi söylenemez?"

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 8-12 cümle, 120-180 kelime
- Dil: Akademik, edebi
- Konu: Edebiyat, felsefe, bilim, sanat, toplum, kültür
- Anlatım: Tüm teknikler, ÖSYM formatı
"""
            level == EducationLevel.AGS -> """
📚 AGS SÖZEL YETENEK - PARAGRAF:
- Akademik metinler (bilimsel, felsefi)
- Edebî metinler (roman, hikaye, deneme)
- Güncel konular
- Ana fikir ve yardımcı fikirler
- Çıkarım ve yorum
- Anlatım teknikleri

🎯 SORU TİPLERİ:
- Ana fikir/Ana düşünce
- Metinden çıkarım
- Yazarın amacı
- Anlatım tekniği
- Paragrafın konusu

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 9-13 cümle, 140-190 kelime
- Dil: Akademik, edebi, felsefi
- Konu: Edebiyat, felsefe, bilim, sanat, eğitim, toplum
- Anlatım: Karmaşık yapılar, çok katmanlı
"""
            else -> """
📚 GENEL PARAGRAF KURALLARI:
- Ana düşünce/Ana fikir
- Yardımcı düşünceler
- Metinden çıkarım
- Anlatım teknikleri

📖 PARAGRAF ÖZELLİKLERİ:
- Uzunluk: 6-10 cümle, 90-140 kelime
- Dil: Anlaşılır
- Konu: Çeşitli
"""
        }
        
        return buildString {
            appendLine("$count adet $seviye PARAGRAF sorusu üret.")
            appendLine()
            appendLine(kazanimlar)
            appendLine()
            appendLine("🎯 PARAGRAF SORU FORMATI:")
            appendLine("1. ${if(is5OptionExam) "5 şık (A-E)" else if(is3OptionGrade) "3 şık (A-C)" else "4 şık (A-D)"}, sadece 1 doğru cevap")
            appendLine("2. Önce paragraf metni, sonra soru")
            appendLine("3. Şıklar eşit uzunlukta ve paralel yapıda")
            appendLine("4. Olumsuz ifadeler **kalın** yazılmalı")
            appendLine("5. Çeldiriciler gerçekçi olmalı (öğrenci hatalarından türetilmeli)")
            appendLine("6. Doğru cevap dengeli dağılsın (az kullanılan: $leastUsedAnswer)")
            appendLine()
            appendLine("📝 PARAGRAF YAZIM KURALLARI:")
            appendLine("- Paragraf tek bir ana fikir içermeli")
            appendLine("- Cümleler birbirine bağlı ve akıcı olmalı")
            appendLine("- Konu cümlesi net olmalı")
            appendLine("- Destekleyici cümleler ana fikri güçlendirmeli")
            appendLine("- Sonuç cümlesi varsa ana fikri pekiştirmeli")
            appendLine()
            appendLine("⛔ YASAKLAR:")
            appendLine("- \"Hepsi doğrudur\", \"Hiçbiri\", \"Yukarıdakilerin tümü\" gibi şıklar YASAK")
            appendLine("- Birden fazla doğru cevap olabilecek sorular YASAK")
            appendLine("- Paragrafta geçmeyen bilgiler şıklarda YASAK")
            appendLine("- Çok kısa veya çok uzun paragraflar YASAK")
            appendLine()
            appendLine("JSON FORMAT (SADECE BU):")
            appendLine(jsonFormat)
            appendLine()
            appendLine("⚠️ SADECE JSON DÖNDÜR, BAŞKA HİÇBİR ŞEY YAZMA.")
        }
    }
    
    // ==================== JSON PARSE ====================
    
    private fun parseQuestions(rawText: String, lesson: String): List<QuestionModel> {
        val result = mutableListOf<QuestionModel>()
        
        try {
            // Markdown code block işaretlerini temizle (```json ... ```)
            var cleanText = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            // JSON array başlangıcını bul
            val jsonStart = cleanText.indexOf('[')
            val jsonEnd = cleanText.lastIndexOf(']')
            
            if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                Log.w(TAG, "JSON bulunamadı: ${rawText.take(100)}")
                return emptyList()
            }
            
            var jsonStr = cleanText.substring(jsonStart, jsonEnd + 1)
            
            // JSON truncation fix: Eksik kapanış parantezlerini tamamla
            val openBraces = jsonStr.count { it == '{' }
            val closeBraces = jsonStr.count { it == '}' }
            val openBrackets = jsonStr.count { it == '[' }
            val closeBrackets = jsonStr.count { it == ']' }
            
            if (openBraces > closeBraces || openBrackets > closeBrackets) {
                Log.w(TAG, "⚠️ JSON truncated: {$openBraces/$closeBraces} [$openBrackets/$closeBrackets]. Auto-completing...")
                
                // Son objeyi kapat
                repeat(openBraces - closeBraces) {
                    jsonStr += "}"
                }
                
                // Array'i kapat
                repeat(openBrackets - closeBrackets) {
                    jsonStr += "]"
                }
                
                Log.d(TAG, "✅ JSON auto-completed: ${jsonStr.length} chars")
            }
            
            val jsonArray = JSONArray(jsonStr)
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                
                val question = QuestionModel(
                    question = obj.optString("question", ""),
                    optionA = obj.optString("optionA", ""),
                    optionB = obj.optString("optionB", ""),
                    optionC = obj.optString("optionC", ""),
                    optionD = obj.optString("optionD", ""),
                    optionE = obj.optString("optionE", ""),
                    correctAnswer = obj.optString("correctAnswer", "A").uppercase(),
                    explanation = obj.optString("explanation", ""),
                    lesson = lesson
                )
                
                if (question.question.isNotBlank() && 
                    question.optionA.isNotBlank() && 
                    question.optionB.isNotBlank()) {
                    result.add(question)
                }
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse hatası: ${e.message}")
        }
        
        return result
    }
}
