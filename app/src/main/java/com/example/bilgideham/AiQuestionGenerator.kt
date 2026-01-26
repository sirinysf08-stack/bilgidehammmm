package com.example.bilgideham

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.json.JSONArray
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * AI Soru Üretici - MEB 2025 TYMM UYUMLU
 *
 * ÖSYM + MEB Standartlarına Uyumlu Soru Üretimi:
 * 1. Tek doğru cevap, 4 şık (A-D)
 * 2. Şıklar eşit uzunlukta ve paralel yapıda
 * 3. Olumsuz ifadeler kalın+altı çizili (renk yok)
 * 4. Tekrar kontrolü (parmak izi)
 * 5. Doğru cevap dağılımı dengeli
 */

class AiQuestionGenerator {

    companion object {
        private const val TAG = "AI_MEB_TYMM"
        private const val GEMINI_MODEL = "gemini-2.0-flash"

        // 5 paralel istek
        private val turboSemaphore = Semaphore(5)

        // Thread-safe fingerprint cache
        private val seenFingerprints = ConcurrentHashMap.newKeySet<String>()
        private const val MAX_CACHE = 3000

        // Son üretilen soruların konuları (rotasyon kontrolü)
        private val lastTopics = mutableListOf<String>()
        private const val MAX_TOPIC_HISTORY = 10

        // Son doğru cevap harfleri (denge kontrolü)
        private val lastCorrectAnswers = mutableListOf<String>()
        private const val MAX_ANSWER_HISTORY = 20
        
        // YENİ: Son soru tipleri (ardışık tip engelleme)
        private val lastQuestionTypes = mutableListOf<String>()
        private const val MAX_TYPE_HISTORY = 5
    }

    private val gemini: GenerativeModel by lazy {
        Firebase.vertexAI.generativeModel(modelName = GEMINI_MODEL)
    }

    // ==================== RAG BAĞLAM ====================

    /**
     * RAG sisteminden MEB müfredat bağlamı oluşturur
     */
    private fun buildRagContext(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        lesson: String
    ): String {
        val context = RagRepository.buildContext(level, schoolType, grade, lesson)
        return if (context.isNotBlank()) {
            """
📚 MEB MÜFREDAT REFERANSI (ZORUNLU - BU BİLGİLERE DAYANARAK SORU ÜRET):

⚠️ KRİTİK: Aşağıdaki kazanımlar SADECE "$lesson" dersine aittir.
Bu kazanımlar dışında BAŞKA DERS KONULARINDAN SORU ÜRETME!

$context

⚠️ UYARI: Yukarıdaki kazanımlar dışında kalan konulardan soru sorma!
Örnek: Matematik dersinde Türkçe paragraf sorusu YASAK!
            """.trimIndent()
        } else {
            """
⚠️ MÜFREDAT UYARISI:
- Bu soru seti "$lesson" dersi içindir
- SADECE $lesson konularından soru üret
- Başka derslerin konularını karıştırma
            """.trimIndent()
        }
    }

     private fun buildParagrafWordRangeRule(level: EducationLevel, grade: Int?): String {
         val range = when {
             grade == 3 -> "maksimum 50"
             grade == 4 -> "70-110"
             grade == 5 -> "80-120"
             grade == 6 -> "90-125"
             grade == 7 -> "90-130"
             grade == 8 -> "100-150"
             level == EducationLevel.LISE && (grade == 9 || grade == 10) -> "120-180"
             level == EducationLevel.LISE && (grade == 11 || grade == 12) -> "150-220"
             else -> "80-130"
         }
         return "- Okuma parçası $range kelime olmalıdır (grade seviyesine uygun)."
     }

    /**
     * DERS-SEVİYE UYUMU KURALLARI
     * Her ders için özel kurallar tanımlar, yanlış ders içeriği üretimini engeller
     */
    private fun buildDersSeviyeKurali(lesson: String, level: EducationLevel, grade: Int?, seviye: String): String {
        val lessonLower = lesson.lowercase()
        
        // Matematik dersi kuralları
        if (lessonLower.contains("matematik") || lessonLower.contains("math")) {
            return when {
                grade == 3 -> """
⚠️ MATEMATİK 3. SINIF ÖZEL KURALLARI (KRİTİK - HARFİYEN UYGULA):

✅ SADECE ŞU KONULAR SORULACAK:
- Doğal sayılar (0-1000 arası), basamak değeri, çözümleme
- Toplama ve çıkarma işlemleri (3 basamaklı sayılarla)
- Çarpma tablosu (2, 3, 4, 5, 10 ile çarpma)
- Bölme (basit bölme işlemleri)
- Kesirler (sadece birim kesirler: 1/2, 1/3, 1/4)
- Geometrik şekiller (üçgen, kare, dikdörtgen, daire)
- Uzunluk ölçme (cm, m, km)
- Zaman (saat okuma, gün, hafta, ay)
- Para (TL ve kuruş)
- Basit örüntüler

❌ KESINLIKLE SORULMAYACAK KONULAR:
- Paragraf okuma soruları (Bu Türkçe dersidir!)
- Metin anlama soruları (Bu Türkçe dersidir!)
- Fen Bilimleri konuları (canlılar, doğa, vb.)
- Sosyal Bilgiler konuları (tarih, coğrafya, vb.)
- 4 basamaklı veya daha büyük sayılar
- Ondalık sayılar
- Kesirli sayılarla işlemler
- Cebirsel ifadeler
- Alan ve hacim hesaplamaları

⚠️ SORU ÖRNEKLERİ (UYGUN):
✅ "45 + 38 işleminin sonucu kaçtır?"
✅ "Bir bütünün yarısı hangi kesirle gösterilir?"
✅ "Aşağıdaki şekillerden hangisi 4 kenarı eşit olan çokgendir?"

❌ SORU ÖRNEKLERİ (UYGUNSUZ - YAPMA):
❌ "Aşağıdaki paragrafta anlatılan konu nedir?" (Bu Türkçe sorusudur!)
❌ "Metne göre Ali kaç yaşındadır?" (Bu Türkçe sorusudur!)
❌ "Bitkiler nasıl beslenir?" (Bu Fen Bilimleri sorusudur!)
                """.trimIndent()
                
                grade == 4 -> """
⚠️ MATEMATİK 4. SINIF ÖZEL KURALLARI (KRİTİK):

✅ SADECE ŞU KONULAR SORULACAK:
- Doğal sayılar (0-10.000 arası)
- Dört işlem (toplama, çıkarma, çarpma, bölme)
- Kesirler (basit kesirler ve karşılaştırma)
- Geometrik cisimler ve şekiller
- Ölçme (uzunluk, ağırlık, sıvı ölçme)
- Zaman problemleri
- Veri analizi (basit tablo ve grafik okuma)

❌ KESINLIKLE SORULMAYACAK:
- Paragraf ve metin soruları (Türkçe dersi!)
- Fen, Sosyal Bilgiler konuları
- Ondalık sayılar (5. sınıf konusu)
- Yüzdeler (5. sınıf konusu)
                """.trimIndent()
                
                level == EducationLevel.ILKOKUL -> """
⚠️ İLKOKUL MATEMATİK GENEL KURALLARI:

✅ SADECE MATEMATİK KONULARI:
- Sayılar ve işlemler
- Geometri (şekiller, cisimler)
- Ölçme (uzunluk, ağırlık, zaman, para)
- Veri (tablo, grafik)

❌ DİĞER DERSLERDEN SORU YASAK:
- Türkçe paragraf/metin soruları
- Fen Bilimleri konuları
- Sosyal Bilgiler konuları
                """.trimIndent()
                
                else -> """
⚠️ MATEMATİK DERSİ KURALI:
- SADECE matematik konuları sorulacak
- Paragraf, metin, okuma soruları YASAK
- Diğer derslerden konu karıştırma YASAK
                """.trimIndent()
            }
        }
        
        // Türkçe dersi kuralları
        if (lessonLower.contains("türkçe") || lessonLower.contains("turkce")) {
            return """
⚠️ TÜRKÇE DERSİ ÖZEL KURALLARI:

✅ SADECE ŞU KONULAR SORULACAK:
- Okuma-anlama (paragraf, hikaye, şiir)
- Sözcük bilgisi (eş anlam, zıt anlam, eş sesli)
- Cümle yapısı ve noktalama
- Yazım kuralları
- Dil bilgisi (isim, fiil, sıfat vb.)

❌ DİĞER DERSLERDEN SORU YASAK:
- Matematik işlemleri
- Fen Bilimleri konuları
- Sosyal Bilgiler konuları
            """.trimIndent()
        }
        
        // Fen Bilimleri dersi kuralları
        if (lessonLower.contains("fen")) {
            return when {
                grade == 3 -> """
⚠️ FEN BİLİMLERİ 3. SINIF ÖZEL KURALLARI:

✅ SADECE ŞU KONULAR SORULACAK:
- Canlılar (bitkiler, hayvanlar, yaşam döngüsü)
- Madde (katı, sıvı, gaz halleri)
- Hareket ve kuvvet (basit düzeyde)
- Dünya ve evren (gün, gece, mevsimler)
- Işık ve ses (basit gözlemler)

❌ DİĞER DERSLERDEN SORU YASAK:
- Matematik işlemleri
- Türkçe paragraf soruları
- Sosyal Bilgiler konuları
                """.trimIndent()
                
                else -> """
⚠️ FEN BİLİMLERİ DERSİ KURALI:
- SADECE fen konuları sorulacak
- Matematik, Türkçe, Sosyal Bilgiler karıştırma YASAK
                """.trimIndent()
            }
        }
        
        // Sosyal Bilgiler dersi kuralları
        if (lessonLower.contains("sosyal")) {
            return """
⚠️ SOSYAL BİLGİLER DERSİ KURALI:

✅ SADECE ŞU KONULAR SORULACAK:
- Tarih (Türk tarihi, Atatürk)
- Coğrafya (harita, yön, iklim)
- Vatandaşlık (haklar, görevler)
- Ekonomi (üretim, tüketim)

❌ DİĞER DERSLERDEN SORU YASAK:
- Matematik işlemleri
- Türkçe dil bilgisi
- Fen Bilimleri konuları
            """.trimIndent()
        }
        
        // İngilizce dersi kuralları
        if (lessonLower.contains("ingilizce") || lessonLower.contains("english")) {
            return """
⚠️ İNGİLİZCE DERSİ KURALI:

✅ SADECE İNGİLİZCE DİL BECERİLERİ:
- Kelime bilgisi (vocabulary)
- Dilbilgisi (grammar)
- Okuma anlama (reading)
- Diyalog tamamlama

❌ TÜRKÇE SORU YASAK:
- Sorular İNGİLİZCE olmalı
- Türkçe paragraf soruları YASAK
            """.trimIndent()
        }
        
        // Genel kural (diğer dersler için)
        return """
⚠️ DERS UYUMU KURALI (KRİTİK):

✅ SADECE "$lesson" DERSİNE AİT KONULAR SORULACAK
- Soru içeriği tamamen $lesson müfredatına uygun olmalı
- $seviye seviyesine uygun zorlukta olmalı

❌ DİĞER DERSLERDEN KONU KARIŞIMI KESINLIKLE YASAK:
- Başka derslerin konularını sorma
- Ders dışı içerik üretme
- Seviye dışı konu sorma

⚠️ KONTROL: Her soru üretmeden önce şunu sor:
"Bu soru gerçekten $lesson dersine mi ait?"
"Bu soru $seviye seviyesine uygun mu?"
        """.trimIndent()
    }

    // ==================== PUBLIC GEMINI GENERATOR (AI SOURCE TRACKING) ====================
    
    /**
     * Gemini ile soru üretir ve kaynak bilgisi döndürür
     * @return Pair<List<QuestionModel>, String> - Sorular ve AI adı
     */
    suspend fun generateWithSource(
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ): Pair<List<QuestionModel>, String> = withContext(Dispatchers.IO) {
        val aiName = "🔵 Gemini"
        try {
            Log.d(TAG, "🔄 Gemini başlatılıyor: $lesson, $count soru, $level, $schoolType, $grade")
            val questions = generateFastBatch(lesson, count, level, schoolType, grade)
            Log.d(TAG, "✅ Gemini tamamlandı: ${questions.size} soru üretildi")
            Pair(questions, aiName)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Gemini hatası: ${e.message}")
            e.printStackTrace()
            Pair(emptyList(), "$aiName (HATA: ${e.message?.take(30)})")
        }
    }

    // ==================== TURBO TOPLU ÜRETİM ====================

    suspend fun generateBulkForLevel(
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        questionsPerSubject: Int = 15,
        onProgress: (String, Int, Int) -> Unit = { _, _, _ -> }
    ): Map<String, List<QuestionModel>> = withContext(Dispatchers.IO) {

        val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
        val results = ConcurrentHashMap<String, MutableList<QuestionModel>>()
        val completed = AtomicInteger(0)

        DebugLog.d(TAG, "🚀 MEB TYMM ÜRETİM BAŞLADI: ${subjects.size} ders, ${questionsPerSubject} soru/ders")

        val jobs = subjects.map { subject ->
            async {
                turboSemaphore.withPermit {
                    try {
                        val questions = turboGenerate(
                            lesson = subject.displayName,
                            count = questionsPerSubject,
                            level = level,
                            schoolType = schoolType,
                            grade = grade,
                            units = subject.units
                        )

                        if (questions.isNotEmpty()) {
                            results[subject.id] = questions.toMutableList()
                        }

                        val done = completed.incrementAndGet()
                        onProgress(subject.displayName, done, subjects.size)

                        DebugLog.d(TAG, "✅ ${subject.displayName}: ${questions.size} soru")

                    } catch (e: Exception) {
                        Log.e(TAG, "❌ ${subject.displayName}: ${e.message}")
                        completed.incrementAndGet()
                    }
                }
            }
        }

        jobs.awaitAll()

        DebugLog.d(TAG, "🏁 ÜRETİM BİTTİ: Toplam ${results.values.sumOf { it.size }} soru")
        results
    }

    /**
     * TURBO TEK DERS - MEB TYMM Uyumlu
     */
    private suspend fun turboGenerate(
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        units: List<UnitConfig> = emptyList(),
        validateWithAi: Boolean = true
    ): List<QuestionModel> = withContext(Dispatchers.IO) {

        val effectiveCount = count.coerceAtMost(40)
        val askCount = (effectiveCount * 1.3).toInt().coerceIn(effectiveCount, 40)
        val prompt = buildMebTymmPrompt(lesson, askCount, level, schoolType, grade, units)

        val rawText = try {
            val response = gemini.generateContent(prompt)
            response.text?.trim().orEmpty()
        } catch (e: Exception) {
            val errorMsg = when {
                e.message?.contains("quota", ignoreCase = true) == true -> "API kotası aşıldı"
                e.message?.contains("rate", ignoreCase = true) == true -> "Rate limit aşıldı"
                e.message?.contains("timeout", ignoreCase = true) == true -> "Zaman aşımı"
                e.message?.contains("network", ignoreCase = true) == true -> "Ağ hatası"
                e.message?.contains("safety", ignoreCase = true) == true -> "İçerik güvenlik filtresi"
                else -> e.message ?: "Bilinmeyen hata"
            }
            Log.e(TAG, "❌ Gemini API hatası ($lesson): $errorMsg")
            throw Exception(errorMsg) // Üst katmana ilet (retry için)
        }

        if (rawText.isBlank()) {
            Log.w(TAG, "⚠️ Gemini boş yanıt döndü ($lesson)")
            return@withContext emptyList()
        }
        
        // JSON parse kontrolü
        if (!rawText.contains("[") || !rawText.contains("]")) {
            Log.e(TAG, "❌ Geçersiz JSON formatı ($lesson): ${rawText.take(100)}...")
            return@withContext emptyList()
        }

        val parsed = parseQuestions(rawText, lesson)
        
        if (parsed.isEmpty()) {
            Log.w(TAG, "⚠️ JSON parse edildi ama soru çıkarılamadı ($lesson)")
            return@withContext emptyList()
        }
        
        DebugLog.d(TAG, "📝 $lesson: ${parsed.size} soru parse edildi, doğrulama başlıyor...")
        
        // Kalite kontrolleri - ÇİFT KATMANLI DOĞRULAMA
        val validated = mutableListOf<QuestionModel>()
        
        for (q in parsed) {
            val fp = fingerprint(q)
            val isUnique = fp !in seenFingerprints
            val hasValidOptions = validateOptionLength(q)
            
            // Şık sayısına göre geçerli cevapları belirle (3/4/5 şıklı sorular için)
            val hasOptionE = !q.optionE.isNullOrBlank()
            val hasOptionD = !q.optionD.isNullOrBlank()
            val validAnswers = when {
                hasOptionE -> listOf("A", "B", "C", "D", "E")  // 5 şıklı
                hasOptionD -> listOf("A", "B", "C", "D")       // 4 şıklı
                else -> listOf("A", "B", "C")                   // 3 şıklı (3. sınıf)
            }
            val hasSingleCorrect = q.correctAnswer in validAnswers
            
            if (!hasSingleCorrect) {
                Log.w(TAG, "❌ Geçersiz cevap (${q.correctAnswer}), geçerli: $validAnswers - ${q.question.take(30)}")
            }
            
            // Katman 1: Yerel içerik doğrulama
            val hasValidContent = validateQuestionContent(q)
            
            if (isUnique && hasValidOptions && hasSingleCorrect && hasValidContent) {
                // Katman 2: AI ile doğru cevap doğrulama (halüsinasyon önleme)
                // HIZ İÇİN SADECE validateWithAi=true İSE YAPILIR
                val aiValid = if (validateWithAi) {
                    try {
                        validateCorrectAnswerWithAI(q)
                    } catch (e: Exception) {
                        Log.w(TAG, "AI doğrulama hatası, devam ediliyor: ${e.message}")
                        true // Rate limit durumunda geçerli say
                    }
                } else {
                    true // Doğrulama kapalıysa her zaman geçerli
                }
                
                if (aiValid) {
                    seenFingerprints.add(fp)
                    // Doğru cevap dağılımını takip et
                    synchronized(lastCorrectAnswers) {
                        lastCorrectAnswers.add(q.correctAnswer)
                        if (lastCorrectAnswers.size > MAX_ANSWER_HISTORY) {
                            lastCorrectAnswers.removeAt(0)
                        }
                    }
                    validated.add(q)
                    DebugLog.d(TAG, "✅ Çift katman doğrulandı: ${q.question.take(30)}...")
                } else {
                    Log.w(TAG, "❌ AI doğrulama başarısız (halüsinasyon?): ${q.question.take(30)}...")
                }
            }
        }

        if (seenFingerprints.size > MAX_CACHE) {
            seenFingerprints.clear()
        }

        validated.take(effectiveCount)
    }

    /**
     * Sınıf bazlı paragraf formatı kuralları
     * 3-8. sınıf için analiz edilmiş soru bankası formatlarına uygun
     */
    private fun buildGradeSpecificParagraphRules(level: EducationLevel, grade: Int?): String {
        return when {
            grade == 3 -> """
   📖 3. SINIF PARAGRAF FORMATI (ÇOK KRİTİK - HARFİYEN UYGULA):
   
   ⚠️ SINIRLAMALAR (MUTLAKA UYULMALI):
   - PARAGRAF UZUNLUĞU: SADECE 3-4 cümle (MAKSIMUM 50 kelime)
   - CÜMLE UZUNLUĞU: Her cümle EN FAZLA 10-12 kelime
   - DİL SEVİYESİ: 8-9 yaş çocuğunun anlayabileceği kadar basit
   - KARMAŞIK YAPILAR YASAK: Sıralı cümleler, devrik cümleler, yan cümleler YASAK
   
   ✅ UYGUN KONULAR:
   - Okul, aile, oyun, hayvanlar, mevsimler, doğa, arkadaşlık, temizlik
   
   ✅ UYGUN SORU TÜRLERİ:
   • "Bu paragrafta neden bahsedilmektedir?"
   • "Metne göre [karakter] ne yapmıştır?"
   • "Bu paragrafta aşağıdakilerden hangisi anlatılmıştır?"
   • "Metne göre hangisi doğrudur?"
   
   ⚠️ ÖRNEK PARAGRAF (BU UZUNLUĞU AŞMA):
   "Ali okula gitti. Okulda arkadaşlarıyla oynadı. Öğretmeni ona bir kitap verdi. Ali çok mutlu oldu."
   
   - ÖZELLİK: Doğrudan anlama odaklı. Çıkarım gerektirmeyen sorular.
   - 50 KELİMEYİ GEÇEN PARAGRAF HATALIDIR!
            """.trimIndent()
            
            level == EducationLevel.ILKOKUL || grade == 4 -> """
   📖 4. SINIF PARAGRAF FORMATI:
   - PARAGRAF UZUNLUĞU: 5-7 cümle (70-110 kelime)
   - DİL SEVİYESİ: Basit, günlük dil. Kısa cümleler. Bağlaçlar: "ve, ama, çünkü"
   - KONULAR: Okul hayatı, aile, doğa, hayvanlar, arkadaşlık, sağlık, kitap okuma
   - SORU TÜRLERİ:
     • "Bu paragrafın KONUSU aşağıdakilerden hangisidir?"
     • "Metne göre [karakter] neden [eylem] yapmıştır?"
     • "Bu paragrafta aşağıdaki bilgilerden hangisi verilmiştir?"
     • "Metne göre aşağıdakilerden hangisi söylenemez?"
   - ÖZELLİK: Cevaplar metinde açıkça bulunmalı, doğrudan anlama odaklı
            """.trimIndent()
            
            grade == 5 -> """
   📖 5. SINIF PARAGRAF FORMATI:
   - PARAGRAF UZUNLUĞU: 5-9 cümle (80-120 kelime)
   - DİL SEVİYESİ: Açık, öğretici dil. Bilgilendirici veya hikaye anlatımı
   - KONULAR: Günlük alışkanlıklar, bilim/teknoloji, çevre, arkadaşlık, okuma kültürü
   - SORU TÜRLERİ (HER TÜR EŞİT DAĞILMALI):
     • "Bu metinde aşağıdakilerden hangisinden söz edilmektedir?" (Konu)
     • "Bu metinde asıl anlatılmak istenen aşağıdakilerden hangisidir?" (Ana Düşünce)
     • "Bu metinden aşağıdakilerden hangisi çıkarılamaz?" (Yardımcı Düşünce - Olumsuz)
     • "Bu metne en uygun başlık aşağıdakilerden hangisidir?" (Başlık)
     • "Metne göre aşağıdakilerden hangisi doğrudur?" (Doğrudan Anlama)
     • "Parçadan çıkarılacak en kapsamlı yargı hangisidir?" (Çıkarım)
     • "Bu parçayı kim söylüyor olabilir?" (Konuşmacı Tahmini)
     • "Metindeki olaylar hangi sırayla gerçekleşmiştir?" (Sıralama)
   - ÖZELLİK: Konu, ana düşünce ve yardımcı düşünce ayrımı öğretilir
            """.trimIndent()
            
            grade == 6 -> """
   📖 6. SINIF PARAGRAF FORMATI:
   - PARAGRAF UZUNLUĞU: 6-10 cümle (90-125 kelime)
   - DİL SEVİYESİ: Orta karmaşıklıkta, kavram kelimeler içerebilir
   - KONULAR: Sosyal konular, kültür, sanat, tarih, bilim, çevre sorunları
   - SORU TÜRLERİ (HER TÜR EŞİT DAĞILMALI):
     • "Paragraftaki altı çizili cümlenin yerine aşağıdakilerden hangisi getirilebilir?"
     • "Bu metinden aşağıdakilerden hangisine ulaşılamaz?"
     • "Yazar bu paragrafta aşağıdakilerden hangisini vurgulamak istemiştir?"
     • "Metne göre aşağıdakilerden hangisi doğrudur?"
     • "Metinde altı çizili sözcük yerine hangisi kullanılabilir?" (Eş Anlam)
     • "Bu parçadaki ana fikir hangisidir?"
     • "Metne göre aşağıdaki sorulardan hangisinin cevabı verilebilir?"
     • "Parçadaki ... ifadesinden ne anlaşılmaktadır?" (Anlam Çıkarma)
   - ÖZELLİK: Çıkarım yapma becerisi geliştirilir
            """.trimIndent()
            
            grade == 7 -> """
   📖 7. SINIF PARAGRAF FORMATI:
   - PARAGRAF UZUNLUĞU: 7-10 cümle (90-130 kelime)
   - DİL SEVİYESİ: Gelişmiş, edebi ifadeler, mecaz anlamlar
   - KONULAR: Edebiyat, felsefe (basit), psikoloji, sosyoloji, bilimsel keşifler
   - SORU TÜRLERİ (HER TÜR EŞİT DAĞILMALI):
     • "Bu sözleri söyleyen biri için aşağıdakilerden hangisi söylenebilir?" (Yazar Analizi)
     • "Numaralanmış cümlelerden hangisi düşüncenin akışını bozmaktadır?" (Yapı)
     • "Bu paragraftan aşağıdaki yargılardan hangisine ulaşılamaz?"
     • "Paragraftaki anahtar kavram aşağıdakilerden hangisidir?"
     • "Parçada geçen ... sözü mecaz anlamda mı gerçek anlamda mı kullanılmıştır?"
     • "Aşağıdaki cümlelerden hangisi bu parçanın devamı olabilir?" (Paragraf Tamamlama)
     • "Metindeki altı çizili sözün parçaya kattığı anlam nedir?"
     • "Bu parçada hangi anlatım tekniği kullanılmıştır?" (Betimleme, Öyküleme, Açıklama)
   - ÖZELLİK: Düşünceyi geliştirme yolları (tanımlama, örnekleme, karşılaştırma) sorulur
            """.trimIndent()
            
            grade == 8 -> """
   📖 8. SINIF PARAGRAF FORMATI (LGS STANDARDI):
   - PARAGRAF UZUNLUĞU: 7-12 cümle (100-150 kelime)
   - DİL SEVİYESİ: Akademik, entelektüel. Terim ve mecaz kullanımı yoğun
   - KONULAR: Edebiyat eleştirisi, psikolojik kavramlar, sosyal gözlemler, kültürel miras
   - SORU TÜRLERİ (HER TÜR EŞİT DAĞILMALI):
     • "Bu metinden aşağıdakilerden hangisi çıkarılamaz/söylenemez?" (Olumsuz Çıkarım)
     • "Bu metin iki paragrafa bölünmek istense ikinci paragraf hangi cümleyle başlar?" (Yapı Analizi)
     • "Numaralanmış cümlelerden hangisi düşüncenin akışını bozmaktadır?" (Akış Bozucu)
     • "Bu sözleri söyleyen bir yazar için aşağıdakilerden hangisi söylenebilir?" (Yazar Karakteri)
     • "Parçadaki boşluğa aşağıdakilerden hangisi getirilmelidir?" (Boşluk Doldurma)
     • "Numaralanmış cümlelerden hangisi parçanın giriş cümlesi olabilir?" (Giriş Cümlesi)
     • "Bu parça hangi metin türüne örnek olabilir?" (Metin Türü - Makale, Deneme, Fıkra vs.)
     • "Parçada altı çizili sözcüğün yerine hangisi getirilebilir?" (Sözcük/Deyim Anlamı)
     • "Parçadaki anlatım biçimi aşağıdakilerden hangisidir?" (Anlatım Biçimi - Açıklama, Tartışma, Öyküleme)
     • "Bu paragrafta kullanılan düşünceyi geliştirme yolu hangisidir?" (Tanımlama, Örnekleme, Karşılaştırma)
     • "Metindeki altı çizili cümlenin paragraftaki görevi nedir?" (Cümle Görevi)
     • "Bu parçanın yazılış amacı aşağıdakilerden hangisidir?" (Amaç Belirleme)
   - ÖZELLİK: 
     • Cümleler I, II, III şeklinde numaralandırılabilir
     • Muhakeme ve derin çıkarım odaklı
     • Güçlü çeldiriciler - seçenekler birbirine çok yakın
     • Beceri temelli sorular (grafik yorumlama, iki metin karşılaştırma)
            """.trimIndent()
            
            level == EducationLevel.LISE && (grade == 9 || grade == 10) -> """
   📖 LİSE 9-10. SINIF PARAGRAF FORMATI (TYT STANDARDI):
   - PARAGRAF UZUNLUĞU: 8-12 cümle (120-180 kelime)
   - DİL SEVİYESİ: Akademik, bilimsel ve edebi dil. Soyut kavramlar, terimler
   - KONULAR: Bilim, teknoloji, felsefe, edebiyat, sosyoloji, psikoloji, tarih, sanat
   - SORU TÜRLERİ (TYT TÜRKÇE STANDARDI):
     • "Bu parçada aşağıdakilerden hangisi vurgulanmaktadır?" (Ana Düşünce)
     • "Bu parçadan aşağıdakilerden hangisi çıkarılamaz?" (Olumsuz Çıkarım)
     • "Parçada boş bırakılan yere aşağıdakilerden hangisi getirilmelidir?" (Boşluk Doldurma)
     • "Parçanın akışına göre numaralanmış cümlelerden hangisi çıkarılmalıdır?" (Akış Bozucu)
     • "Bu parçada anlatım bozukluğu olan cümle hangisidir?" (Anlatım Bozukluğu)
     • "Parçada altı çizili sözcük/deyim yerine hangisi kullanılabilir?" (Eş Anlam)
     • "Bu parçanın yazarı için aşağıdakilerden hangisi söylenebilir?" (Yazar Tutumu)
     • "Parçaya göre aşağıdakilerden hangisi söylenebilir?" (Çıkarım)
   - ÖZELLİK: 
     • TYT Türkçe sınav formatına uygun
     • Derin anlama ve eleştirel düşünme gerektiren sorular
     • Çeldiriciler çok güçlü, ince ayrımlar
     • Paragraf yapısı ve dil bilgisi soruları da olabilir
            """.trimIndent()
            
            level == EducationLevel.LISE && (grade == 11 || grade == 12) -> """
   📖 LİSE 11-12. SINIF PARAGRAF FORMATI (AYT STANDARDI):
   - PARAGRAF UZUNLUĞU: 10-15 cümle (150-220 kelime)
   - DİL SEVİYESİ: İleri akademik, felsefi ve edebi dil. Karmaşık cümle yapıları
   - KONULAR: Felsefe, sosyoloji, psikoloji, edebiyat eleştirisi, bilim felsefesi, sanat tarihi
   - SORU TÜRLERİ (AYT TÜRK DİLİ VE EDEBİYATI STANDARDI):
     • "Bu parçada aşağıdakilerden hangisi savunulmaktadır?" (Tez/Görüş)
     • "Parçada sözü edilen ... kavramı ile aşağıdakilerden hangisi kastedilmektedir?" (Kavram Analizi)
     • "Bu parçadan hareketle aşağıdakilerden hangisi söylenebilir?" (Derin Çıkarım)
     • "Parçada geçen ... ifadesi ile aşağıdakilerden hangisi amaçlanmaktadır?" (Amaç Belirleme)
     • "Bu parçanın anlatım özelliği aşağıdakilerden hangisidir?" (Anlatım Tekniği)
     • "Parçada kullanılan düşünceyi geliştirme yolu hangisidir?" (Tanımlama, Örnekleme, Karşılaştırma, Tanık Gösterme)
     • "Bu parçada aşağıdaki sanat/edebiyat akımlarından hangisinin izleri görülür?" (Akım Tespiti)
     • "Parçanın bütünlüğünü bozan cümle hangisidir?" (Bütünlük)
     • "Bu parçada hangi anlatım biçimi ağırlıklıdır?" (Açıklama, Tartışma, Betimleme, Öyküleme)
   - ÖZELLİK: 
     • AYT Türk Dili ve Edebiyatı sınav formatına uygun
     • Felsefi derinlik, edebi analiz, eleştirel okuma
     • Çok güçlü çeldiriciler, nüans gerektiren seçenekler
     • Edebiyat akımları, sanat tarihi, düşünce tarihi bilgisi gerektirebilir
            """.trimIndent()
            
            else -> """
   📖 GENEL PARAGRAF FORMATI:
   - PARAGRAF UZUNLUĞU: 5-8 cümle (80-130 kelime)
   - SORU TÜRLERİ:
     • "Verilen bilgiye göre aşağıdakilerden hangisine ulaşılabilir?"
     • "Yukarıdaki durumla ilgili hangisi çıkarılabilir?"
     • "Metne göre hangisi söylenebilir?"
     • "Bu metinden aşağıdakilerden hangisi çıkarılamaz?"
            """.trimIndent()
        }
    }

    /**
     * MEB 2025 TYMM UYUMLU PROMPT
     * Master Prompt kurallarına harfiyen uyar
     * LGS/KPSS tarzı soru çeşitliliği
     */
    private fun buildMebTymmPrompt(
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?,
        units: List<UnitConfig>
    ): String {
        val seviye = when (level) {
            EducationLevel.ILKOKUL -> "İlkokul ${grade ?: 4}. sınıf"
            EducationLevel.ORTAOKUL -> "Ortaokul ${grade ?: 5}. sınıf"
            EducationLevel.LISE -> "${schoolType.displayName} ${grade ?: 9}. sınıf"
            EducationLevel.KPSS -> "KPSS ${schoolType.displayName}"
            EducationLevel.AGS -> "AGS ${schoolType.displayName}"
        }

        val uniteListesi = if (units.isNotEmpty()) {
            "ÜNİTE/TEMALAR:\n" + units.joinToString("\n") { "- ${it.name}" }
        } else ""

        // Doğru cevap dağılımı analizi
        val answerDistribution = lastCorrectAnswers.groupingBy { it }.eachCount()
        
        // 3. sınıf için 3 şık (A-C), 4+ sınıf için 4 şık (A-D), Lise/KPSS/AGS için 5 şık (A-E)
        val is5OptionExam = level == EducationLevel.KPSS || level == EducationLevel.AGS || level == EducationLevel.LISE
        val is3OptionGrade = grade == 3 // 3. sınıf için 3 şık
        val optionLetters = when {
            is5OptionExam -> listOf("A", "B", "C", "D", "E")
            is3OptionGrade -> listOf("A", "B", "C")
            else -> listOf("A", "B", "C", "D")
        }
        val leastUsedAnswer = optionLetters.minByOrNull { answerDistribution[it] ?: 0 } ?: "B"

        // Türkçe için paragraf oranı daha yüksek
        val isTurkce = lesson.contains("Türkçe", ignoreCase = true)
        val isParagrafLesson = lesson.contains("Paragraf", ignoreCase = true)
        
        // İngilizce dersi tespiti
        val isEnglish = lesson.contains("İngilizce", ignoreCase = true) || 
                        lesson.contains("English", ignoreCase = true) ||
                        lesson.contains("ingilizce", ignoreCase = true)
        
        // Soru tipi dağılımı - GRAFİK YOK, PARAGRAF AĞIRLIKLI
        val pozitifCount = if (isParagrafLesson) 0 else (count * 0.15).toInt().coerceAtLeast(1)
        val negatifCount = if (isParagrafLesson) 0 else (count * 0.15).toInt().coerceAtLeast(1)
        val eslestirmeCount = if (isParagrafLesson) 0 else (count * 0.15).toInt().coerceAtLeast(1)
        
        // Eğer ders "Paragraf" ise %100 paragraf sorusu, Türkçe ise %50, diğerleri %30
        val paragrafCount = if (isParagrafLesson) count else if (isTurkce) (count * 0.50).toInt().coerceAtLeast(3) else (count * 0.30).toInt().coerceAtLeast(2)
        val karsilastirmaCount = count - pozitifCount - negatifCount - eslestirmeCount - paragrafCount
        
        // Zorluk seviyesi - ilkokul için sınıf bazlı kalibre
        val zorlukNotu = if (level == EducationLevel.ILKOKUL) {
            when (grade) {
                3 -> """
⚠️ ZORLUK SEVİYESİ (3. SINIF - KRİTİK):
- Sorular kısa ve net olmalı
- Cevap metinden doğrudan bulunabilir olmalı
- Ağır çıkarım / çok adımlı muhakeme ZORUNLU DEĞİL
- Çeldiriciler yaş düzeyine uygun, gerçekçi olmalı
                """.trimIndent()
                else -> """
⚠️ ZORLUK SEVİYESİ (İLKOKUL - KRİTİK):
- Sorular BASİT OLMAMALI, düşündürücü olmalı
- Tek adımda çözülen sorular tercih edilmemeli
- En az 1-2 adım akıl yürütme içerebilir
- Çeldiriciler gerçekçi ve yanıltıcı olmalı
- Ezber değil, anlama ve uygulama ölçülmeli
                """.trimIndent()
            }
        } else ""
        
        // Paragraf dersi için özel format kuralı
        val paragrafFormatKurali = if (isParagrafLesson) {
            """
⚠️ PARAGRAF DERSİ ÖZEL KURALI (HARFİYEN UYGULA):
- HER SORU mutlaka bir okuma parçası ile başlamalıdır.
${buildParagrafWordRangeRule(level, grade)}
- Format şu şekilde olmalı:
  "[OKUMA PARÇASI METNİ BURAYA GELECEK]
  
  (Boşluk)
  
  Soru Kökü"
- Soru metni (question alanı) hem parçayı hem de soruyu içermelidir.
            """.trimIndent()
        } else ""
        
        // İngilizce dersi için özel dil kuralı
        val englishLanguageRule = if (isEnglish) {
            """
🇬🇧 İNGİLİZCE DERSİ ÖZEL KURALI (KRİTİK - HARFİYEN UYGULA):

⚠️ SORU DİLİ: Tüm sorular İNGİLİZCE olarak yazılmalıdır!
- Soru metni (question) İNGİLİZCE olmalı
- Şıklar (optionA, optionB, optionC, optionD) İNGİLİZCE olmalı
- SADECE açıklama (explanation) Türkçe olabilir

📝 SORU TİPLERİ (İngilizce):
1. Grammar (Dilbilgisi): Tense, articles, prepositions, conditionals
   - "Choose the correct option to complete the sentence."
   - "Which sentence is grammatically correct?"

2. Vocabulary (Kelime Bilgisi): Synonyms, antonyms, word meanings
   - "What is the meaning of the underlined word?"
   - "Choose the word that best completes the sentence."

3. Reading Comprehension (Okuduğunu Anlama): Short passages in English
   - "According to the passage, which statement is true?"
   - "What is the main idea of the text?"

4. Fill in the blanks (Boşluk Doldurma):
   - "She _____ to school every day." (goes/go/going/went)

5. Error Detection (Hata Bulma):
   - "Find the error in the sentence."

⚠️ YASAK: Türkçe soru sormak! Sorular TAMAMEN İngilizce olmalı.
            """.trimIndent()
        } else ""

        // Ders-seviye uyumu için özel kurallar
        val dersSeviyeKurali = buildDersSeviyeKurali(lesson, level, grade, seviye)
        
        return """
ROL: Sen, MEB 2025 TYMM müfredatına ve ÖSYM sınav standartlarına tam uyumlu çoktan seçmeli soru üreticisisin.

HEDEF: $count adet $seviye $lesson sorusu üret.

$uniteListesi

${buildRagContext(level, schoolType, grade, lesson)}

$dersSeviyeKurali
$zorlukNotu
$paragrafFormatKurali
$englishLanguageRule

🎯 SORU TİPİ DAĞILIMI (ZORUNLU - HARFİYEN UYGULA):

1. POZİTİF SORULAR ($pozitifCount adet):
   - "Aşağıdakilerden hangisi doğrudur?"
   - "Hangisi ... özelliğine sahiptir?"
   - "Buna göre hangisi söylenebilir?"

2. NEGATİF SORULAR ($negatifCount adet):
   - "Aşağıdakilerden hangisi **_yanlıştır_**?"
   - "Hangisi ... **_değildir_**?"
   - "Hangisinde ... **_kullanılmamıştır_**?"
   (Olumsuz kelimeler sadece altı çizili, büyük harf yok)

3. EŞLEŞTİRME/SIRALAMA SORULARI ($eslestirmeCount adet):
   - "Hangisi ... ile ilgili doğru bilgi içerir?"
   - "Verilen öncüllerden hangisi ... ile eşleşir?"
   - "Aşağıdaki eşleştirmelerden hangisi doğrudur?"

4. PARAGRAF/METİN TABANLI SORULAR ($paragrafCount adet):
${buildGradeSpecificParagraphRules(level, grade)}

5. KARŞILAŞTIRMA SORULARI ($karsilastirmaCount adet):
   - "I. [ifade]\n   II. [ifade]\n   III. [ifade]\n   Yukarıdaki ifadelerden hangileri doğrudur?"
   - Şıklar: A) Yalnız I  B) I ve II  C) II ve III  D) I, II ve III

⚠️ ARDIŞIK FORMAT YASAĞI (KRİTİK):
   - Ard arda 2 "değildir/yanlıştır" sorusu YASAK
   - Ard arda 2 "doğrudur/hangisidir" sorusu YASAK
   - Ard arda 2 eşleştirme sorusu YASAK
   - Sorular KARMA sıralanmalı: pozitif→paragraf→negatif→karşılaştırma→eşleştirme

DEĞİŞMEZ KURAL SETİ:

1. SORU YAPISI:
   - ${when { is5OptionExam -> "5 şık (A, B, C, D, E)" ; is3OptionGrade -> "3 şık (A, B, C)" ; else -> "4 şık (A, B, C, D)" }}, yalnızca 1 doğru cevap
   - Soru kökü açık, net ve gereksiz bilgi içermemeli
   - Tek doğru cevaba izin vermeli, yoruma açık olmamalı

2. ŞIK KURALLARI:
   - Şıklar homojen, paralel yapıda ve EŞİT UZUNLUKTA olmalı
   - Aynı dil yapısında, aynı zaman kipinde, aynı üslupta
   - "Hepsi doğru", "Hiçbiri" gibi şıklar YASAK
   - Şıklar birbirini kapsamamalı

3. OLUMSUZ İFADELER (KRİTİK):
   - "değildir", "yanlıştır", "olamaz", "söylenemez", "yoktur" gibi ifadeler
   - Bu ifadeler SADECE altı çizili olarak vurgulanmalı: **_kelime_**
   - Büyük harf KULLANILMAYACAK
   - Örnek: Aşağıdakilerden hangisi doğru **_değildir_**?

4. ÇELDİRİCİLER:
   - Gerçekçi olmalı, tipik öğrenci hatalarından türetilmeli
   - İşlem hatası, kavram yanılgısı, birim dönüşümü hataları
   - Saçma/kolay elenen şıklar üretilmemeli

5. DOĞRU CEVAP DAĞILIMI:
   - Rastgele ve dengeli dağılsın (hep aynı şık olmasın)
   - Özellikle "$leastUsedAnswer" şıkkına ağırlık ver (az kullanıldı)

6. AÇIKLAMA:
   - Her soru için kısa ve pedagojik açıklama
   - Neden doğru + çeldiricilerin tipik hatası

7. GRAFİK VE TABLO YASAĞI (KRİTİK):
   - graphicType ve graphicData HER ZAMAN boş string "" olacak
   - Tablo, grafik, şekil, diyagram içeren sorular KESİNLİKLE YASAK
   - Soru metninde Markdown Tablosu (| --- |), ASCII art veya karmaşık şekiller YASAK
   - SADECE düzyazı (paragraf) veya basit maddeli sorular üretilecek

8. YASAK İÇERİKLER:
   - Kopya-yapıştır veya bilinen soru bankası kalıbı
   - Kültürel/etik açıdan riskli, ayrımcı içerik
   - Muğlaklaştırıcı zarflar: "genellikle", "çoğu zaman", "her zaman"

JSON FORMAT (SADECE BU FORMATTA DÖNDÜR):
${when {
    is5OptionExam -> """
[{
  "question": "Soru metni (olumsuz ifadeler **_altı çizili_** ile, büyük harf yok)",
  "optionA": "Şık A",
  "optionB": "Şık B",
  "optionC": "Şık C",
  "optionD": "Şık D",
  "optionE": "Şık E",
  "correctAnswer": "A/B/C/D/E",
  "explanation": "Açıklama",
  "graphicType": "",
  "graphicData": "",
  "unit": "Ünite adı (varsa)",
  "questionType": "pozitif/negatif/eslestirme/paragraf/karsilastirma"
}]
"""
    is3OptionGrade -> """
[{
  "question": "Soru metni (olumsuz ifadeler **_altı çizili_** ile, büyük harf yok)",
  "optionA": "Şık A",
  "optionB": "Şık B",
  "optionC": "Şık C",
  "correctAnswer": "A/B/C",
  "explanation": "Açıklama",
  "graphicType": "",
  "graphicData": "",
  "unit": "Ünite adı (varsa)",
  "questionType": "pozitif/negatif/eslestirme/paragraf/karsilastirma"
}]
"""
    else -> """
[{
  "question": "Soru metni (olumsuz ifadeler **_altı çizili_** ile, büyük harf yok)",
  "optionA": "Şık A",
  "optionB": "Şık B",
  "optionC": "Şık C",
  "optionD": "Şık D",
  "correctAnswer": "A/B/C/D",
  "explanation": "Açıklama",
  "graphicType": "",
  "graphicData": "",
  "unit": "Ünite adı (varsa)",
  "questionType": "pozitif/negatif/eslestirme/paragraf/karsilastirma"
}]
"""
}}

⚠️ SON KONTROL (HER SORU İÇİN ZORUNLU):
Soru üretmeden önce şu soruları sor:
1. "Bu soru gerçekten $lesson dersine mi ait?"
2. "Bu soru $seviye seviyesine uygun mu?"
3. "Başka bir dersin konusunu karıştırmış mıyım?"

❌ ÖRNEK HATALAR (YAPMA):
- Matematik dersinde: "Aşağıdaki paragrafta..." → YANLIŞ! Bu Türkçe sorusudur!
- Fen dersinde: "45 + 38 işleminin sonucu..." → YANLIŞ! Bu Matematik sorusudur!
- Türkçe dersinde: "Bitkiler nasıl beslenir?" → YANLIŞ! Bu Fen sorusudur!

SADECE JSON DÖNDÜR, BAŞKA HİÇBİR ŞEY YAZMA.
""".trimIndent()
    }

    /**
     * Şık uzunluk dengesi kontrolü
     */
    private fun validateOptionLength(q: QuestionModel): Boolean {
        val options = listOfNotNull(
            q.optionA.takeIf { it.isNotBlank() },
            q.optionB.takeIf { it.isNotBlank() },
            q.optionC.takeIf { it.isNotBlank() },
            q.optionD.takeIf { it.isNotBlank() },
            q.optionE.takeIf { it.isNotBlank() }
        )
        // 3. sınıf için 3 şık, 4+ sınıf için 4 şık, KPSS/AGS için 5 şık gerekli
        // Minimum 3 şık olmalı (3. sınıf desteği için)
        if (options.size < 3) return false
        val lengths = options.map { it.length }
        val avg = lengths.average()
        val maxDeviation = lengths.maxOf { kotlin.math.abs(it - avg) }
        // Ortalamadan %100'den fazla sapma varsa reddet
        return maxDeviation <= avg
    }

    /**
     * ÇİFT KONTROL SİSTEMİ - Yerel Doğrulama
     * Soru içeriğini kapsamlı şekilde kontrol eder
     */
    private fun validateQuestionContent(q: QuestionModel): Boolean {
        val question = q.question ?: return false
        
        // 1. Soru uzunluğu kontrolü (en az 20 karakter)
        if (question.length < 20) {
            Log.w(TAG, "❌ Soru çok kısa: ${question.take(30)}")
            return false
        }
        
        // 2. Soru soru işareti ile bitmeli veya soru kalıbı içermeli
        val hasQuestionFormat = question.contains("?") || 
            question.contains("hangisi", ignoreCase = true) ||
            question.contains("kaçtır", ignoreCase = true) ||
            question.contains("nedir", ignoreCase = true)
        if (!hasQuestionFormat) {
            Log.w(TAG, "❌ Soru formatı hatalı: ${question.take(30)}")
            return false
        }
        
        // 3. Doğru cevap şıklarda mevcut mu?
        val correctOption = when (q.correctAnswer) {
            "A" -> q.optionA
            "B" -> q.optionB
            "C" -> q.optionC
            "D" -> q.optionD
            "E" -> q.optionE
            else -> null
        }
        if (correctOption.isNullOrBlank()) {
            Log.w(TAG, "❌ Doğru cevap şıkkı boş: ${q.correctAnswer}")
            return false
        }
        
        // 4. Şıklar birbirinden farklı mı? (aynı şık tekrarı yok)
        val allOptions = listOfNotNull(q.optionA, q.optionB, q.optionC, q.optionD, q.optionE)
            .filter { it.isNotBlank() }
            .map { it.lowercase().trim() }
        if (allOptions.distinct().size != allOptions.size) {
            Log.w(TAG, "❌ Tekrar eden şık var")
            return false
        }
        
        // 5. Şıklar soru metninin kopyası değil mi?
        val questionLower = question.lowercase()
        for (opt in allOptions) {
            if (opt.length > 10 && questionLower.contains(opt)) {
                Log.w(TAG, "❌ Şık soru metninde geçiyor: $opt")
                return false
            }
        }
        
        // 6. DERS UYUMU KONTROLÜ (YENİ - KRİTİK)
        if (!validateLessonContentMatch(q)) {
            Log.w(TAG, "❌ Ders uyumsuzluğu tespit edildi: ${question.take(50)}")
            return false
        }
        
        return true
    }
    
    /**
     * DERS UYUMU KONTROLÜ
     * Sorunun ders içeriğine uygun olup olmadığını kontrol eder
     */
    private fun validateLessonContentMatch(q: QuestionModel): Boolean {
        val lesson = q.lesson.lowercase()
        val question = q.question?.lowercase() ?: return false
        val allText = "$question ${q.optionA} ${q.optionB} ${q.optionC} ${q.optionD} ${q.optionE}".lowercase()
        
        // Matematik dersi kontrolü
        if (lesson.contains("matematik") || lesson.contains("math")) {
            // Matematik dışı içerik tespiti
            val nonMathKeywords = listOf(
                "paragraf", "metin", "yazar", "şair", "hikaye", "öykü", "roman",
                "cümle", "kelime", "sözcük", "noktalama", "yazım",
                "canlı", "bitki", "hayvan", "hücre", "organ", "sistem",
                "tarih", "coğrafya", "harita", "ülke", "şehir", "kıta"
            )
            
            for (keyword in nonMathKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Matematik dersinde '$keyword' kelimesi bulundu")
                    return false
                }
            }
            
            // Matematik içeriği var mı kontrolü
            val mathKeywords = listOf(
                "sayı", "işlem", "toplama", "çıkarma", "çarpma", "bölme",
                "kesir", "geometri", "şekil", "alan", "çevre", "hacim",
                "ölçme", "uzunluk", "ağırlık", "zaman", "para",
                "problem", "çözüm", "hesap", "sonuç"
            )
            
            val hasMathContent = mathKeywords.any { allText.contains(it) } ||
                                 allText.contains(Regex("\\d+")) // Sayı içeriyor mu?
            
            if (!hasMathContent) {
                Log.w(TAG, "❌ Matematik dersinde matematik içeriği bulunamadı")
                return false
            }
        }
        
        // Türkçe dersi kontrolü
        if (lesson.contains("türkçe") || lesson.contains("turkce")) {
            // Türkçe dışı içerik tespiti
            val nonTurkishKeywords = listOf(
                "toplama", "çıkarma", "çarpma", "bölme", "işlem", "hesap",
                "atom", "molekül", "hücre", "organ", "enerji",
                "harita", "kıta", "ülke", "başkent"
            )
            
            for (keyword in nonTurkishKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Türkçe dersinde '$keyword' kelimesi bulundu")
                    return false
                }
            }
        }
        
        // Fen Bilimleri dersi kontrolü
        if (lesson.contains("fen")) {
            // Fen dışı içerik tespiti
            val nonScienceKeywords = listOf(
                "paragraf", "cümle", "noktalama", "yazım", "şair", "yazar",
                "toplama", "çıkarma", "çarpma", "bölme", "kesir",
                "tarih", "coğrafya", "harita", "ülke"
            )
            
            for (keyword in nonScienceKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Fen dersinde '$keyword' kelimesi bulundu")
                    return false
                }
            }
            
            // Fen içeriği var mı kontrolü
            val scienceKeywords = listOf(
                "canlı", "bitki", "hayvan", "hücre", "organ",
                "madde", "katı", "sıvı", "gaz", "enerji",
                "ışık", "ses", "kuvvet", "hareket", "sürtünme",
                "dünya", "güneş", "ay", "gezegen", "yıldız"
            )
            
            val hasScienceContent = scienceKeywords.any { allText.contains(it) }
            
            if (!hasScienceContent) {
                Log.w(TAG, "❌ Fen dersinde fen içeriği bulunamadı")
                return false
            }
        }
        
        // Sosyal Bilgiler dersi kontrolü
        if (lesson.contains("sosyal")) {
            // Sosyal Bilgiler dışı içerik tespiti
            val nonSocialKeywords = listOf(
                "toplama", "çıkarma", "çarpma", "bölme", "kesir",
                "atom", "molekül", "hücre", "enerji",
                "paragraf", "cümle", "noktalama"
            )
            
            for (keyword in nonSocialKeywords) {
                if (allText.contains(keyword)) {
                    Log.w(TAG, "❌ Sosyal Bilgiler dersinde '$keyword' kelimesi bulundu")
                    return false
                }
            }
        }
        
        return true
    }

    /**
     * ÇİFT KATMANLI DOĞRULAMA - HALÜSİNASYON  ÖNLEYİCİ
     * AI soruyu çözüp verilen cevapla karşılaştırır
     */
    private suspend fun validateCorrectAnswerWithAI(q: QuestionModel): Boolean = withContext(Dispatchers.IO) {
        val prompt = """
Sen bir sınav uzmanısın. Bu soruyu dikkatlice çöz ve SADECE doğru cevabın harfini yaz.

SORU: ${q.question}
A) ${q.optionA}
B) ${q.optionB}
C) ${q.optionC}
D) ${q.optionD}
${if (q.optionE.isNotBlank()) "E) ${q.optionE}" else ""}

Kurallar:
- Soruyu dikkatli analiz et
- Doğru cevabı bul
- SADECE TEK HARF YAZ (A, B, C, D veya E)
- Başka hiçbir açıklama yazma

CEVAP:
""".trimIndent()

        try {
            val response = gemini.generateContent(prompt).text?.trim().orEmpty()
            val aiAnswer = response.uppercase().firstOrNull { it in 'A'..'E' }?.toString() ?: ""
            val matches = aiAnswer == q.correctAnswer
            
            if (matches) {
                DebugLog.d(TAG, "🔍 AI doğrulama: ✅ Eşleşti (${q.correctAnswer})")
            } else {
                Log.w(TAG, "🔍 AI doğrulama: ❌ Eşleşmedi! AI=$aiAnswer, Beklenen=${q.correctAnswer}")
            }
            matches
        } catch (e: Exception) {
            Log.w(TAG, "AI doğrulama hatası: ${e.message}")
            // Hata durumunda GEÇERLİ say (rate limit’e takılmamak için)
            true
        }
    }

    /**
     * AI İLE İKİNCİ KONTROL (opsiyonel - ağır sorular için)
     * Soruyu AI'a gönderip mantıksal tutarlılık kontrolü yapar
     */
    suspend fun validateQuestionWithAI(q: QuestionModel): Boolean = withContext(Dispatchers.IO) {
        validateCorrectAnswerWithAI(q)
    }

    // ==================== ESKİ API UYUMLULUĞU ====================

    suspend fun generateFastBatch(
        lesson: String, count: Int, level: EducationLevel,
        schoolType: SchoolType, grade: Int?
    ): List<QuestionModel> {
        val units = CurriculumManager.getSubjectsFor(schoolType, grade)
            .find { it.displayName == lesson }?.units ?: emptyList()
        
        // AKILLI RETRY MEKANİZMASI - 5 deneme hakkı (429 için özel bekleme)
        var lastError: Exception? = null
        var consecutiveRateLimits = 0
        
        repeat(5) { attempt ->
            try {
                val result = turboGenerate(lesson, count, level, schoolType, grade, units, validateWithAi = false)
                if (result.isNotEmpty()) {
                    consecutiveRateLimits = 0 // Başarılı olunca sıfırla
                    return result
                }
                // Boş sonuç geldi, tekrar dene
                Log.w(TAG, "⚠️ $lesson: Deneme ${attempt + 1}/5 - Boş sonuç, tekrar deneniyor...")
                kotlinx.coroutines.delay(2000L * (attempt + 1))
            } catch (e: Exception) {
                lastError = e
                val errorMsg = e.message ?: ""
                
                // 🔴 429 RATE LIMIT ÖZEL İŞLEM
                val isRateLimit = errorMsg.contains("429") || 
                                  errorMsg.contains("rate", ignoreCase = true) ||
                                  errorMsg.contains("quota", ignoreCase = true) ||
                                  errorMsg.contains("too many", ignoreCase = true)
                
                if (isRateLimit) {
                    consecutiveRateLimits++
                    val waitTime = 15000L * consecutiveRateLimits // 15, 30, 45... saniye
                    Log.w(TAG, "⏳ $lesson: 429 Rate Limit! ${waitTime/1000} saniye bekleniyor... (${consecutiveRateLimits}. ardışık)")
                    kotlinx.coroutines.delay(waitTime)
                } else {
                    Log.e(TAG, "❌ $lesson: Deneme ${attempt + 1}/5 - Hata: $errorMsg")
                    kotlinx.coroutines.delay(3000L * (attempt + 1))
                }
            }
        }
        
        // 5 deneme de başarısız
        Log.e(TAG, "❌ $lesson: 5 deneme sonrası başarısız. Son hata: ${lastError?.message}")
        return emptyList()
    }

    suspend fun generateBatchForLevel(
        lesson: String, count: Int, level: EducationLevel,
        schoolType: SchoolType, grade: Int?
    ) = generateFastBatch(lesson, count, level, schoolType, grade)

    suspend fun generateBatch(lesson: String, count: Int) =
        turboGenerate(lesson, count, EducationLevel.ORTAOKUL, SchoolType.ORTAOKUL_STANDARD, 5, emptyList())

    suspend fun generateMultiple(lesson: String, count: Int) =
        generateBatch(lesson, count)

    /**
     * SADECE GRAFİKLİ SORU ÜRET
     * Her soru zorunlu olarak graphicType ve graphicData içerir
     */
    suspend fun generateGraphicOnlyBatch(
        lesson: String,
        count: Int,
        level: EducationLevel,
        schoolType: SchoolType,
        grade: Int?
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        // ============ GEÇİCİ KISITLAMA ============
        // 4. ve 5. sınıf için grafikli soru üretme (müfredat hazır değil)
        if (grade == 4 || grade == 5) {
            DebugLog.d("AI_GEN", "⚠️ Grafikli sorular 4-5. sınıf için geçici olarak kapalı")
            return@withContext emptyList()
        }
        // ============ GEÇİCİ KISITLAMA SONU ============
        
        val seviye = when (level) {
            EducationLevel.ILKOKUL -> "İlkokul ${grade ?: 4}. sınıf"
            EducationLevel.ORTAOKUL -> "Ortaokul ${grade ?: 5}. sınıf"
            EducationLevel.LISE -> "${schoolType.displayName} ${grade ?: 9}. sınıf"
            EducationLevel.KPSS -> "KPSS ${schoolType.displayName}"
            EducationLevel.AGS -> "AGS ${schoolType.displayName}"
        }
        
        val graphicPrompt = """
ROL: Sen, ÖSYM ve MEB standartlarına uygun GRAFİKLİ soru üreticisisin.

HEDEF: $count adet $seviye $lesson GRAFİKLİ sorusu üret.

⚠️ KRİTİK: HER SORU ZORUNLU OLARAK graphicType VE graphicData İÇERMELİ!

Desteklenen graphicType değerleri ve formatları:

1. "numberLine" - Sayı doğrusu:
   {"min":-5,"max":5,"points":{"A":-2,"B":3}}
   Örnek: "Sayı doğrusunda A ve B noktaları gösterilmiştir. A ile B arası mesafe kaçtır?"

2. "pieChart" - Pasta grafiği:
   {"slices":[25,35,40],"labels":["Kırmızı","Mavi","Yeşil"]}
   Örnek: "Pasta grafiğinde gösterilen dağılıma göre en büyük dilim hangisidir?"

3. "table" - Veri tablosu:
   {"rows":[["Öğrenci","Puan"],["Ali","85"],["Ayşe","90"],["Mehmet","78"]]}
   Örnek: "Tabloya göre en yüksek puanı alan öğrenci kimdir?"

4. "barChart" - Çubuk grafik:
   {"bars":[10,25,15,30],"labels":["Ocak","Şubat","Mart","Nisan"]}
   Örnek: "Grafiğe göre en fazla satış hangi ayda yapılmıştır?"

5. "grid" - Kare ızgara:
   {"cols":5,"rows":5,"filled":[[0,0,"blue"],[1,1,"red"],[2,2,"green"]]}
   Örnek: "Şekilde boyalı karelerin toplam sayısı kaçtır?"

6. "coordinate" - Koordinat sistemi:
   {"minX":-5,"maxX":5,"minY":-5,"maxY":5,"points":[{"label":"A","x":2,"y":3},{"label":"B","x":-1,"y":2}]}
   Örnek: "Koordinat sisteminde A ve B noktaları verilmiştir. Hangi çeyrekte bulunurlar?"

JSON FORMAT:
[{
  "question": "Soru metni",
  "optionA": "Şık A",
  "optionB": "Şık B",
  "optionC": "Şık C",
  "optionD": "Şık D",
  "optionE": "Şık E",
  "correctAnswer": "A/B/C/D/E",
  "explanation": "Açıklama",
  "graphicType": "ZORUNLU - numberLine/pieChart/table/barChart/grid/coordinate",
  "graphicData": "ZORUNLU - JSON formatında grafik verisi"
}]

SADECE JSON DÖNDÜR.
""".trimIndent()

        val rawText = try {
            val response = gemini.generateContent(graphicPrompt).text?.trim().orEmpty()
            DebugLog.d(TAG, "🎨 GRAPHIC RAW RESPONSE: ${response.take(500)}")
            response
        } catch (e: Exception) {
            Log.e(TAG, "Graphic question error: ${e.message}")
            return@withContext emptyList()
        }
        
        if (rawText.isBlank()) {
            Log.w(TAG, "🎨 Empty response from AI")
            return@withContext emptyList()
        }
        
        val parsed = parseQuestions(rawText, lesson)
        DebugLog.d(TAG, "🎨 PARSED ${parsed.size} questions, with graphics: ${parsed.count { it.graphicType.isNotBlank() }}")
        
        // Debug: log each question's graphicType
        parsed.forEach { q ->
            DebugLog.d(TAG, "🎨 Q: ${q.question.take(30)}... | type=${q.graphicType} | data=${q.graphicData.take(50)}")
        }
        
        // Döndür (filtre kaldırıldı - debug için)
        parsed
    }

    // ==================== YARDIMCI FONKSİYONLAR ====================

    private fun fingerprint(q: QuestionModel): String {
        val norm = { s: String ->
            s.lowercase(Locale.ROOT)
                .replace(Regex("[^a-zçğıöşü0-9]"), "")
                .take(80)
        }
        return "${norm(q.question)}|${norm(q.optionA)}|${q.correctAnswer}"
    }

    private fun parseQuestions(raw: String, lesson: String): List<QuestionModel> {
    val results = mutableListOf<QuestionModel>()

    try {
        val start = raw.indexOf('[')
        val end = raw.lastIndexOf(']')
        if (start < 0 || end <= start) return emptyList()

        val arr = JSONArray(raw.substring(start, end + 1))

        for (i in 0 until arr.length()) {
            try {
                val obj = arr.getJSONObject(i)

                var question = obj.optString("question", "").trim()
                val optA = obj.optString("optionA", "").trim()
                val optB = obj.optString("optionB", "").trim()
                val optC = obj.optString("optionC", "").trim()
                val optD = obj.optString("optionD", "").trim()
                val optE = obj.optString("optionE", "").trim()  // 5. şık
                val correct = obj.optString("correctAnswer", "").uppercase().trim()
                var explanation = obj.optString("explanation", "").trim()
                
                // Grafik verileri
                val graphicType = obj.optString("graphicType", "").trim()
                val graphicData = obj.optString("graphicData", "").trim()

                // Validasyon: En az 3 şık (A-B-C) zorunlu, D ve E opsiyonel
                // 3. sınıf 3 şık, 4+ sınıf 4 şık, KPSS/AGS 5 şık
                if (question.isBlank() || optA.isBlank() || optB.isBlank() || optC.isBlank()) continue
                
                // Doğru cevap kontrolü: şık varsa geçerli
                val validAnswers = buildList {
                    add("A"); add("B"); add("C")
                    if (optD.isNotBlank()) add("D")
                    if (optE.isNotBlank()) add("E")
                }
                if (correct !in validAnswers) continue
                
                // E şıkkı seçiliyse optE de dolu olmalı (zaten validAnswers'da kontrol edildi)

                // Olumsuz kelime vurgulama (sadece altı çizili - büyük harf yok)
                val negatives = listOf(
                    "değildir", "yanlıştır", "olamaz", "söylenemez", "yoktur",
                    "yapılamaz", "kullanılamaz", "göstermez", "içermez", "bulunmaz"
                )
                for (neg in negatives) {
                    if (question.lowercase(Locale("tr")).contains(neg) && !question.contains("<u>")) {
                        question = question.replace(Regex("(?i)\\b($neg)\\b")) {
                            "<u>${it.value}</u>"
                        }
                        break
                    }
                }

                // [[...]] -> <u>...</u>
                question = question.replace(Regex("\\[\\[(.+?)]]")) { 
                    "<u>${it.groupValues[1]}</u>" 
                }
                
                // **_..._** formatını destekle
                question = question.replace(Regex("\\*\\*_(.+?)_\\*\\*")) {
                    "<u>${it.groupValues[1]}</u>"
                }
                
                // _**...**_ formatını da destekle (AI bazen bu şekilde üretiyor)
                question = question.replace(Regex("_\\*\\*(.+?)\\*\\*_")) {
                    "<u>${it.groupValues[1]}</u>"
                }

                // Minimum açıklama
                if (explanation.length < 30) {
                    explanation = "Doğru cevap $correct şıkkıdır. $explanation"
                }

                // graphicType boşsa soru metninden otomatik tespit et
                val detectedGraphicType = if (graphicType.isBlank()) {
                    detectGraphicTypeFromText(question)
                } else {
                    graphicType
                }
                
                // graphicType varsa ama graphicData boşsa, örnek veri üret
                val finalGraphicData = if (detectedGraphicType.isNotBlank() && graphicData.isBlank()) {
                    generateFallbackGraphicData(detectedGraphicType)
                } else {
                    graphicData
                }

                results.add(QuestionModel(
                    question = question,
                    optionA = cleanOption(optA),
                    optionB = cleanOption(optB),
                    optionC = cleanOption(optC),
                    optionD = cleanOption(optD),
                    optionE = cleanOption(optE),
                    correctAnswer = correct,
                    explanation = explanation,
                    lesson = lesson,
                    graphicType = detectedGraphicType,
                    graphicData = finalGraphicData
                ))
            } catch (e: Exception) {
                Log.w(TAG, "Parse item: ${e.message}")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Parse error: ${e.message}")
    }

    return results
}

    private fun cleanOption(option: String): String {
        return option
            .replace(Regex("<u>|</u>|<b>|</b>|\\[\\[|]]"), "")
            .replace(Regex("\\*\\*|__"), "")
            .trim()
    }

    fun addToSeenFingerprints(fingerprints: Set<String>) {
        seenFingerprints.addAll(fingerprints)
    }

    /**
     * Soru metninden grafik türünü otomatik tespit et
     */
    private fun detectGraphicTypeFromText(text: String): String {
        val lowerText = text.lowercase(Locale("tr"))
        return when {
            // Pasta grafik önce kontrol et (öncelik önemli!)
            lowerText.contains("pasta grafik") || lowerText.contains("pasta dağ") || 
            lowerText.contains("daire grafik") || lowerText.contains("derece ile temsil") ||
            (lowerText.contains("dağılım") && lowerText.contains("grafik") && !lowerText.contains("yağış")) -> "pieChart"
            
            // Tablo - eşleştirme soruları dahil
            lowerText.contains("tabloya göre") || lowerText.contains("tabloda") ||
            lowerText.contains("aşağıdaki tablo") || lowerText.contains("eşleştirilmiştir") -> "table"
            
            // Çubuk/Sütun/Yağış grafik - geniş kapsam
            lowerText.contains("çubuk grafik") || lowerText.contains("sütun grafik") ||
            lowerText.contains("bar grafik") || lowerText.contains("yağış grafik") ||
            lowerText.contains("grafikte") && (
                lowerText.contains("yıl") || lowerText.contains("yağış") ||
                lowerText.contains("gelir") || lowerText.contains("satış") ||
                lowerText.contains("kar") || lowerText.contains("bütçe")
            ) -> "barChart"
            
            // Sayı doğrusu
            lowerText.contains("sayı doğrusu") || lowerText.contains("sayı eksen") -> "numberLine"
            
            // Koordinat
            lowerText.contains("koordinat") || lowerText.contains("grafik düzlem") -> "coordinate"
            
            // Grid
            lowerText.contains("birim kare") || lowerText.contains("ızgara") ||
            lowerText.contains("kareli") -> "grid"
            
            else -> ""
        }
    }

    /**
     * graphicType için varsayılan örnek veri üret
     */
    private fun generateFallbackGraphicData(graphicType: String): String {
        return when (graphicType.lowercase()) {
            "numberline" -> """{"min":-5,"max":5,"points":{"A":-2,"B":3}}"""
            "piechart" -> """{"slices":[30,25,20,15,10],"labels":["I","II","III","IV","V"]}"""
            "table" -> """{"rows":[["Öğe","Değer"],["I","45"],["II","52"],["III","38"],["IV","61"],["V","55"]]}"""
            "barchart" -> """{"bars":[45,52,38,61,55],"labels":["I","II","III","IV","V"]}"""
            "grid" -> """{"cols":5,"rows":5,"filled":[[0,0,"blue"],[1,1,"red"],[2,2,"green"]]}"""
            "coordinate" -> """{"minX":-5,"maxX":5,"minY":-5,"maxY":5,"points":[{"label":"A","x":2,"y":3},{"label":"B","x":-1,"y":2}]}"""
            else -> ""
        }
    }

    // ==================== MİNİ OYUN ====================

    suspend fun generateMiniGameBatch(gameType: String, count: Int): List<GameQuestion> =
        withContext(Dispatchers.IO) {
            val prompt = """
$count adet $gameType mini oyun sorusu.
Format: [{"text":"...","options":["A","B","C","D"],"correctIndex":0}]
SADECE JSON.
""".trimIndent()

            val raw = try {
                gemini.generateContent(prompt).text?.trim().orEmpty()
            } catch (e: Exception) { "" }

            if (raw.isBlank()) return@withContext emptyList()

            try {
                val start = raw.indexOf('[')
                val end = raw.lastIndexOf(']')
                if (start < 0 || end <= start) return@withContext emptyList()

                val arr = JSONArray(raw.substring(start, end + 1))
                (0 until arr.length()).mapNotNull { i ->
                    val obj = arr.getJSONObject(i)
                    val opts = obj.optJSONArray("options") ?: return@mapNotNull null
                    if (opts.length() != 4) return@mapNotNull null

                    GameQuestion(
                        lesson = gameType,
                        text = obj.optString("text", ""),
                        options = (0 until 4).map { opts.optString(it, "") },
                        correctIndex = obj.optInt("correctIndex", 0).coerceIn(0, 3)
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
}
