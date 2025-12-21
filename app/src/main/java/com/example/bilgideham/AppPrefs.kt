package com.example.bilgideham

import android.content.Context
import androidx.core.content.edit
// ✅ AŞAĞIDAKİ SATIR EKSİK OLDUĞU İÇİN HATA ALIYORDUNUZ
import com.example.bilgideham.ui.theme.AppThemeId

object AppPrefs {

    private const val PREFS_NAME = "bilgideham_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_THEME_ID = "theme_id"
    private const val KEY_READING_LEVEL = "reading_level"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getDarkMode(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, value: Boolean) {
        prefs(context).edit {
            putBoolean(KEY_DARK_MODE, value)
        }
    }

    fun getTheme(context: Context): AppThemeId {
        val stored = prefs(context).getString(KEY_THEME_ID, null)
        return if (stored.isNullOrEmpty()) {
            // Varsayılan tema
            AppThemeId.ROYAL_ACADEMY
        } else {
            // Hata olursa veya eski veri kalmışsa varsayılan temaya dön
            runCatching { AppThemeId.valueOf(stored) }.getOrElse { AppThemeId.ROYAL_ACADEMY }
        }
    }

    fun setTheme(context: Context, themeId: AppThemeId) {
        prefs(context).edit {
            putString(KEY_THEME_ID, themeId.name)
        }
    }

    fun getReadingLevel(context: Context): Int {
        return prefs(context).getInt(KEY_READING_LEVEL, 0).coerceIn(0, 3)
    }

    fun setReadingLevel(context: Context, level: Int) {
        prefs(context).edit {
            putInt(KEY_READING_LEVEL, level.coerceIn(0, 3))
        }
    }
}