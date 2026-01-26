package com.example.bilgideham

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.bilgideham.analytics.DetailedAnalyticsScreen
import com.example.bilgideham.analytics.ParentReportScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bilgideham.ui.theme.AppThemeId
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    darkMode: Boolean,
    onToggleTheme: () -> Unit,
    themeId: AppThemeId,
    onSetTheme: (AppThemeId) -> Unit,
    readingLevel: Int,
    onToggleBrightness: () -> Unit
) {
    val context = LocalContext.current

    // Kullanıcı daha önce seviye seçmiş mi kontrol et
    val startDest = if (AppPrefs.isLevelSelected(context)) {
        // Tüm seviyeler (AGS dahil) HomeScreen'e yönlendirilir
        "home"
    } else {
        "level_selection"
    }

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        // ==================== SEVİYE SEÇİM EKRANLARI ====================

        composable("level_selection") {
            LevelSelectionScreen(navController = navController)
        }

        composable(
            route = "school_type_selection/{levelName}",
            arguments = listOf(
                navArgument("levelName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val levelName = backStackEntry.arguments?.getString("levelName") ?: "ORTAOKUL"
            val level = try {
                EducationLevel.valueOf(levelName)
            } catch (e: Exception) {
                EducationLevel.ORTAOKUL
            }

            SchoolTypeScreen(
                navController = navController,
                educationLevel = level
            )
        }

        // ==================== ANA EKRAN ====================

        composable("home") {
            HomeScreen(
                navController = navController,
                darkMode = darkMode,
                onToggleTheme = onToggleTheme,
                onToggleBrightness = onToggleBrightness,
                currentBrightness = readingLevel
            )
        }

        composable("settings") { SettingsScreen(navController = navController) }
        composable("admin") { AdminPanelScreen(navController = navController, onBack = { navController.popBackStack() }) }
        composable("admin_panel") { AdminPanelScreen(navController = navController, onBack = { navController.popBackStack() }) }
        composable("admin_delete") { AdminDeleteScreen(onBack = { navController.popBackStack() }) }
        composable("chart_questions") { ChartQuestionScreen(onBack = { navController.popBackStack() }) }
        composable("chart_question_screen") { ChartQuestionScreen(onBack = { navController.popBackStack() }) }

        // Analiz & Rapor Ekranları
        composable("progress") { ProgressScreen(navController = navController) }
        composable("history") { HistoryScreen(navController = navController) }
        composable("detailed_analytics") { DetailedAnalyticsScreen(navController = navController) }
        composable("parent_report") { ParentReportScreen(navController = navController) }

        // ==================== YAPAY ZEKA & ÖZELLİK EKRANLARI ====================
        composable("scan_solve") { ScanSolveScreen(navController = navController) }
        composable("ai_oral_exam") { OralExamScreen(navController = navController) }
        composable("history_chat") { HistoryChatScreen(navController = navController) }
        composable("be_the_teacher") { BeTheTeacherScreen(navController = navController) }
        composable("composition_fixer") { CompositionFixerScreen(navController = navController) }

        // ==================== DİL DÜNYASI ====================
        composable("english_chat_buddy") { EnglishChatBuddyScreen(navController = navController) }
        composable("accent_coach") { AccentCoachScreen(navController = navController) }
        composable("arabic_coach") { ArabicCoachScreen(navController = navController) }
        composable("word_hunt") { WordHuntScreen(navController = navController) }

        // ==================== ÖĞRENCİ ÇANTASI ====================
        composable("exam_countdown") { ExamCountdownScreen(navController = navController) }
        composable("timetable") { TimetableScreen(navController = navController) }
        composable("book_worm") { BookWormScreen(navController = navController) }
        composable("ai_dictionary") { AiDictionaryScreen(navController = navController) }
        composable("atlas") { AtlasScreen(navController = navController) }
        composable("science_fact") { ScienceFactScreen(navController = navController) }

        // ==================== OYUNLAR ====================
        composable("games") { GamesScreen(navController = navController) }
        composable("code_master") { CodeMasterScreen(navController = navController) }
        composable("robo_logic") { RoboticCodingScreen(navController = navController) }
        composable("math_rally") { MathRallyScreen(navController = navController) }

        // Geliştirme aşamasındaki oyunlar
        composable("game_science") { UnderDevelopmentScreen(navController, "Fen Bilimleri Oyunu", "🧪") }
        composable("game_social") { UnderDevelopmentScreen(navController, "Sosyal Bilgiler Oyunu", "🌍") }
        composable("game_turkish") { UnderDevelopmentScreen(navController, "Türkçe Oyunu", "📘") }
        composable("game_english") { UnderDevelopmentScreen(navController, "İngilizce Oyunu", "🇬🇧") }

        // Ebeveyn Kontrolü
        composable("parental_control") { ParentalControlScreen(navController) }
        
        // Tema Seçici Ekranı
        composable("theme_picker_screen") {
            ThemePickerScreen(
                navController = navController,
                currentTheme = themeId,
                onSetTheme = onSetTheme,
                darkMode = darkMode,
                onToggleDarkMode = onToggleTheme
            )
        }

        // ✅ HomeScreen hangi route'u basarsa bassın crash olmasın (ALIAS HAVUZU)
        lessonAlias(navController, "turkce", "Türkçe")
        lessonAlias(navController, "Türkçe", "Türkçe")
        lessonAlias(navController, "türkçe", "Türkçe")

        lessonAlias(navController, "matematik", "Matematik")
        lessonAlias(navController, "Matematik", "Matematik")
        lessonAlias(navController, "math", "Matematik") // ✅ Crash route buydu
        lessonAlias(navController, "MATEMATIK", "Matematik")
        lessonAlias(navController, "MATEMATİK", "Matematik")
        lessonAlias(navController, "mat", "Matematik")

        lessonAlias(navController, "fen", "Fen")
        lessonAlias(navController, "Fen", "Fen")

        lessonAlias(navController, "sosyal", "Sosyal")
        lessonAlias(navController, "Sosyal", "Sosyal")

        lessonAlias(navController, "ingilizce", "İngilizce")
        lessonAlias(navController, "İngilizce", "İngilizce")
        lessonAlias(navController, "ing", "İngilizce")

        lessonAlias(navController, "arapca", "Arapça")
        lessonAlias(navController, "Arapça", "Arapça")

        lessonAlias(navController, "din_kulturu", "Din Kültürü")
        lessonAlias(navController, "Din Kültürü", "Din Kültürü")

        lessonAlias(navController, "paragraf", "PARAGRAF")
        lessonAlias(navController, "PARAGRAF", "PARAGRAF")

        // ==================== DİNAMİK DERS ROUTE'LARI ====================
        // Tüm seviyeler için dinamik ders route'ları (CurriculumConfig'den gelen)

        // İlkokul 3. sınıf dersleri
        lessonAlias(navController, "turkce_3", "Türkçe")
        lessonAlias(navController, "matematik_3", "Matematik")
        lessonAlias(navController, "hayat_bilgisi_3", "Hayat Bilgisi")
        lessonAlias(navController, "fen_3", "Fen Bilimleri")
        lessonAlias(navController, "ingilizce_3", "İngilizce")

        // İlkokul 4. sınıf dersleri
        lessonAlias(navController, "turkce_4", "Türkçe")
        lessonAlias(navController, "matematik_4", "Matematik")
        lessonAlias(navController, "hayat_bilgisi", "Hayat Bilgisi")
        lessonAlias(navController, "fen_4", "Fen Bilimleri")
        lessonAlias(navController, "sosyal_4", "Sosyal Bilgiler")
        lessonAlias(navController, "ingilizce_4", "İngilizce")
        lessonAlias(navController, "din_4", "Din Kültürü")

        // Ortaokul dersleri (5-8. sınıf)
        for (grade in 5..8) {
            lessonAlias(navController, "turkce_$grade", "Türkçe")
            lessonAlias(navController, "matematik_$grade", "Matematik")
            lessonAlias(navController, "fen_$grade", "Fen Bilimleri")
            lessonAlias(navController, "sosyal_$grade", "Sosyal Bilgiler")
            lessonAlias(navController, "ingilizce_$grade", "İngilizce")
            lessonAlias(navController, "din_$grade", "Din Kültürü")
            lessonAlias(navController, "arapca_$grade", "Arapça")
            lessonAlias(navController, "kuran_$grade", "Kur'an-ı Kerim")
            lessonAlias(navController, "siyer_$grade", "Siyer")
            lessonAlias(navController, "paragraf_$grade", "Paragraf")
            
            if (grade == 8) {
                lessonAlias(navController, "inkilap_8", "T.C. İnkılap Tarihi")
            }
        }

        // LGS Deneme
        lessonAlias(navController, "lgs_deneme", "LGS Deneme")

        // Lise dersleri (9-12. sınıf)
        for (grade in 9..12) {
            lessonAlias(navController, "turk_dili_$grade", "Türk Dili ve Edebiyatı")
            lessonAlias(navController, "matematik_lise_$grade", "Matematik")
            lessonAlias(navController, "tarih_$grade", "Tarih")
            lessonAlias(navController, "cografya_$grade", "Coğrafya")
            lessonAlias(navController, "ingilizce_lise_$grade", "İngilizce")
            lessonAlias(navController, "din_lise_$grade", "Din Kültürü")
            lessonAlias(navController, "fizik_$grade", "Fizik")
            lessonAlias(navController, "kimya_$grade", "Kimya")
            lessonAlias(navController, "biyoloji_$grade", "Biyoloji")
            lessonAlias(navController, "felsefe_$grade", "Felsefe")
            lessonAlias(navController, "sosyoloji_$grade", "Sosyoloji")
            lessonAlias(navController, "psikoloji_$grade", "Psikoloji")
            lessonAlias(navController, "mantik_$grade", "Mantık")
            lessonAlias(navController, "ileri_matematik_$grade", "İleri Matematik")
            lessonAlias(navController, "arapca_lise_$grade", "Arapça")
            lessonAlias(navController, "kuran_lise_$grade", "Kur'an-ı Kerim")
            lessonAlias(navController, "hadis_$grade", "Hadis")
            lessonAlias(navController, "fikih_$grade", "Fıkıh")
            lessonAlias(navController, "kelam_$grade", "Kelam")
            lessonAlias(navController, "meslek_dersi_$grade", "Meslek Dersleri")
            lessonAlias(navController, "atolye_$grade", "Atölye")
            // ✅ LİSE PARAGRAF ROUTE'LARI EKLENDİ (CRASH FİX)
            lessonAlias(navController, "paragraf_lise_$grade", "Paragraf")
        }

        // YKS Deneme
        lessonAlias(navController, "tyt_deneme", "TYT Deneme")
        lessonAlias(navController, "ayt_deneme", "AYT Deneme")

        // KPSS dersleri
        lessonAlias(navController, "turkce_kpss", "Türkçe KPSS")
        lessonAlias(navController, "matematik_kpss", "Matematik KPSS")
        lessonAlias(navController, "tarih_kpss", "Tarih KPSS")
        lessonAlias(navController, "cografya_kpss", "Coğrafya KPSS")
        lessonAlias(navController, "vatandaslik_kpss", "Vatandaşlık KPSS")
        lessonAlias(navController, "guncel_kpss", "Güncel Bilgiler KPSS")
        lessonAlias(navController, "egitim_bilimleri", "Eğitim Bilimleri")
        lessonAlias(navController, "gelisim_ogrenme", "Gelişim ve Öğrenme")
        lessonAlias(navController, "olcme_degerlendirme", "Ölçme ve Değerlendirme")
        lessonAlias(navController, "program_gelistirme", "Program Geliştirme")
        lessonAlias(navController, "sinif_yonetimi", "Sınıf Yönetimi")
        lessonAlias(navController, "rehberlik", "Rehberlik")
        // KPSS Denemeleri (Modern Liste)
        composable("kpss_gy_deneme") {
            KpssDenemListScreen(navController = navController)
        }
        composable("kpss_gk_deneme") {
            KpssDenemListScreen(navController = navController)
        }

        // AGS dersleri (1. Oturum - MEB)
        lessonAlias(navController, "ags_sozel", "Sözel Yetenek")
        lessonAlias(navController, "ags_sayisal", "Sayısal Yetenek")
        lessonAlias(navController, "ags_tarih", "Tarih AGS")
        lessonAlias(navController, "ags_cografya", "Türkiye Coğrafyası")
        lessonAlias(navController, "ags_egitim", "Eğitimin Temelleri")
        lessonAlias(navController, "ags_mevzuat", "Mevzuat AGS")

        // AGS dersleri (2. Oturum - ÖABT)
        lessonAlias(navController, "oabt_turkce", "Türkçe ÖABT")
        lessonAlias(navController, "oabt_ilkmat", "İlköğretim Matematik ÖABT")
        lessonAlias(navController, "oabt_fen", "Fen Bilimleri ÖABT")
        lessonAlias(navController, "oabt_sosyal", "Sosyal Bilgiler ÖABT")
        lessonAlias(navController, "oabt_edebiyat", "Türk Dili ve Edebiyatı ÖABT")
        
        // ÖABT Tarih - Artık direkt HomeScreen'e gider (alan seçimi SchoolTypeScreen'de yapıldı)
        lessonAlias(navController, "oabt_tarih", "Tarih ÖABT")
        
        // AGS Tarih 14 Ünite Route'ları - STANDART QuizScreen kullanır
        // lessonTitle olarak tarih_unite_X geçirilir, QuizScreen bu formatta tanır
        for (uniteId in 1..14) {
            composable("tarih_unite_$uniteId") {
                // Standart QuizScreen kullan - doğrudan tarih_unite_X formatı
                QuizScreen(
                    navController = navController,
                    lessonTitle = "tarih_unite_$uniteId",
                    questionCount = 10
                )
            }
        }

        val oabtUnitRouteCounts = mapOf(
            "turkce" to 7,
            "ilkmat" to 4,
            "fen" to 6,
            "sosyal" to 4,
            "edebiyat" to 4,
            "cografya" to 3,
            "matematik" to 4,
            "fizik" to 5,
            "kimya" to 5,
            "biyoloji" to 6,
            "rehberlik" to 8,
            "sinif" to 7,
            "okoncesi" to 8,
            "beden" to 5,
            "din" to 10
        )

        for ((field, count) in oabtUnitRouteCounts) {
            for (uniteId in 1..count) {
                composable("${field}_unite_$uniteId") {
                    QuizScreen(
                        navController = navController,
                        lessonTitle = "${field}_unite_$uniteId",
                        questionCount = 10
                    )
                }
            }
        }
        
        lessonAlias(navController, "oabt_cografya", "Coğrafya ÖABT")
        lessonAlias(navController, "oabt_matematik", "Matematik ÖABT")
        lessonAlias(navController, "oabt_fizik", "Fizik ÖABT")
        lessonAlias(navController, "oabt_kimya", "Kimya ÖABT")
        lessonAlias(navController, "oabt_biyoloji", "Biyoloji ÖABT")
        lessonAlias(navController, "oabt_din", "Din Kültürü ÖABT")
        lessonAlias(navController, "oabt_rehberlik", "Rehberlik ÖABT")
        lessonAlias(navController, "oabt_sinif", "Sınıf Öğretmenliği ÖABT")
        lessonAlias(navController, "oabt_okoncesi", "Okul Öncesi ÖABT")
        lessonAlias(navController, "oabt_beden", "Beden Eğitimi ÖABT")

        // Paragraf ve Deneme seçim ekranları
        composable("paragraph_practice_screen") {
            ParagraphPracticeScreen(navController = navController)
        }
        composable("practice_exam_screen") {
            val context = LocalContext.current
            // remember kullanarak performans optimizasyonu
            val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
            
            if (educationPrefs.level == EducationLevel.KPSS) {
                KpssDenemListScreen(navController = navController)
            } else {
                PracticeExamScreen(navController = navController)
            }
        }

        // Geçmiş sınav soruları ekranları
        // KPSS geçmiş soruları kaldırıldı (yasal değil)
        // composable("past_kpss_questions") {
        //     PastKpssQuestionsScreen(navController = navController)
        // }
        composable("past_lgs_questions") {
            PastLgsQuestionsScreen(navController = navController)
        }
        composable("past_ags_questions") {
            PastAgsQuestionsScreen(navController = navController)
        }
        
        // KPSS Deneme Listesi
        composable("kpss_denemeler") {
            KpssDenemListScreen(navController = navController)
        }
        
        // KPSS Deneme Çözüm Ekranı
        composable(
            route = "kpss_deneme_coz/{paketNo}/{soruNo}",
            arguments = listOf(
                navArgument("paketNo") { type = NavType.IntType },
                navArgument("soruNo") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val paketNo = backStackEntry.arguments?.getInt("paketNo") ?: 1
            // startQuestionIndex henüz QuizScreen'de yoksa bile route'u tanımlayalım
            // QuizScreen'in bu deneme formatını tanıması için QuestionRepository'de işlem yapacağız
            val soruNo = backStackEntry.arguments?.getInt("soruNo") ?: 1
            QuizScreen(
                navController = navController,
                lessonTitle = "kpss_deneme_$paketNo",
                startQuestionIndex = (soruNo - 1).coerceAtLeast(0),
                questionCount = 120,
                examDurationMinutes = 120 // KPSS Deneme süresi
            )
        }
        
        // KPSS Deneme Sonuç Ekranı (Placeholder - İleride detaylı analiz eklenebilir)
        composable(
            route = "kpss_deneme_sonuc/{paketNo}",
            arguments = listOf(navArgument("paketNo") { type = NavType.IntType })
        ) {
             // Şimdilik listeye geri dön
             KpssDenemListScreen(navController = navController)
        }
        
        // AGS Alan Dersleri (Eski route - geriye uyumluluk için home'a yönlendir)
        composable("ags_alan_dersleri") {
            // Artık standart HomeScreen kullanılıyor
            HomeScreen(
                navController = navController,
                darkMode = darkMode,
                onToggleTheme = onToggleTheme,
                onToggleBrightness = onToggleBrightness,
                currentBrightness = readingLevel
            )
        }

        // Paragraf quiz route'ları
        composable("turkce_paragraf_gunluk") {
            QuizScreen(navController = navController, lessonTitle = "Paragraf", questionCount = 20)
        }
        composable("turkce_paragraf_haftasonu") {
            QuizScreen(navController = navController, lessonTitle = "Paragraf", questionCount = 30)
        }

        // Deneme sınavı quiz route'ları
        composable("quiz_genel_deneme") {
            QuizScreen(navController = navController, lessonTitle = "GENEL_DENEME", questionCount = 70, examDurationMinutes = 105)
        }
        composable("quiz_maraton") {
            QuizScreen(navController = navController, lessonTitle = "MARATON", questionCount = 120, examDurationMinutes = 120)
        }

        // Düello ekranı
        composable("class_duel") {
            DuelScreen(navController = navController)
        }

        composable("deneme") { QuizScreen(navController, "GENEL_DENEME", 40, examDurationMinutes = 60) }
        composable("maraton") { QuizScreen(navController, "MARATON", 60, examDurationMinutes = 90) }

        // Eski/dinamik akışlar için (varsa)
        composable(
            route = "quiz/{lessonTitle}/{questionCount}/{examDuration}",
            arguments = listOf(
                navArgument("lessonTitle") { type = NavType.StringType },
                navArgument("questionCount") { type = NavType.IntType },
                navArgument("examDuration") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val rawLesson = backStackEntry.arguments?.getString("lessonTitle").orEmpty()
            val lessonTitle = runCatching { URLDecoder.decode(rawLesson, "UTF-8") }.getOrDefault(rawLesson)
            val count = backStackEntry.arguments?.getInt("questionCount") ?: 10
            val duration = backStackEntry.arguments?.getInt("examDuration") ?: 0

            QuizScreen(
                navController = navController,
                lessonTitle = lessonTitle,
                questionCount = count,
                examDurationMinutes = duration
            )
        }

        // QuizScreen süre bitince buraya gidiyor (parametresiz fallback)
        composable("exam_result") { ExamResultFallbackScreen(navController = navController) }

        // Parametreli exam_result route
        composable(
            route = "exam_result/{correct}/{wrong}/{total}/{time}",
            arguments = listOf(
                navArgument("correct") { type = NavType.IntType },
                navArgument("wrong") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("time") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val correct = backStackEntry.arguments?.getInt("correct") ?: 0
            val wrong = backStackEntry.arguments?.getInt("wrong") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val time = backStackEntry.arguments?.getString("time") ?: "00:00"

            ExamResultScreen(
                navController = navController,
                correctCount = correct,
                wrongCount = wrong,
                totalQuestions = total,
                durationText = time
            )
        }
    }
}

private fun NavGraphBuilder.lessonAlias(
    navController: NavHostController,
    route: String,
    lessonTitle: String
) {
    composable(route) {
        QuizScreen(
            navController = navController,
            lessonTitle = lessonTitle,
            questionCount = 10
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamResultFallbackScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Sınav Sonucu") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(24.dp)
        ) {
            Text("Sınav tamamlandı.")
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("home") }) { Text("Ana Sayfa") }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(onClick = { navController.popBackStack() }) { Text("Geri") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnderDevelopmentScreen(
    navController: NavController,
    title: String,
    emoji: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 80.sp)
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Geliştirme Aşamasında",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "$title çok yakında sizlerle!",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "🚧 Yapım devam ediyor... 🚧",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Geri Dön")
            }
        }
    }
}
