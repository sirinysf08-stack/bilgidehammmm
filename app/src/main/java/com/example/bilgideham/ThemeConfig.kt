package com.example.bilgideham.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Arayüz Tipleri - 3 farklı UI stili
 * NOT: COSMIC teması performans optimizasyonu için kaldırıldı (2026-01-23)
 */
enum class InterfaceStyle(
    val displayName: String,
    val description: String,
    val icon: String
) {
    MODERN("Modern", "Temiz ve minimal tasarım", "✨"),
    PLAYFUL("Eğlenceli", "Renkli ve canlı tasarım", "🎨"),
    CLASSIC("Klasik", "Geleneksel ve sade tasarım", "📚"),
    NEURAL_LUX("Neural Lux", "AI + eğitim odaklı premium görünüm", "🧠");
    
    companion object {
        // Eski COSMIC kullanıcıları için fallback
        @Deprecated("COSMIC teması kaldırıldı, MODERN kullanın")
        val COSMIC = MODERN
    }
}

/**
 * Tema Renkleri - Her arayüz için 5 tema
 */
enum class ThemeColor(
    val displayName: String,
    val icon: String,
    val primaryLight: Long,
    val secondaryLight: Long,
    val backgroundLight: Long,
    val primaryDark: Long,
    val secondaryDark: Long,
    val backgroundDark: Long
) {
    // Mavi tonları
    OCEAN(
        "Okyanus", "🌊",
        0xFF1976D2, 0xFF64B5F6, 0xFFF5F9FF,
        0xFF42A5F5, 0xFF90CAF9, 0xFF0D1B2A
    ),

    // Yeşil tonları
    FOREST(
        "Orman", "🌲",
        0xFF2E7D32, 0xFF81C784, 0xFFF1F8E9,
        0xFF66BB6A, 0xFFA5D6A7, 0xFF0D1F0D
    ),

    // Mor tonları
    GALAXY(
        "Galaksi", "🌌",
        0xFF7B1FA2, 0xFFBA68C8, 0xFFF3E5F5,
        0xFFAB47BC, 0xFFCE93D8, 0xFF1A0D1F
    ),

    // Turuncu tonları
    SUNSET(
        "Gün Batımı", "🌅",
        0xFFE65100, 0xFFFFB74D, 0xFFFFF3E0,
        0xFFFF9800, 0xFFFFCC80, 0xFF1F1408
    ),

    // Pembe tonları
    SAKURA(
        "Sakura", "🌸",
        0xFFD81B60, 0xFFF48FB1, 0xFFFCE4EC,
        0xFFEC407A, 0xFFF8BBD9, 0xFF1F0D14
    )
}

/**
 * Tam tema konfigürasyonu
 */
data class FullThemeConfig(
    val interfaceStyle: InterfaceStyle,
    val themeColor: ThemeColor,
    val isDarkMode: Boolean
) {
    companion object {
        val DEFAULT = FullThemeConfig(
            interfaceStyle = InterfaceStyle.MODERN,
            themeColor = ThemeColor.OCEAN,
            isDarkMode = false
        )
    }
}

/**
 * Arayüz stiline göre UI parametreleri
 */
object InterfaceParams {

    fun getCornerRadius(style: InterfaceStyle): Int = when (style) {
        InterfaceStyle.MODERN -> 24
        InterfaceStyle.PLAYFUL -> 32
        InterfaceStyle.CLASSIC -> 12
        InterfaceStyle.NEURAL_LUX -> 28
    }

    fun getCardElevation(style: InterfaceStyle): Int = when (style) {
        InterfaceStyle.MODERN -> 4
        InterfaceStyle.PLAYFUL -> 8
        InterfaceStyle.CLASSIC -> 2
        InterfaceStyle.NEURAL_LUX -> 6
    }

    fun getSpacing(style: InterfaceStyle): Int = when (style) {
        InterfaceStyle.MODERN -> 16
        InterfaceStyle.PLAYFUL -> 20
        InterfaceStyle.CLASSIC -> 12
        InterfaceStyle.NEURAL_LUX -> 18
    }

    fun getFontScale(style: InterfaceStyle): Float = when (style) {
        InterfaceStyle.MODERN -> 1.0f
        InterfaceStyle.PLAYFUL -> 1.05f
        InterfaceStyle.CLASSIC -> 0.95f
        InterfaceStyle.NEURAL_LUX -> 1.02f
    }

    fun useAnimations(style: InterfaceStyle): Boolean = when (style) {
        InterfaceStyle.MODERN -> true
        InterfaceStyle.PLAYFUL -> true
        InterfaceStyle.CLASSIC -> false
        InterfaceStyle.NEURAL_LUX -> true
    }

    fun useGradients(style: InterfaceStyle): Boolean = when (style) {
        InterfaceStyle.MODERN -> true
        InterfaceStyle.PLAYFUL -> true
        InterfaceStyle.CLASSIC -> false
        InterfaceStyle.NEURAL_LUX -> true
    }

    fun getHeaderHeight(style: InterfaceStyle): Int = when (style) {
        InterfaceStyle.MODERN -> 130
        InterfaceStyle.PLAYFUL -> 150
        InterfaceStyle.CLASSIC -> 110
        InterfaceStyle.NEURAL_LUX -> 160
    }

    fun getIconSize(style: InterfaceStyle): Int = when (style) {
        InterfaceStyle.MODERN -> 28
        InterfaceStyle.PLAYFUL -> 32
        InterfaceStyle.CLASSIC -> 24
        InterfaceStyle.NEURAL_LUX -> 30
    }
    
    // Kaldırılan COSMIC tema fonksiyonları (her zaman false/0 döner)
    @Deprecated("COSMIC teması kaldırıldı")
    fun useGlassmorphism(style: InterfaceStyle): Boolean = false
    @Deprecated("COSMIC teması kaldırıldı")
    fun useNeonGlow(style: InterfaceStyle): Boolean = false
    @Deprecated("COSMIC teması kaldırıldı")
    fun useStarField(style: InterfaceStyle): Boolean = false
    @Deprecated("COSMIC teması kaldırıldı")
    fun getGlassOpacity(style: InterfaceStyle): Float = 1f
    @Deprecated("COSMIC teması kaldırıldı")
    fun getBlurRadius(style: InterfaceStyle): Int = 0
}

/**
 * Renk yardımcıları
 */
fun ThemeColor.getPrimaryColor(isDark: Boolean): Color {
    return Color(if (isDark) primaryDark else primaryLight)
}

fun ThemeColor.getSecondaryColor(isDark: Boolean): Color {
    return Color(if (isDark) secondaryDark else secondaryLight)
}

fun ThemeColor.getBackgroundColor(isDark: Boolean): Color {
    return Color(if (isDark) backgroundDark else backgroundLight)
}
