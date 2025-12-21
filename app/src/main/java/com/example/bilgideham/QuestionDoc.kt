package com.example.bilgideham

data class QuestionDoc(
    val id: String = "",
    val poolKey: String = "",
    val subject: String = "",
    val type: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val teacherNote: String = "",
    val difficulty: Int = 4, // 1-5
    val createdAtMs: Long = System.currentTimeMillis()
)
