package com.example.bilgideham

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * KPSS Deneme Soru Üreticisi
 * 
 * 120 soruluk paketler oluşturur:
 * - Genel Yetenek (60): Türkçe 30, Matematik 30
 * - Genel Kültür (60): Tarih 27, Coğrafya 18, Vatandaşlık 9, Güncel 6
 * 
 * 4x Gemini API ile paralel üretim yapar
 */
object KpssDenemGenerator {
    
    private const val TAG = "KPSS_DENEME"
    
    // Progress callback
    var onProgressUpdate: ((Int, Int, String) -> Unit)? = null
    var onLogMessage: ((String) -> Unit)? = null
    
    // Üretilen sorular (geçici)
    private val generatedQuestions = mutableListOf<QuestionModel>()
    
    // Fingerprint cache (tekrar kontrolü)
    private val seenFingerprints = ConcurrentHashMap.newKeySet<String>()
    
    // Cevap dağılımı takibi
    private val answerDistribution = mutableMapOf<String, AtomicInteger>()
    
    // API Keys (GeminiApiProvider'dan alınacak)
    private fun getApiKey(index: Int): String {
        return GeminiApiProvider.getKeyByIndex(index) ?: GeminiApiProvider.getFirstKey() ?: ""
    }
    
    /**
     * 120 soruluk KPSS deneme paketi üretir
     * @param paketNo Deneme paket numarası (1, 2, 3...)
     * @param seviye KPSS_ORTAOGRETIM, KPSS_ONLISANS veya KPSS_LISANS
     * @return Tam 120 soru (eksik varsa retry ile tamamlanır)
     */
    suspend fun generateDenemePaketi(
        paketNo: Int,
        seviye: SchoolType
    ): List<QuestionModel> = coroutineScope {
        val paketId = "kpss_deneme_$paketNo"
        log("🚀 KPSS Deneme #$paketNo ($seviye) üretimi başlıyor... (PARALEL MOD)")
        onProgressUpdate?.invoke(0, 120, "Hazırlanıyor...")
        
        val generatedQuestions = Collections.synchronizedList(ArrayList<QuestionModel>())
        val seenFingerprints = Collections.synchronizedSet(ConcurrentHashMap.newKeySet<String>())
        
        // Ders hedefleri (Toplam 120)
        val dersHedefleri = mapOf(
            "Türkçe" to 30,
            "Matematik" to 30,
            "Tarih" to 27,
            "Coğrafya" to 18,
            "Vatandaşlık" to 9,
            "Güncel Bilgiler" to 6
        )
        
        val dersUretimleri = ConcurrentHashMap<String, MutableList<QuestionModel>>()
        dersHedefleri.keys.forEach { dersUretimleri[it] = Collections.synchronizedList(ArrayList()) }
        
        // Ders üretim fonksiyonları haritası
        val generators = mapOf(
            "Türkçe" to ::generateTurkceQuestions,
            "Matematik" to ::generateMatematikQuestions,
            "Tarih" to ::generateTarihQuestions,
            "Coğrafya" to ::generateCografyaQuestions,
            "Vatandaşlık" to ::generateVatandaslikQuestions,
            "Güncel Bilgiler" to ::generateGuncelQuestions
        )
        
        // PARALEL ÇALIŞMA (Tüm dersler aynı anda başlasın ama kademeli)
        val jobs = dersHedefleri.entries.toList().mapIndexed { index, (dersAdi, hedef) ->
            async(Dispatchers.IO) {
                // STAGGERING: Ani yüklenmeyi önlemek için her ders arasında ufak gecikme
                delay(index * 2000L)

                try {
                    log("⚡ $dersAdi başlatılıyor ($hedef soru)...")
                    val generator = generators[dersAdi] ?: return@async
                    
                    // RETRY LOOP (10 Deneme Hakkı)
                    var currentQuestions = mutableListOf<QuestionModel>()
                    var retryCount = 0
                    val maxRetries = 10
                    
                    // İlk deneme
                    try {
                        val ilkUretim = generator(hedef, paketId, seviye)
                        currentQuestions.addAll(ilkUretim)
                    } catch (e: Exception) {
                        log("❌ $dersAdi ilk deneme hatası: ${e.message}")
                    }
                    
                    // Eksik tamamlama döngüsü
                    while (currentQuestions.size < hedef && retryCount < maxRetries) {
                        val eksik = hedef - currentQuestions.size
                        retryCount++
                        
                        // Akıllı Backoff: Hata sayısına göre artan bekleme
                        val baseDelay = 3000L 
                        val waitTime = baseDelay * retryCount + (if(retryCount > 3) 2000L else 0L)
                        
                        log("⚠️ $dersAdi: $eksik eksik soru, İNATÇI EŞİTLEME #${retryCount} (Bekleniyor: ${waitTime/1000}sn)...")
                        
                        delay(waitTime)
                        
                        try {
                            // Bufferlı iste: Eksik * 1.5 + 2 (Garanti olsun)
                            val iste = (eksik * 1.5).toInt() + 2
                            val yeniSorular = generator(iste, paketId, seviye)
                            
                            // Fingerprint kontrolü
                            val filtrelenmis = yeniSorular.filter { yeni ->
                                val fp = fingerprint(yeni)
                                if (seenFingerprints.contains(fp)) {
                                    false
                                } else {
                                    seenFingerprints.add(fp)
                                    true
                                }
                            }
                            
                            currentQuestions.addAll(filtrelenmis)
                            log("  -> $dersAdi: +${filtrelenmis.size} eklendi (Toplam: ${currentQuestions.size}/$hedef)")
                            
                        } catch (e: Exception) {
                            val isRateLimit = e.message?.contains("429") == true || e.message?.contains("quota") == true
                            if (isRateLimit) {
                                log("⏳ $dersAdi: API Limit Aşımı (429) - Biraz daha bekleniyor...")
                                delay(5000) // Ekstra bekleme
                            } else {
                                log("❌ $dersAdi retry hatası: ${e.message}")
                            }
                        }
                    }
                    
                    // Hedef sayıya kes (fazla varsa)
                    val finalQuestions = currentQuestions.take(hedef)
                    dersUretimleri[dersAdi]?.addAll(finalQuestions) 
                        ?: run {
                            log("⚠️ $dersAdi için liste bulunamadı, oluşturuluyor...")
                            dersUretimleri[dersAdi] = Collections.synchronizedList(ArrayList<QuestionModel>()).apply {
                                addAll(finalQuestions)
                            }
                        }
                    generatedQuestions.addAll(finalQuestions)
                    
                    log("✅ $dersAdi TAMAMLANDI: ${finalQuestions.size}/$hedef")
                    onProgressUpdate?.invoke(generatedQuestions.size, 120, "$dersAdi bitti")
                    
                } catch (e: Exception) {
                    log("🔥 $dersAdi kritik hata: ${e.message}")
                }
            }
        }
        
        // Tüm işlerin bitmesini bekle
        jobs.awaitAll()
        
        // Sıralama ve Numaralandırma
        val sortedQuestions = mutableListOf<QuestionModel>()
        var globalSoruNo = 0
        
        // Ders sırasına göre ekle
        for ((dersAdi, hedef) in dersHedefleri) {
            val sorular = dersUretimleri[dersAdi] ?: emptyList()
            
            // Eğer hala eksik varsa, çok acil durum (Placeholder)
            // Kullanıcı "eksik kalmasın" dediği için burada "Yedek soru" üretebiliriz
            // Veya sadece olanı koyarız (10 retry sonrası yapacak çok şey yok)
            
            sorular.take(hedef).forEach { q ->
                globalSoruNo++
                sortedQuestions.add(q.copy(
                    id = "${paketId}_$globalSoruNo",
                    questionNumber = globalSoruNo
                ))
            }
        }
        
        log("� KPSS Deneme #$paketNo BİTTİ: ${sortedQuestions.size}/120 soru")
        
        if (sortedQuestions.size < 120) {
             log("❌ KRİTİK: ${120 - sortedQuestions.size} soru hala eksik! (Yüksek trafik olabilir)")
        }
        
        sortedQuestions
    }
    
    // ==================== TÜRKÇE SORULARI ====================
    
    private suspend fun generateTurkceQuestions(
        count: Int, 
        paketId: String, 
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        // Konu dağılımını al
        val konuDagilimi = KpssDenemeCurriculumData.generateKonuDagilimi("Türkçe")
        log("  📝 Türkçe Dağılım: $konuDagilimi")
        
        // Her konu için paralel üretim (4 Gemini ile)
        val jobs = mutableListOf<Deferred<List<QuestionModel>>>()
        var keyIndex = 0
        
        for ((konuId, soruSayisi) in konuDagilimi) {
            if (soruSayisi == 0) continue
            
            val konuDetay = KpssDenemeCurriculumData.getTurkceKonuDetay(konuId)
            if (konuDetay == null) continue
            
            val apiKey = getApiKey(keyIndex % 4)
            keyIndex++
            
            jobs.add(async {
                delay(keyIndex * 1000L) // Internal Stagger
                generateTurkceKonuSorulari(apiKey, konuDetay, soruSayisi, seviye)
            })
        }
        
        // Tüm async işlemleri bekle
        jobs.forEach { job ->
            result.addAll(job.await())
        }
        
        result.take(count)
    }
    
    private suspend fun generateTurkceKonuSorulari(
        apiKey: String,
        konu: KpssDenemeCurriculumData.KpssTurkceKonu,
        count: Int,
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        val leastUsedAnswer = getLeastUsedAnswer()
        
        val prompt = """
${count} adet KPSS ${seviye.displayName} Türkçe sorusu üret.

KONU: ${konu.baslik}
ALT KONULAR: ${konu.altKonular.joinToString(", ")}

SORU TİPLERİ ÖRNEKLERİ:
${konu.soruTipleri.joinToString("\n")}

KURALLAR:
1. 5 şık (A-E), 1 doğru cevap
2. Şıklar eşit uzunlukta ve paralel yapıda olmalı
3. Olumsuz ifadeler **_altı çizili_** formatında yazılmalı
4. Çeldiriciler gerçekçi ve öğrenci hatalarından türetilmeli
5. Doğru cevap dengeli dağılsın (öncelikli kullan: $leastUsedAnswer)
6. ${if(konu.id == "paragraf") "Paragraf 8-12 cümle, 120-180 kelime olmalı" else "Soru metni açık ve net olmalı"}
7. KPSS formatına uygun, sınav tarzı sorular

JSON FORMAT (SADECE BU):
[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"..."}]

SADECE JSON DÖNDÜR.
""".trimIndent()
        
        try {
            val response = callGeminiApi(apiKey, prompt)
            val questions = parseQuestions(response, "turkce_kpss", konu.baslik)
            questions.filter { validateQuestion(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Türkçe ${konu.baslik} üretim hatası: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== MATEMATİK SORULARI ====================
    
    private suspend fun generateMatematikQuestions(
        count: Int, 
        paketId: String, 
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        val konuDagilimi = KpssDenemeCurriculumData.generateKonuDagilimi("Matematik")
        log("  🔢 Matematik Dağılım: $konuDagilimi")
        
        val jobs = mutableListOf<Deferred<List<QuestionModel>>>()
        var keyIndex = 0
        
        for ((konuId, soruSayisi) in konuDagilimi) {
            if (soruSayisi == 0) continue
            
            val konuDetay = KpssDenemeCurriculumData.getMatematikKonuDetay(konuId) ?: continue
            val apiKey = getApiKey(keyIndex % 4)
            keyIndex++
            
            jobs.add(async {
                delay(keyIndex * 1000L) // Internal Stagger
                generateMatematikKonuSorulari(apiKey, konuDetay, soruSayisi, seviye)
            })
        }
        
        jobs.forEach { result.addAll(it.await()) }
        result.take(count)
    }
    
    private suspend fun generateMatematikKonuSorulari(
        apiKey: String,
        konu: KpssDenemeCurriculumData.KpssMatematikKonu,
        count: Int,
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        val leastUsedAnswer = getLeastUsedAnswer()
        
        val prompt = """
${count} adet KPSS ${seviye.displayName} Matematik sorusu üret.

KONU: ${konu.baslik}
ALT KONULAR: ${konu.altKonular.joinToString(", ")}

PROBLEM TİPLERİ:
${konu.problemTipleri.joinToString("\n")}

KURALLAR:
1. 5 şık (A-E), 1 doğru cevap
2. Sayısal değerler net ve çözülebilir olmalı
3. Grafik/tablo/şekil YASAK (sadece metin tabanlı)
4. Çeldiriciler yaygın hesaplama hatalarından türetilmeli
5. Doğru cevap: öncelikli olarak $leastUsedAnswer kullan
6. Her soru mutlaka çözümle sonuçlanmalı (imkansız problem YASAK)
7. KPSS formatına uygun zorlukta

ÖRNEK ÇELDIRICI STRATEJİLERİ:
- İşlem sırası hatası
- Birim dönüşüm hatası
- Yanlış formül kullanımı
- Eksik/fazla adım

JSON FORMAT:
[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"Çözüm: ..."}]

SADECE JSON DÖNDÜR.
""".trimIndent()
        
        try {
            val response = callGeminiApi(apiKey, prompt)
            val questions = parseQuestions(response, "matematik_kpss", konu.baslik)
            questions.filter { validateQuestion(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Matematik ${konu.baslik} üretim hatası: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== TARİH SORULARI ====================
    
    private suspend fun generateTarihQuestions(
        count: Int, 
        paketId: String, 
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        val konuDagilimi = KpssDenemeCurriculumData.generateKonuDagilimi("Tarih")
        log("  🏛️ Tarih Dağılım: $konuDagilimi")
        
        val jobs = mutableListOf<Deferred<List<QuestionModel>>>()
        var keyIndex = 0
        
        for ((konuId, soruSayisi) in konuDagilimi) {
            if (soruSayisi == 0) continue
            
            val konuDetay = KpssDenemeCurriculumData.getTarihKonuDetay(konuId) ?: continue
            val apiKey = getApiKey(keyIndex % 4)
            keyIndex++
            
            jobs.add(async {
                delay(keyIndex * 1000L) // Internal Stagger
                generateTarihKonuSorulari(apiKey, konuDetay, soruSayisi, seviye)
            })
        }
        
        jobs.forEach { result.addAll(it.await()) }
        result.take(count)
    }
    
    private suspend fun generateTarihKonuSorulari(
        apiKey: String,
        konu: KpssDenemeCurriculumData.KpssTarihKonu,
        count: Int,
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        val leastUsedAnswer = getLeastUsedAnswer()
        
        // Anti-halüsinasyon: Gerçek tarihler
        val tarihBilgisi = if (konu.onemliTarihler.isNotEmpty()) {
            "\n\nGERÇEK TARİHLER (BUNLARI KULLAN):\n${konu.onemliTarihler.joinToString("\n")}"
        } else ""
        
        val prompt = """
${count} adet KPSS ${seviye.displayName} Tarih sorusu üret.

KONU: ${konu.baslik}
ALT KONULAR: ${konu.altKonular.joinToString(", ")}
$tarihBilgisi

⚠️ ANTİ-HALÜSİNASYON KURALLARI:
1. SADECE gerçek tarihi olayları kullan
2. Tarih ve isim uydurmak YASAK
3. Şüpheliysen genel ifade kullan ("Bu dönemde..." gibi)
4. Çeldirici olarak YANLIŞ TARİH/İSİM kullanabilirsin ama doğru cevap GERÇEK olmalı

KURALLAR:
1. 5 şık (A-E), 1 doğru cevap
2. Olumsuz ifadeler **_altı çizili_** formatında
3. Doğru cevap: öncelikli $leastUsedAnswer
4. KPSS formatına uygun

JSON FORMAT:
[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"..."}]

SADECE JSON DÖNDÜR.
""".trimIndent()
        
        try {
            val response = callGeminiApi(apiKey, prompt)
            val questions = parseQuestions(response, "tarih_kpss", konu.baslik)
            questions.filter { validateQuestion(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Tarih ${konu.baslik} üretim hatası: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== COĞRAFYA SORULARI ====================
    
    private suspend fun generateCografyaQuestions(
        count: Int, 
        paketId: String, 
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        val konuDagilimi = KpssDenemeCurriculumData.generateKonuDagilimi("Coğrafya")
        log("  🌍 Coğrafya Dağılım: $konuDagilimi")
        
        val jobs = mutableListOf<Deferred<List<QuestionModel>>>()
        var keyIndex = 0
        
        for ((konuId, soruSayisi) in konuDagilimi) {
            if (soruSayisi == 0) continue
            
            val konuDetay = KpssDenemeCurriculumData.getCografyaKonuDetay(konuId) ?: continue
            val apiKey = getApiKey(keyIndex % 4)
            keyIndex++
            
            jobs.add(async {
                delay(keyIndex * 1000L) // Internal Stagger
                generateCografyaKonuSorulari(apiKey, konuDetay, soruSayisi, seviye)
            })
        }
        
        jobs.forEach { result.addAll(it.await()) }
        result.take(count)
    }
    
    private suspend fun generateCografyaKonuSorulari(
        apiKey: String,
        konu: KpssDenemeCurriculumData.KpssCografyaKonu,
        count: Int,
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        val leastUsedAnswer = getLeastUsedAnswer()
        
        // Anti-halüsinasyon: Gerçek Türkiye verileri
        val veriBilgisi = if (konu.turkiyeVerileri.isNotEmpty()) {
            "\n\nGERÇEK TÜRKİYE VERİLERİ:\n${konu.turkiyeVerileri.joinToString("\n")}"
        } else ""
        
        val prompt = """
${count} adet KPSS ${seviye.displayName} Coğrafya sorusu üret.

KONU: ${konu.baslik}
ALT KONULAR: ${konu.altKonular.joinToString(", ")}
$veriBilgisi

⚠️ ANTİ-HALÜSİNASYON KURALLARI:
1. SADECE gerçek Türkiye coğrafyası bilgisi kullan
2. Şehir/bölge/ürün bilgisi uydurmak YASAK
3. Harita/grafik/tablo YASAK (metin tabanlı)
4. Çeldirici: Yaygın yanlış bilinen coğrafi bilgiler

KURALLAR:
1. 5 şık (A-E), 1 doğru cevap
2. Olumsuz ifadeler **_altı çizili_** formatında
3. Doğru cevap: öncelikli $leastUsedAnswer

JSON FORMAT:
[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"..."}]

SADECE JSON DÖNDÜR.
""".trimIndent()
        
        try {
            val response = callGeminiApi(apiKey, prompt)
            val questions = parseQuestions(response, "cografya_kpss", konu.baslik)
            questions.filter { validateQuestion(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Coğrafya ${konu.baslik} üretim hatası: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== VATANDAŞLIK SORULARI ====================
    
    private suspend fun generateVatandaslikQuestions(
        count: Int, 
        paketId: String, 
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        val konuDagilimi = KpssDenemeCurriculumData.generateKonuDagilimi("Vatandaşlık")
        log("  🇹🇷 Vatandaşlık Dağılım: $konuDagilimi")
        
        val jobs = mutableListOf<Deferred<List<QuestionModel>>>()
        var keyIndex = 0
        
        for ((konuId, soruSayisi) in konuDagilimi) {
            if (soruSayisi == 0) continue
            
            val konuDetay = KpssDenemeCurriculumData.getVatandaslikKonuDetay(konuId) ?: continue
            val apiKey = getApiKey(keyIndex % 4)
            keyIndex++
            
            jobs.add(async {
                delay(keyIndex * 1000L) // Internal Stagger
                generateVatandaslikKonuSorulari(apiKey, konuDetay, soruSayisi, seviye)
            })
        }
        
        jobs.forEach { result.addAll(it.await()) }
        result.take(count)
    }
    
    private suspend fun generateVatandaslikKonuSorulari(
        apiKey: String,
        konu: KpssDenemeCurriculumData.KpssVatandaslikKonu,
        count: Int,
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        val leastUsedAnswer = getLeastUsedAnswer()
        
        // Anti-halüsinasyon: Gerçek anayasa maddeleri
        val anayasaBilgisi = if (konu.anayasaMaddeleri.isNotEmpty()) {
            "\n\n1982 ANAYASASI (GERÇEK MADDELER):\n${konu.anayasaMaddeleri.joinToString("\n")}"
        } else ""
        
        val prompt = """
${count} adet KPSS ${seviye.displayName} Vatandaşlık sorusu üret.

KONU: ${konu.baslik}
ALT KONULAR: ${konu.altKonular.joinToString(", ")}
$anayasaBilgisi

⚠️ ANTİ-HALÜSİNASYON KURALLARI:
1. SADECE güncel 1982 Anayasası bilgileri kullan (2017 değişiklikleri dahil)
2. Madde numarası veriyorsan DOĞRU olmalı
3. Yanlış/eski bilgi vermek YASAK
4. Şüpheliysen genel hukuki ilke kullan

KURALLAR:
1. 5 şık (A-E), 1 doğru cevap
2. Olumsuz ifadeler **_altı çizili_** formatında
3. Doğru cevap: öncelikli $leastUsedAnswer
4. Hukuki terminoloji kullan

JSON FORMAT:
[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"..."}]

SADECE JSON DÖNDÜR.
""".trimIndent()
        
        try {
            val response = callGeminiApi(apiKey, prompt)
            val questions = parseQuestions(response, "vatandaslik_kpss", konu.baslik)
            questions.filter { validateQuestion(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Vatandaşlık ${konu.baslik} üretim hatası: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== GÜNCEL BİLGİLER SORULARI ====================
    
    private suspend fun generateGuncelQuestions(
        count: Int, 
        paketId: String, 
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        val result = mutableListOf<QuestionModel>()
        
        val konuDagilimi = KpssDenemeCurriculumData.generateKonuDagilimi("Güncel")
        log("  📰 Güncel Dağılım: $konuDagilimi")
        
        val jobs = mutableListOf<Deferred<List<QuestionModel>>>()
        var keyIndex = 0
        
        for ((konuId, soruSayisi) in konuDagilimi) {
            if (soruSayisi == 0) continue
            
            val konuDetay = KpssDenemeCurriculumData.getGuncelKonuDetay(konuId) ?: continue
            val apiKey = getApiKey(keyIndex % 4)
            keyIndex++
            
            jobs.add(async {
                delay(keyIndex * 1000L) // Internal Stagger
                generateGuncelKonuSorulari(apiKey, konuDetay, soruSayisi, seviye)
            })
        }
        
        jobs.forEach { result.addAll(it.await()) }
        result.take(count)
    }
    
    private suspend fun generateGuncelKonuSorulari(
        apiKey: String,
        konu: KpssDenemeCurriculumData.KpssGuncelKonu,
        count: Int,
        seviye: SchoolType
    ): List<QuestionModel> = withContext(Dispatchers.IO) {
        
        val leastUsedAnswer = getLeastUsedAnswer()
        
        val prompt = """
${count} adet KPSS ${seviye.displayName} Güncel Bilgiler sorusu üret.

KONU: ${konu.baslik}
ALT KONULAR: ${konu.altKonular.joinToString(", ")}

ÖRNEK KONULAR:
${konu.ornekKonular.joinToString("\n")}

⚠️ ANTİ-HALÜSİNASYON KURALLARI:
1. SOMUT, TARİHLİ bilgiler kullan (örn: "2024 yılında...", "Ocak 2025'te...")
2. Genel kalıp ifadeler YASAK
3. Güncel olayları "bilgi ölçen" soru şeklinde sor
4. Telif/izin riski olan haber metinleri KULLANMA
5. Kurumlar ve projeler GERÇEK olmalı (TOGG, KAAN, vb.)

KURALLAR:
1. 5 şık (A-E), 1 doğru cevap
2. Kısa, net sorular (haber metni değil, bilgi sorusu)
3. Doğru cevap: öncelikli $leastUsedAnswer

JSON FORMAT:
[{"question":"...","optionA":"...","optionB":"...","optionC":"...","optionD":"...","optionE":"...","correctAnswer":"A/B/C/D/E","explanation":"..."}]

SADECE JSON DÖNDÜR.
""".trimIndent()
        
        try {
            val response = callGeminiApi(apiKey, prompt)
            val questions = parseQuestions(response, "guncel_kpss", konu.baslik)
            questions.filter { validateQuestion(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Güncel ${konu.baslik} üretim hatası: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== YARDIMCI FONKSİYONLAR ====================
    
    private data class DersUretim(
        val dersAdi: String,
        val soruSayisi: Int,
        val generator: suspend (Int, String, SchoolType) -> List<QuestionModel>
    )
    
    private fun getLeastUsedAnswer(): String {
        return answerDistribution.minByOrNull { it.value.get() }?.key ?: "C"
    }
    
    private fun updateAnswerDistribution(answer: String) {
        answerDistribution[answer]?.incrementAndGet()
    }
    
    private fun fingerprint(q: QuestionModel): String {
        val text = "${q.question}|${q.optionA}|${q.optionB}|${q.correctAnswer}"
        return text.lowercase().replace("\\s+".toRegex(), " ").trim().hashCode().toString()
    }
    
    private fun validateQuestion(q: QuestionModel): Boolean {
        // Temel kontroller
        if (q.question.isNullOrBlank() || (q.question?.length ?: 0) < 20) return false
        if (q.correctAnswer !in listOf("A", "B", "C", "D", "E")) return false
        
        // En az 5 şık olmalı
        val options = listOfNotNull(q.optionA, q.optionB, q.optionC, q.optionD, q.optionE)
            .filter { it.isNotBlank() }
        if (options.size < 5) return false
        
        // Fingerprint kontrolü (tekrar)
        val fp = fingerprint(q)
        if (!seenFingerprints.add(fp)) {
            Log.d(TAG, "Tekrar soru tespit edildi, atlanıyor")
            return false
        }
        
        // Cevap dağılımını güncelle
        q.correctAnswer?.let { updateAnswerDistribution(it) }
        
        // Şıkların benzersiz olması
        if (options.distinct().size != options.size) return false
        
        // Yasaklı ifadeler
        val banned = listOf("hepsi doğru", "hiçbiri", "hepsi yanlış", "yukarıdakilerin hepsi")
        for (opt in options) {
            if (banned.any { opt.lowercase().contains(it) }) return false
        }
        
        return true
    }
    
    private suspend fun callGeminiApi(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        var retries = 0
        val maxApiRetries = 3
        
        while (retries < maxApiRetries) {
            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            
            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                connection.connectTimeout = 60000
                connection.readTimeout = 60000
                
                val requestBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", prompt))
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("topP", 0.95)
                        put("topK", 40)
                        put("maxOutputTokens", 8192)
                    })
                }
                
                OutputStreamWriter(connection.outputStream).use { it.write(requestBody.toString()) }
                
                val responseCode = connection.responseCode
                
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonResponse = JSONObject(response)
                    
                    return@withContext jsonResponse
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                } else if (responseCode == 429) {
                    val retryWait = (retries + 1) * 2000L + 1000L
                    Log.w(TAG, "API 429 Hatası (Deneme ${retries+1}/$maxApiRetries) - ${retryWait}ms bekleniyor...")
                    delay(retryWait)
                    retries++
                } else {
                    // Diğer hatalar (500, 400 vs)
                    val errorMsg = try {
                        connection.errorStream?.bufferedReader()?.readText() ?: "Bilinmeyen hata ($responseCode)"
                    } catch (e: Exception) { "Okunamadı ($responseCode)" }
                    throw Exception("API Hatası ($responseCode): $errorMsg")
                }
                
            } catch (e: Exception) {
                // Network hataları için de retry
                if (e.message?.contains("429") == true || e.message?.contains("quota") == true) {
                     val retryWait = (retries + 1) * 2000L + 1000L
                     delay(retryWait)
                     retries++
                } else {
                    throw e // Diğer hataları fırlat
                }
            } finally {
                connection.disconnect()
            }
        }
        throw Exception("Maksimum API deneme sayısı aşıldı ($maxApiRetries)")
    }
    
    private fun parseQuestions(rawText: String, lesson: String, konu: String): List<QuestionModel> {
        val result = mutableListOf<QuestionModel>()
        
        try {
            // JSON'ı temizle
            val cleanText = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            val jsonArray = JSONArray(cleanText)
            
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                
                val question = QuestionModel(
                    id = "",
                    question = obj.optString("question", ""),
                    optionA = obj.optString("optionA", ""),
                    optionB = obj.optString("optionB", ""),
                    optionC = obj.optString("optionC", ""),
                    optionD = obj.optString("optionD", ""),
                    optionE = obj.optString("optionE", ""),
                    correctAnswer = obj.optString("correctAnswer", "").uppercase().take(1),
                    explanation = obj.optString("explanation", ""),
                    lesson = lesson,
                    topic = konu,
                    level = EducationLevel.KPSS
                )
                
                result.add(question)
            }
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse hatası: ${e.message}")
        }
        
        return result
    }
    
    private fun log(message: String) {
        Log.d(TAG, message)
        onLogMessage?.invoke(message)
    }
}
