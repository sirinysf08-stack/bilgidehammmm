package com.example.bilgideham

// Firestore'un veriyi okuyabilmesi için tüm alanların varsayılan değeri (= "") olmak ZORUNDADIR.
data class QuestionModel(
    val id: String = "",  // Firestore document ID
    val questionNumber: Int = 0,  // Soru numarası (deneme için)
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val optionE: String = "",  // 5. şık (ÖSYM standardı)
    val correctAnswer: String = "",
    val explanation: String = "",
    val lesson: String = "",
    val topic: String = "",  // Alt konu (paragraf, problemler vb.)
    val level: EducationLevel? = null,  // Eğitim seviyesi

    // SVG Grafik desteği
    val graphicType: String = "",  // "numberLine", "pieChart", "table", "grid", "coordinate", "venn"
    val graphicData: String = "",  // JSON formatında grafik verileri

    // Görsel opsiyonel (Imagen)
    val needsImage: Boolean = false,
    val imagePrompt: String = "",

    // Imagen sonucu
    val imageBase64: String? = null,
    val imageMimeType: String? = null
)
