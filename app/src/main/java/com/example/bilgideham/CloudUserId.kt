// CloudUserId.kt
package com.example.bilgideham

import android.content.Context
import java.util.UUID

/**
 * Firebase Auth kullanılmayan senaryolarda dahi bulutta kullanıcıyı ayırt etmek için
 * cihaz/kurulum bazlı stabil bir kimlik üretir ve saklar.
 *
 * Not: Bu UID "kurulum bazlı"dır; farklı cihazlarda aynı kullanıcı için birleşmez.
 * Eğer cross-device tekillik istenirse ayrıca Firebase Auth (anon/login) ile UID standardize edilmelidir.
 */
object CloudUserId {

    private const val PREF_NAME = "cloud_user_id_prefs"
    private const val KEY_USER_ID = "cloud_user_id"

    fun getOrCreate(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_USER_ID, null)
        if (!existing.isNullOrBlank()) return existing

        val created = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_USER_ID, created).apply()
        return created
    }
}
