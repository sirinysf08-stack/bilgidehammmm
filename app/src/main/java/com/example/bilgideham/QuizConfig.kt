package com.example.bilgideham

data class QuizConfig(
    val title: String,
    val aiType: String,               // ⭐ Yapay zekâ soru tipi
    val targetCount: Int,             // Kaç soru üretilecek
    val secondsPerQuestion: Int       // Süre
)
