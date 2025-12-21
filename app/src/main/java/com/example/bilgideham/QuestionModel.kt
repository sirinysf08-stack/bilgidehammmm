package com.example.bilgideham

// Firestore'un veriyi okuyabilmesi için tüm alanların varsayılan değeri (= "") olmak ZORUNDADIR.
data class QuestionModel(
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String = "",
    val explanation: String = "",
    val lesson: String = "",

    // Görsel opsiyonel
    val needsImage: Boolean = false,
    val imagePrompt: String = "",

    // Imagen sonucu
    val imageBase64: String? = null,
    val imageMimeType: String? = null
)