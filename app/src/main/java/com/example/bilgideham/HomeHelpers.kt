package com.example.bilgideham

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

/**
 * Pure Helper Functions extracted from HomeScreen.kt
 * These functions don't use @Composable and can be called from anywhere.
 */

/**
 * Creates a gradient brush for the top header based on theme mode.
 */
fun resolveTopGradient(
    cs: ColorScheme,
    darkMode: Boolean
): Brush {
    return if (darkMode) {
        Brush.verticalGradient(listOf(Color(0xFF0B1220), Color(0xFF1E293B), Color(0xFF0F172A)))
    } else {
        Brush.verticalGradient(listOf(cs.primary, cs.tertiary))
    }
}

/**
 * Opens email client with pre-filled bug report template.
 */
fun openBugReportEmail(context: Context, appVersionName: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("bilgideham@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Hata Bildirimi (v$appVersionName)")
    }
    runCatching { context.startActivity(Intent.createChooser(intent, "Hata Bildir")) }
}

/**
 * Reads the personalized brand title from SharedPreferences.
 */
fun readBrandTitle(context: Context): String {
    val prefs = context.getSharedPreferences("bilgideham_prefs", Context.MODE_PRIVATE)
    val name = (prefs.getString("student_name", "") ?: "").trim()
    val style = prefs.getString("brand_style", "Küpü") ?: "Küpü"
    if (name.isEmpty()) return "Akıl Küpü"
    val vowels = setOf('a', 'e', 'ı', 'i', 'o', 'ö', 'u', 'ü', 'A', 'E', 'I', 'İ', 'O', 'Ö', 'U', 'Ü')
    val suffix = if (vowels.contains(name.last())) "'nın" else "'in"
    return "$name$suffix $style"
}

/**
 * Extension function for safe navigation with error handling.
 * Drawer click stabilization: Direct and safe navigation.
 */
fun NavController.safeNavigateDeferred(route: String, context: Context) {
    try {
        navigate(route) {
            launchSingleTop = true
            restoreState = true
        }
    } catch (e: Exception) {
        Log.e("NAV_SAFE", "Navigate başarısız. route=$route err=${e.message}", e)
        Toast.makeText(context, "Sayfa yüklenemedi, lütfen tekrar deneyin.", Toast.LENGTH_SHORT).show()
    }
}
