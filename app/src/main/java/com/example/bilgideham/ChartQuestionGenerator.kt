package com.example.bilgideham

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Grafikli soru üretici
 * MEB müfredatına uygun, halüsinasyon içermeyen grafik soruları üretir
 */
object ChartQuestionGenerator {
    
    private const val TAG = "ChartQuestionGenerator"
    private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-pro-preview:generateContent"
    
    // API key'leri GeminiApiProvider'dan al
    private var apiKey: String = ""
    
    fun setApiKey(key: String) {
        apiKey = key
    }
    
    /**
     * Grafik sorusu üret
     * @param chartType bar, line, pie
     * @param grade Sınıf seviyesi (5-12)
     * @param subject Ders adı
     * @param topic Konu (opsiyonel)
     */
    suspend fun generateChartQuestion(
        context: Context,
        chartType: String,
        grade: Int,
        subject: String,
        topic: String = ""
    ): ChartQuestionModel? = withContext(Dispatchers.IO) {
        
        // API key'i GeminiApiProvider'dan al
        GeminiApiProvider.loadKeysFromAssets(context)
        val keys = GeminiApiProvider.getLoadedKeyCount()
        if (keys == 0) {
            Log.e(TAG, "API key bulunamadı")
            return@withContext null
        }
        
        try {
            val prompt = buildChartPrompt(chartType, grade, subject, topic)
            val response = GeminiApiProvider.callGeminiApi(GeminiApiProvider.getFirstKey() ?: "", prompt)
            
            if (response.isNotEmpty()) {
                parseChartQuestion(response, chartType, grade, subject)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Grafik soru üretimi hatası: ${e.message}")
            null
        }
    }
    
    /**
     * Toplu grafik sorusu üret (Parallel)
     */
    suspend fun generateBatchChartQuestions(
        context: Context,
        chartType: String,
        grade: Int,
        subject: String,
        count: Int = 5,
        onProgress: (Int, Int) -> Unit
    ): List<ChartQuestionModel> = withContext(Dispatchers.IO) {
        // Çeşitlilik için odak noktaları
        val variations = listOf(
            "En büyük/küçük değeri bulmaya odaklan",
            "Verilerin toplamını veya ortalamasını hesaplamaya odaklan",
            "İki veri arasındaki farkı veya oranı bulmaya odaklan",
            "Veri setindeki genel eğilimi veya trendi yorumlamaya odaklan",
            "Belirli bir şarta uyan verileri bulmaya odaklan"
        )
        
        val results = mutableListOf<ChartQuestionModel>()
        val jobs: List<kotlinx.coroutines.Deferred<ChartQuestionModel?>> = List(count) { index: Int ->
            async {
                // Her soru için farklı bir varyasyon/seed kullan
                val variation = variations[index % variations.size]
                
                // Eğer "random" seçildiyse her iterasyonda farklı bir tip seç
                val effectiveChartType = if (chartType == "random") {
                    val types = listOf("bar", "line", "pie")
                    types[index % types.size]
                } else {
                    chartType
                }
                
                val q = generateChartQuestion(context, effectiveChartType, grade, subject, "Varyasyon: $variation")
                if (q != null) {
                    synchronized(results) {
                        results.add(q)
                    }
                    withContext(Dispatchers.Main) {
                        onProgress(results.size, count)
                    }
                }
                q
            }
        }
        jobs.awaitAll().filterNotNull()
    }
    
    /**
     * Anti-halüsinasyon prompt builder
     */
    private fun buildChartPrompt(chartType: String, grade: Int, subject: String, topic: String): String {
        val chartTypeDesc = when (chartType) {
            "bar" -> "sütun grafik (bar chart)"
            "line" -> "çizgi grafik (line chart)"
            "pie" -> "pasta grafik (pie chart)"
            else -> "sütun grafik"
        }
        
        val gradeDesc = when {
            grade <= 4 -> "ilkokul ${grade}. sınıf"
            grade <= 8 -> "ortaokul ${grade}. sınıf"
            else -> "lise ${grade}. sınıf"
        }
        
        val optionCount = if (grade >= 9) 5 else 4
        val optionLabels = if (optionCount == 5) "A, B, C, D, E" else "A, B, C, D"
        
        // Topic (varyasyon) varsa ekle
        val topicInstruction = if (topic.isNotEmpty()) "\n🎯 ODAK NOKTASI: $topic" else ""
        
        return """
Sen bir $gradeDesc $subject öğretmenisin. $chartTypeDesc içeren bir test sorusu hazırla.
$topicInstruction

⚠️ MİSYONUN:
- Öğrencileri düşündüren, net ve anlaşılır sorular hazırla.
- Üslubun "Öğretici ve Profesyonel" olsun. Çok resmi (akademik) olma, ama çok laubali de olma.
- Öğrencinin seviyesine ($gradeDesc) uygun, cesaretlendirici bir dil kullan.

⚠️ KRİTİK KURALLAR:
1. VERİ TUTARLILIĞI: Ürettiğin sayısal veriler, soru ve doğru cevap birbiriyle TAMAMEN UYUMLU olmalı
2. MATEMATİKSEL DOĞRULUK: Toplam, ortalama, yüzde hesapları DOĞRU olmalı
3. KISA ETİKETLER: Grafik kategori isimleri (X ekseni) ÇOK KISA olmalı (max 1-2 kelime). Örn: "Ocak", "Ali", "İstanbul" gibi. Asla uzun cümle kullanma.
4. DOĞRU CEVAP: Veriden hesaplanabilir, "yaklaşık" veya "tahmini" ifade KULLANMA
5. ÇELDİRİCİLER: Mantıklı ama yanlış olmalı (örn: yanlış hesaplama sonuçları)

📊 GRAFİK VERİSİ:
- 4-6 kategori/zaman noktası kullan
- EĞER ZAMAN VERİSİ VARSA (Gün, Ay, Yıl) MUTLAKA KRONOLOJİK SIRAYA DİZ (Pzt, Sal, Çar... veya Ocak, Şubat...)
- Sayılar sınıf seviyesine uygun olsun
- $gradeDesc için anlaşılır değerler kullan


📝 SORU FORMAT:
- Grafiğe bakarak cevaplanabilecek bir soru
- ${if(topic.contains("Varyasyon")) subject else topic.ifEmpty { subject }} konusuyla ilgili
- $optionLabels şıkları olacak

⚠️ ÇOK ÖNEMLİ - YASAKLI KELİMELER:
- ASLA "Yukarıdaki grafik", "Aşağıdaki tablo", "Yandaki şekil" gibi yön bildiren ifadeler KULLANMA.
- Bunun yerine "Grafiğe göre", "Bu grafikte", "Verilen bilgilere göre" gibi nötr ifadeler kullan.

🎯 JSON FORMAT (TEK OBJE):
{
    "chartData": [
        {"kategori": "...", "deger": sayı},
        ...
    ],
    "chartTitle": "Grafik Başlığı",
    "question": "Soru metni...",
    "optionA": "A şıkkı",
    "optionB": "B şıkkı",
    "optionC": "C şıkkı",
    "optionD": "D şıkkı",
    ${if (optionCount == 5) "\"optionE\": \"E şıkkı\"," else ""}
    "correctAnswer": "Doğru şık harfi",
    "explanation": "Çözüm: (SADECE sorunun adım adım çözümünü ve cevabını yaz. 'JSON güncellendi', 'Cevap ektedir' veya 'Umarım beğenirsiniz' gibi yapay zeka sohbet ifadeleri ASLA YAZMA.)"
}

SADECE JSON DÖNDÜR, ek açıklama veya sohbet metni ekleme.
""".trimIndent()
    }
    
    // ... API call methods ... (unchanged)

    private fun parseChartQuestion(
        jsonString: String, 
        chartType: String, 
        grade: Int, 
        subject: String
    ): ChartQuestionModel? {
        try {
            // Markdown code block işaretlerini temizle
            val cleanJson = jsonString
                .replace("```json", "")
                .replace("```", "")
                .trim()
                
            val jsonStart = cleanJson.indexOf('{')
            val jsonEnd = cleanJson.lastIndexOf('}')
            
            if (jsonStart == -1 || jsonEnd == -1) {
                Log.e(TAG, "Geçersiz JSON formatı: $jsonString")
                return null
            }
            
            val validJson = cleanJson.substring(jsonStart, jsonEnd + 1)
            val obj = JSONObject(validJson)
            
            val chartData = obj.getJSONArray("chartData")
            val chartTitle = obj.optString("chartTitle", "Grafik")
            
            // Vega-Lite spec oluştur
            val vegaSpec = when (chartType) {
                "bar" -> buildBarVegaSpec(chartTitle, chartData)
                "line" -> buildLineVegaSpec(chartTitle, chartData)
                "pie" -> buildPieVegaSpec(chartTitle, chartData)
                else -> buildBarVegaSpec(chartTitle, chartData)
            }
            
            return ChartQuestionModel(
                question = obj.getString("question"),
                optionA = obj.getString("optionA"),
                optionB = obj.getString("optionB"),
                optionC = obj.getString("optionC"),
                optionD = obj.getString("optionD"),
                optionE = obj.optString("optionE", ""),
                correctAnswer = obj.getString("correctAnswer").uppercase(),
                explanation = obj.getString("explanation"),
                chartData = chartData.toString(),
                vegaSpec = vegaSpec,
                chartType = chartType,
                grade = grade,
                subject = subject  // Eksik parametre eklendi
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse hatası: ${e.message}")
            return null
        }
    }
   
    
    private fun buildBarVegaSpec(title: String, data: JSONArray): String {
        return """
{
  "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "title": "$title",
  "width": "container",
  "height": 180,
  "data": {"values": $data},
  "mark": {"type": "bar", "cornerRadius": 4},
  "encoding": {
    "x": {
      "field": "kategori", 
      "type": "nominal", 
      "title": null,
      "axis": {"labelAngle": -45, "labelLimit": 80}
    },
    "y": {"field": "deger", "type": "quantitative", "title": "Değer"},
    "color": {"field": "kategori", "type": "nominal", "legend": null}
  }
}
""".trimIndent()
    }
    
    private fun buildLineVegaSpec(title: String, data: JSONArray): String {
        return """
{
  "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "title": "$title",
  "width": "container",
  "height": 180,
  "data": {"values": $data},
  "mark": {"type": "line", "point": true},
  "encoding": {
    "x": {
      "field": "kategori", 
      "type": "ordinal", 
      "title": null,
      "axis": {"labelAngle": -45, "labelLimit": 80}
    },
    "y": {"field": "deger", "type": "quantitative", "title": "Değer"}
  }
}
""".trimIndent()
    }
    
    private fun buildPieVegaSpec(title: String, data: JSONArray): String {
        return """
{
  "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
  "title": "$title",
  "width": "container",
  "height": 200,
  "data": {"values": $data},
  "mark": {"type": "arc", "innerRadius": 0},
  "encoding": {
    "theta": {"field": "deger", "type": "quantitative"},
    "color": {"field": "kategori", "type": "nominal", "title": null}
  }
}
""".trimIndent()
    }
}

// GeminiApiProvider'a eklenmesi gereken helper fonksiyon
// GeminiApiProvider.kt'ye ekle:
/*
fun getFirstKey(): String? {
    return if (API_KEYS.isNotEmpty()) API_KEYS[0] else null
}
*/
