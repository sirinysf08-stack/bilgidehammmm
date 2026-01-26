package com.example.bilgideham

import android.content.Context

object ParentalPrefs {
    private const val PREFS_NAME = "parental_prefs"
    
    private const val KEY_PIN = "parental_pin"
    private const val KEY_HINT_QUESTION = "hint_question"
    private const val KEY_HINT_ANSWER = "hint_answer"
    private const val KEY_RECOVERY_EMAIL = "recovery_email"

    private fun getPrefs(context: Context) = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- PIN Management ---
    fun hasPin(context: Context): Boolean {
        return getPrefs(context).getString(KEY_PIN, null) != null
    }

    fun validatePin(context: Context, inputPin: String): Boolean {
        val storedPin = getPrefs(context).getString(KEY_PIN, "")
        return storedPin == inputPin
    }

    fun setPin(context: Context, pin: String) {
        getPrefs(context).edit().putString(KEY_PIN, pin).apply()
    }

    // --- Secret Hint ---
    fun setSecretHint(context: Context, question: String, answer: String) {
        getPrefs(context).edit()
            .putString(KEY_HINT_QUESTION, question)
            .putString(KEY_HINT_ANSWER, answer.lowercase().trim())
            .apply()
    }

    fun getHintQuestion(context: Context): String? {
        return getPrefs(context).getString(KEY_HINT_QUESTION, null)
    }

    fun validateHintAnswer(context: Context, inputAnswer: String): Boolean {
        val storedAnswer = getPrefs(context).getString(KEY_HINT_ANSWER, "")
        return storedAnswer == inputAnswer.lowercase().trim()
    }

    // --- Recovery Email ---
    fun setRecoveryEmail(context: Context, email: String) {
        getPrefs(context).edit().putString(KEY_RECOVERY_EMAIL, email).apply()
    }
    
    fun getRecoveryEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_RECOVERY_EMAIL, null)
    }

    // --- Support ID & Reset ---
    private const val KEY_SUPPORT_ID = "support_id"

    fun getSupportId(context: Context): String {
        var id = getPrefs(context).getString(KEY_SUPPORT_ID, null)
        if (id == null) {
            // Generate random 8 char ID (e.g. A1B2-C3D4)
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val sb = StringBuilder()
            for (i in 0 until 8) {
                sb.append(chars.random())
                if (i == 3) sb.append("-")
            }
            id = sb.toString()
            getPrefs(context).edit().putString(KEY_SUPPORT_ID, id).apply()
        }
        return id
    }

    fun validateMasterKey(context: Context, inputKey: String): Boolean {
        // Simple master key check based on Support ID
        // Logic: First 4 chars of Support ID reversed + "BH"
        // Example ID: A1B2-C3D4 -> 2B1ABH
        val supportId = getSupportId(context)
        val rawId = supportId.replace("-", "")
        val prefix = rawId.take(4).reversed()
        val expected = "${prefix}BH"
        return inputKey == expected
    }
}
