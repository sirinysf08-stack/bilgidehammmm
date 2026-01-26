package com.example.bilgideham

import android.content.Context
import android.content.SharedPreferences
import com.example.bilgideham.ui.theme.AppThemeId
import com.example.bilgideham.ui.theme.InterfaceStyle
import com.example.bilgideham.ui.theme.ThemeColor
import com.example.bilgideham.ui.theme.FullThemeConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Uygulama Tercihleri Yöneticisi
 *
 * Kullanıcının eğitim seviyesi, okul türü, sınıf ve diğer tercihlerini yönetir.
 * StateFlow kullanarak reaktif güncellemeler sağlar (polling yerine).
 */
object AppPrefs {

    private const val PREFS_NAME = "bilgideham_prefs"

    // Tercih anahtarları
    private const val KEY_EDUCATION_LEVEL = "education_level"
    private const val KEY_SCHOOL_TYPE = "school_type"
    private const val KEY_GRADE = "grade"
    private const val KEY_LEVEL_SELECTED = "level_selected"
    private const val KEY_STUDENT_NAME = "student_name"
    private const val KEY_BRAND_STYLE = "brand_style"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_READING_LEVEL = "reading_level"
    private const val KEY_THEME_ID = "theme_id"
    private const val KEY_INTERFACE_STYLE = "interface_style"
    private const val KEY_THEME_COLOR = "theme_color"
    private const val KEY_OABT_FIELD = "oabt_field"  // AGS 2. Oturum için seçilen alan (tarih, turkce vb.)
    
    // Rating & İzin anahtarları
    private const val KEY_APP_OPEN_COUNT = "app_open_count"
    private const val KEY_RATING_SHOWN_COUNT = "rating_shown_count"
    private const val KEY_PERMISSIONS_REQUESTED = "permissions_requested"
    
    // Performance anahtarları
    private const val KEY_REDUCE_ANIMATIONS = "reduce_animations"
    private const val KEY_AUTO_DETECT_LOW_END = "auto_detect_low_end"

    // ==================== REAKTİF STATE FLOWS ====================
    // Polling yerine StateFlow kullanarak anlık güncelleme sağlar
    
    private val _educationPrefs = MutableStateFlow(UserEducationPrefs.DEFAULT)
    val educationPrefs: StateFlow<UserEducationPrefs> = _educationPrefs.asStateFlow()
    
    private val _darkMode = MutableStateFlow(true)
    val darkModeFlow: StateFlow<Boolean> = _darkMode.asStateFlow()
    
    private val _themeColor = MutableStateFlow(ThemeColor.OCEAN)
    val themeColorFlow: StateFlow<ThemeColor> = _themeColor.asStateFlow()
    
    private val _interfaceStyle = MutableStateFlow(InterfaceStyle.MODERN)
    val interfaceStyleFlow: StateFlow<InterfaceStyle> = _interfaceStyle.asStateFlow()
    
    private val _themeId = MutableStateFlow(AppThemeId.ROYAL_ACADEMY)
    val themeIdFlow: StateFlow<AppThemeId> = _themeId.asStateFlow()
    
    private val _readingLevel = MutableStateFlow(1)
    val readingLevelFlow: StateFlow<Int> = _readingLevel.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Uygulama başlangıcında çağrılmalı - StateFlow'ları SharedPreferences'tan yükler
     */
    fun initialize(context: Context) {
        val prefs = getPrefs(context)

        if (!prefs.contains(KEY_INTERFACE_STYLE)) {
            prefs.edit().putString(KEY_INTERFACE_STYLE, InterfaceStyle.MODERN.name).apply()
        }

        _darkMode.value = prefs.getBoolean(KEY_DARK_MODE, true)
        _readingLevel.value = prefs.getInt(KEY_READING_LEVEL, 1)
        
        _themeColor.value = try {
            ThemeColor.valueOf(prefs.getString(KEY_THEME_COLOR, ThemeColor.OCEAN.name) ?: ThemeColor.OCEAN.name)
        } catch (e: Exception) { ThemeColor.OCEAN }
        
        _interfaceStyle.value = try {
            InterfaceStyle.valueOf(prefs.getString(KEY_INTERFACE_STYLE, InterfaceStyle.MODERN.name) ?: InterfaceStyle.MODERN.name)
        } catch (e: Exception) { InterfaceStyle.MODERN }
        
        _themeId.value = try {
            AppThemeId.valueOf(prefs.getString(KEY_THEME_ID, AppThemeId.ROYAL_ACADEMY.name) ?: AppThemeId.ROYAL_ACADEMY.name)
        } catch (e: Exception) { AppThemeId.ROYAL_ACADEMY }
        
        _educationPrefs.value = loadEducationPrefsInternal(context)
    }

    // ==================== TEMA TERCİHLERİ (Context parametreli - MainActivity uyumlu) ====================

    fun getDarkMode(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_DARK_MODE, true)
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply()
        _darkMode.value = enabled // StateFlow güncelle
    }

    fun getTheme(context: Context): AppThemeId {
        val themeName = getPrefs(context).getString(KEY_THEME_ID, AppThemeId.ROYAL_ACADEMY.name) ?: AppThemeId.ROYAL_ACADEMY.name
        return try {
            AppThemeId.valueOf(themeName)
        } catch (e: Exception) {
            AppThemeId.ROYAL_ACADEMY
        }
    }

    fun setTheme(context: Context, themeId: AppThemeId) {
        getPrefs(context).edit().putString(KEY_THEME_ID, themeId.name).apply()
        _themeId.value = themeId // StateFlow güncelle
    }

    fun getReadingLevel(context: Context): Int {
        return getPrefs(context).getInt(KEY_READING_LEVEL, 1)
    }

    fun setReadingLevel(context: Context, level: Int) {
        getPrefs(context).edit().putInt(KEY_READING_LEVEL, level).apply()
        _readingLevel.value = level // StateFlow güncelle
    }

    // ==================== YENİ TEMA SİSTEMİ (3 Arayüz x 5 Tema) ====================

    /**
     * Arayüz stilini al
     */
    fun getInterfaceStyle(context: Context): InterfaceStyle {
        val styleName = getPrefs(context).getString(KEY_INTERFACE_STYLE, InterfaceStyle.MODERN.name)
            ?: InterfaceStyle.MODERN.name
        return try {
            InterfaceStyle.valueOf(styleName)
        } catch (e: Exception) {
            InterfaceStyle.MODERN
        }
    }

    /**
     * Arayüz stilini kaydet
     */
    fun setInterfaceStyle(context: Context, style: InterfaceStyle) {
        getPrefs(context).edit().putString(KEY_INTERFACE_STYLE, style.name).apply()
        _interfaceStyle.value = style // StateFlow güncelle
    }

    /**
     * Tema rengini al
     */
    fun getThemeColor(context: Context): ThemeColor {
        val colorName = getPrefs(context).getString(KEY_THEME_COLOR, ThemeColor.OCEAN.name)
            ?: ThemeColor.OCEAN.name
        return try {
            ThemeColor.valueOf(colorName)
        } catch (e: Exception) {
            ThemeColor.OCEAN
        }
    }

    /**
     * Tema rengini kaydet
     */
    fun setThemeColor(context: Context, color: ThemeColor) {
        getPrefs(context).edit().putString(KEY_THEME_COLOR, color.name).apply()
        _themeColor.value = color // StateFlow güncelle
    }

    /**
     * Tam tema konfigürasyonunu al
     */
    fun getFullThemeConfig(context: Context): FullThemeConfig {
        return FullThemeConfig(
            interfaceStyle = getInterfaceStyle(context),
            themeColor = getThemeColor(context),
            isDarkMode = getDarkMode(context)
        )
    }

    /**
     * Tam tema konfigürasyonunu kaydet
     */
    fun setFullThemeConfig(context: Context, config: FullThemeConfig) {
        getPrefs(context).edit().apply {
            putString(KEY_INTERFACE_STYLE, config.interfaceStyle.name)
            putString(KEY_THEME_COLOR, config.themeColor.name)
            putBoolean(KEY_DARK_MODE, config.isDarkMode)
            apply()
        }
    }

    // ==================== PERFORMANS TERCİHLERİ ====================

    /**
     * Azaltılmış animasyon modunu al
     * true ise animasyonlar minimize edilir (düşük performanslı cihazlar için)
     */
    fun getReduceAnimations(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REDUCE_ANIMATIONS, false)
    }

    /**
     * Azaltılmış animasyon modunu kaydet
     */
    fun setReduceAnimations(context: Context, reduce: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_REDUCE_ANIMATIONS, reduce).apply()
    }

    /**
     * Düşük performanslı cihaz otomatik tespitini al
     * true ise uygulama otomatik olarak düşük cihazlarda animasyonları azaltır
     */
    fun getAutoDetectLowEnd(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_AUTO_DETECT_LOW_END, true) // Varsayılan açık
    }

    /**
     * Düşük performanslı cihaz otomatik tespitini kaydet
     */
    fun setAutoDetectLowEnd(context: Context, autoDetect: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_AUTO_DETECT_LOW_END, autoDetect).apply()
    }
    // ==================== EĞİTİM TERCİHLERİ ====================

    /**
     * Eğitim tercihlerini kaydet
     */
    fun saveEducationPrefs(context: Context, level: EducationLevel, schoolType: SchoolType, grade: Int?) {
        getPrefs(context).edit().apply {
            putString(KEY_EDUCATION_LEVEL, level.name)
            putString(KEY_SCHOOL_TYPE, schoolType.name)
            grade?.let { putInt(KEY_GRADE, it) } ?: remove(KEY_GRADE)
            putBoolean(KEY_LEVEL_SELECTED, true)
            apply()
        }
        _educationPrefs.value = UserEducationPrefs(level, schoolType, grade)
    }

    /**
     * Eğitim tercihlerini yükle
     */
    fun getEducationPrefs(context: Context): UserEducationPrefs {
        return loadEducationPrefsInternal(context)
    }

    private fun loadEducationPrefsInternal(context: Context): UserEducationPrefs {
        val prefs = getPrefs(context)
        val levelName = prefs.getString(KEY_EDUCATION_LEVEL, null)
        val schoolTypeName = prefs.getString(KEY_SCHOOL_TYPE, null)
        val grade = if (prefs.contains(KEY_GRADE)) prefs.getInt(KEY_GRADE, 5) else null

        if (levelName == null || schoolTypeName == null) {
            return UserEducationPrefs.DEFAULT
        }

        val level = try {
            EducationLevel.valueOf(levelName)
        } catch (e: Exception) {
            EducationLevel.ORTAOKUL
        }

        val schoolType = try {
            SchoolType.valueOf(schoolTypeName)
        } catch (e: Exception) {
            SchoolType.ORTAOKUL_STANDARD
        }

        return UserEducationPrefs(level, schoolType, grade)
    }

    /**
     * Seviye seçilmiş mi?
     */
    fun isLevelSelected(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_LEVEL_SELECTED, false)
    }

    /**
     * Seviye seçimini sıfırla (ayarlardan değiştirmek için)
     */
    fun resetLevelSelection(context: Context) {
        getPrefs(context).edit().apply {
            remove(KEY_EDUCATION_LEVEL)
            remove(KEY_SCHOOL_TYPE)
            remove(KEY_GRADE)
            putBoolean(KEY_LEVEL_SELECTED, false)
            apply()
        }
        _educationPrefs.value = UserEducationPrefs.DEFAULT
    }

    // ==================== ÖĞRENCİ BİLGİLERİ ====================

    /**
     * Öğrenci adını kaydet
     */
    fun setStudentName(context: Context, name: String) {
        getPrefs(context).edit().putString(KEY_STUDENT_NAME, name).apply()
    }

    /**
     * Öğrenci adını al
     */
    fun getStudentName(context: Context): String {
        return getPrefs(context).getString(KEY_STUDENT_NAME, "") ?: ""
    }

    /**
     * Marka stilini kaydet
     */
    fun setBrandStyle(context: Context, style: String) {
        getPrefs(context).edit().putString(KEY_BRAND_STYLE, style).apply()
    }

    /**
     * Marka stilini al
     */
    fun getBrandStyle(context: Context): String {
        return getPrefs(context).getString(KEY_BRAND_STYLE, "Küpü") ?: "Küpü"
    }

    // ==================== YARDIMCI FONKSİYONLAR ====================

    // ==================== AGS ÖABT ALAN SEÇİMİ ====================
    
    /**
     * Seçilen ÖABT alanını kaydet (tarih, turkce, matematik vb.)
     */
    fun setOabtField(context: Context, field: String?) {
        val prefs = getPrefs(context)
        if (field != null) {
            prefs.edit().putString(KEY_OABT_FIELD, field).apply()
        } else {
            prefs.edit().remove(KEY_OABT_FIELD).apply()
        }
    }
    
    /**
     * Seçilen ÖABT alanını al
     */
    fun getOabtField(context: Context): String? {
        return getPrefs(context).getString(KEY_OABT_FIELD, null)
    }

    /**
     * Mevcut seviye için ders listesini al
     * AGS ÖABT + Tarih alanı seçiliyse 14 üniteyi döndürür
     */
    fun getCurrentSubjects(context: Context): List<SubjectConfig> {
        val prefs = getEducationPrefs(context)
        
        // AGS ÖABT + Tarih alanı seçiliyse 14 üniteyi döndür
        if (prefs.schoolType == SchoolType.AGS_OABT) {
            val field = getOabtField(context)
            if (field == "tarih") {
                return getAgsTarihUniteSubjects()
            }
            if (field != null) {
                return getAgsOabtUnitSubjects(field)
            }
        }
        
        return CurriculumManager.getSubjectsFor(prefs.schoolType, prefs.grade)
    }
    
    /**
     * AGS Tarih 14 ünite SubjectConfig listesi
     */
    private fun getAgsTarihUniteSubjects(): List<SubjectConfig> {
        val uniteData = listOf(
            Triple(1, "Tarih Bilimi", "📜" to 0xFF8D6E63),
            Triple(2, "Osmanlı Türkçesi", "📖" to 0xFF795548),
            Triple(3, "Uygarlığın Doğuşu", "🏛️" to 0xFF009688),
            Triple(4, "İlk Türk Devletleri", "🐺" to 0xFF00BCD4),
            Triple(5, "İslam Tarihi", "☪️" to 0xFF4CAF50),
            Triple(6, "Türk İslam Devletleri", "⚔️" to 0xFFFFEB3B),
            Triple(7, "Türk Dünyası", "🌍" to 0xFFFF9800),
            Triple(8, "Osmanlı Tarihi", "👑" to 0xFFFF5722),
            Triple(9, "En Uzun Yüzyıl", "📜" to 0xFF9C27B0),
            Triple(10, "XX. Yüzyıl Başları", "💥" to 0xFF673AB7),
            Triple(11, "Milli Mücadele", "🇹🇷" to 0xFFF44336),
            Triple(12, "Atatürk Dönemi", "🎖️" to 0xFFE91E63),
            Triple(13, "Dünya Tarihi", "🌐" to 0xFF2196F3),
            Triple(14, "Çağdaş Tarih", "🏙️" to 0xFF3F51B5)
        )
        
        return uniteData.map { (id, title, iconColor) ->
            SubjectConfig(
                id = "tarih_unite_$id",
                displayName = title,
                description = "Ünite $id",
                icon = iconColor.first,
                colorHex = iconColor.second,
                route = "tarih_unite_$id",
                isActive = true
            )
        }
    }

    fun getAgsOabtUnitSubjects(field: String): List<SubjectConfig> {
        val (icon, colorHex, unitTitles) = when (field) {
            "turkce" -> Triple(
                "📖",
                0xFF2196F3,
                listOf(
                    "Anlama ve Anlatma Teknikleri",
                    "Dil Bilgisi ve Dil Bilimi",
                    "Çocuk Edebiyatı",
                    "Türk Halk Edebiyatı",
                    "Eski Türk Edebiyatı",
                    "Yeni Türk Edebiyatı",
                    "Edebiyat Bilgi ve Kuramları"
                )
            )

            "ilkmat" -> Triple(
                "🔢",
                0xFFFF5722,
                listOf("Analiz", "Cebir", "Geometri", "Uygulamalı Matematik")
            )

            "fen" -> Triple(
                "🔬",
                0xFF4CAF50,
                listOf("Fizik", "Kimya", "Biyoloji", "Jeoloji (Yer Bilimi)", "Astronomi", "Çevre Bilimi")
            )

            "sosyal" -> Triple(
                "🌍",
                0xFF9C27B0,
                listOf("Tarih", "Coğrafya", "Siyasal Bilim", "Sosyal Bilim Alanları")
            )

            "edebiyat" -> Triple(
                "📚",
                0xFF673AB7,
                listOf(
                    "Eski Türk Dili ve Yeni Türk Dili",
                    "Türk Halk Edebiyatı",
                    "Eski Türk Edebiyatı",
                    "Yeni Türk Edebiyatı"
                )
            )

            "cografya" -> Triple(
                "🗺️",
                0xFF00BCD4,
                listOf("Fiziki Coğrafya", "Beşeri ve Ekonomik Coğrafya", "Kıtalar ve Ülkeler Coğrafyası")
            )

            "matematik" -> Triple(
                "📐",
                0xFFE91E63,
                listOf("Analiz", "Cebir", "Geometri", "Uygulamalı Matematik")
            )

            "fizik" -> Triple(
                "⚡",
                0xFF2196F3,
                listOf("Mekanik", "Elektrik ve Manyetizma", "Maddenin Mekanik ve Isıl Özellikleri", "Dalgalar ve Optik", "Modern Fizik")
            )

            "kimya" -> Triple(
                "🧪",
                0xFFFF9800,
                listOf("Temel Kimya", "Analitik Kimya", "Anorganik Kimya", "Organik Kimya", "Fizikokimya")
            )

            "biyoloji" -> Triple(
                "🧬",
                0xFF8BC34A,
                listOf(
                    "Hücre ve Metabolizma",
                    "Bitki Biyolojisi",
                    "İnsan ve Hayvan Biyolojisi",
                    "Ekoloji",
                    "Canlıların Sınıflandırılması",
                    "Genetik"
                )
            )

            "rehberlik" -> Triple(
                "🧠",
                0xFF3F51B5,
                listOf(
                    "Temel Psikolojik Kavramlar",
                    "Psikolojik Danışma Kuram ve Teknikleri",
                    "Davranış ve Uyum Problemleri",
                    "Bireyi Tanıma Teknikleri",
                    "Bireyle ve Grupla Psikolojik Danışma",
                    "Mesleki Rehberlik ve Kariyer Danışmanlığı",
                    "Araştırma ve Program Geliştirme",
                    "Özel Eğitim ve Yasal Konular"
                )
            )

            "sinif" -> Triple(
                "👨‍🏫",
                0xFFCDDC39,
                listOf(
                    "İlkokulda Temel Matematik",
                    "İlkokulda Temel Fen Bilimleri",
                    "Türk Dili",
                    "Türk Tarihi ve Kültürü",
                    "Türkiye Coğrafyası ve Jeopolitiği",
                    "Çocuk Edebiyatı",
                    "Alan Eğitimi"
                )
            )

            "okoncesi" -> Triple(
                "🎨",
                0xFFFF4081,
                listOf(
                    "Erken Çocukluk Eğitimine Giriş",
                    "Erken Çocukluk Döneminde Gelişim",
                    "Çocuk Sağlığı ve İlk Yardım",
                    "Erken Çocuklukta Sanat",
                    "Erken Çocukluk Dönemi Edebiyatı",
                    "Program, Yöntem ve Yaklaşımlar",
                    "Anne-Baba Eğitimi",
                    "Çocuk Hakları"
                )
            )

            "beden" -> Triple(
                "🏃",
                0xFF4CAF50,
                listOf(
                    "Beden Eğitimi ve Sporun Temelleri",
                    "İnsan Anatomisi ve Kinesiyoloji",
                    "Egzersiz Fizyolojisi",
                    "Antrenman Bilgisi",
                    "Sağlık Bilgisi ve İlk Yardım"
                )
            )

            "din" -> Triple(
                "☪️",
                0xFF607D8B,
                listOf(
                    "Kur'an-ı Kerim ve Tecvid",
                    "Tefsir",
                    "Hadis",
                    "Fıkıh",
                    "İslam Mezhepleri ve Akımlar",
                    "Siyer",
                    "İslam Tarihi, Kültür ve Medeniyeti",
                    "Akaid ve Kelam",
                    "İslam Felsefesi ve Din Bilimleri",
                    "Din Eğitimi"
                )
            )

            else -> Triple("📚", 0xFF9E9E9E, emptyList())
        }

        return unitTitles.mapIndexed { index, title ->
            val id = "${field}_unite_${index + 1}"
            SubjectConfig(
                id = id,
                displayName = title,
                description = "Ünite ${index + 1}",
                icon = icon,
                colorHex = colorHex,
                route = id,
                isActive = true
            )
        }
    }

    /**
     * Mevcut seviye için sınıf listesini al
     */
    fun getAvailableGrades(context: Context): List<Int> {
        return CurriculumManager.getGradesFor(getEducationPrefs(context).schoolType)
    }

    /**
     * Sınıfı değiştir (aynı okul türü içinde)
     */
    fun changeGrade(context: Context, newGrade: Int) {
        val current = getEducationPrefs(context)
        if (newGrade in current.schoolType.grades) {
            saveEducationPrefs(context, current.level, current.schoolType, newGrade)
        }
    }

    /**
     * Mevcut eğitim seviyesi başlığını al
     */
    fun getEducationTitle(context: Context): String {
        val prefs = getEducationPrefs(context)
        return buildString {
            append(prefs.schoolType.displayName)
            prefs.grade?.let { append(" - $it. Sınıf") }
        }
    }
    
    // ==================== RATING & İZİN YÖNETİMİ ====================
    
    /**
     * Uygulama açılış sayısını artır ve döndür
     */
    fun incrementAppOpenCount(context: Context): Int {
        val prefs = getPrefs(context)
        val current = prefs.getInt(KEY_APP_OPEN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_APP_OPEN_COUNT, current).apply()
        return current
    }
    
    /**
     * Rating popup gösterilme sayısını al
     */
    fun getRatingShownCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_RATING_SHOWN_COUNT, 0)
    }
    
    /**
     * Rating popup gösterildi olarak işaretle
     */
    fun markRatingShown(context: Context) {
        val prefs = getPrefs(context)
        val current = prefs.getInt(KEY_RATING_SHOWN_COUNT, 0) + 1
        prefs.edit().putInt(KEY_RATING_SHOWN_COUNT, current).apply()
    }
    
    /**
     * Rating popup gösterilmeli mi?
     * 1. kullanımda ve 4. kullanımda göster, sonra bir daha gösterme
     */
    fun shouldShowRatingPopup(context: Context): Boolean {
        val openCount = getPrefs(context).getInt(KEY_APP_OPEN_COUNT, 0)
        val shownCount = getRatingShownCount(context)
        
        return when {
            shownCount >= 2 -> false // 2 kez gösterildiyse bir daha gösterme
            shownCount == 0 && openCount >= 2 -> true // 2. girişte göster (İlk girişte rahatsız etme)
            shownCount == 1 && openCount >= 10 -> true // 2. hatırlatma 10. girişte
            else -> false
        }
    }
    
    /**
     * İzinler istendi mi?
     */
    fun arePermissionsRequested(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_PERMISSIONS_REQUESTED, false)
    }
    
    /**
     * İzinler istendi olarak işaretle
     */
    fun markPermissionsRequested(context: Context) {
        getPrefs(context).edit().putBoolean(KEY_PERMISSIONS_REQUESTED, true).apply()
    }
}
