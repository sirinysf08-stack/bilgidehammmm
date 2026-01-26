package com.example.bilgideham

import com.google.firebase.firestore.DocumentId

/**
 * Grafikli soru modeli
 * Vega-Lite spec ile client-side render edilir
 */
data class ChartQuestionModel(
    @DocumentId
    val id: String = "",
    
    // Soru metni
    val question: String = "",
    
    // Şıklar
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val optionE: String = "",
    
    // Doğru cevap (A, B, C, D, E)
    val correctAnswer: String = "",
    
    // Açıklama
    val explanation: String = "",
    
    // Grafik verisi (JSON string)
    val chartData: String = "",
    
    // Vega-Lite spec (JSON string)
    val vegaSpec: String = "",
    
    // Grafik tipi: bar, line, pie
    val chartType: String = "bar",
    
    // Meta bilgiler
    val grade: Int = 5,
    val subject: String = "matematik",
    val lesson: String = "",
    val difficulty: String = "orta", // kolay, orta, zor
    
    // Zaman damgası
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        // Grafik tipleri
        const val CHART_BAR = "bar"
        const val CHART_LINE = "line"
        const val CHART_PIE = "pie"
        
        // Örnek Vega-Lite bar chart spec
        fun createBarSpec(title: String, data: List<Map<String, Any>>): String {
            val dataJson = data.joinToString(",") { item ->
                """{"kategori": "${item["kategori"]}", "deger": ${item["deger"]}}"""
            }
            
            return """
            {
              "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
              "title": "$title",
              "width": 300,
              "height": 200,
              "data": {
                "values": [$dataJson]
              },
              "mark": "bar",
              "encoding": {
                "x": {"field": "kategori", "type": "nominal", "title": "Kategori"},
                "y": {"field": "deger", "type": "quantitative", "title": "Değer"},
                "color": {"field": "kategori", "type": "nominal", "legend": null}
              }
            }
            """.trimIndent()
        }
        
        // Örnek Vega-Lite line chart spec
        fun createLineSpec(title: String, data: List<Map<String, Any>>): String {
            val dataJson = data.joinToString(",") { item ->
                """{"zaman": "${item["zaman"]}", "deger": ${item["deger"]}}"""
            }
            
            return """
            {
              "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
              "title": "$title",
              "width": 300,
              "height": 200,
              "data": {
                "values": [$dataJson]
              },
              "mark": {"type": "line", "point": true},
              "encoding": {
                "x": {"field": "zaman", "type": "ordinal", "title": "Zaman"},
                "y": {"field": "deger", "type": "quantitative", "title": "Değer"}
              }
            }
            """.trimIndent()
        }
        
        // Örnek Vega-Lite pie chart spec
        fun createPieSpec(title: String, data: List<Map<String, Any>>): String {
            val dataJson = data.joinToString(",") { item ->
                """{"kategori": "${item["kategori"]}", "deger": ${item["deger"]}}"""
            }
            
            return """
            {
              "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
              "title": "$title",
              "width": 300,
              "height": 200,
              "data": {
                "values": [$dataJson]
              },
              "mark": {"type": "arc", "innerRadius": 0},
              "encoding": {
                "theta": {"field": "deger", "type": "quantitative"},
                "color": {"field": "kategori", "type": "nominal", "title": "Kategori"}
              }
            }
            """.trimIndent()
        }
    }
}
