package com.example.bilgideham

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bilgideham.ui.theme.AppThemeId
import com.google.gson.Gson
import java.net.URLDecoder

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
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                darkMode = darkMode,
                onToggleTheme = onToggleTheme,
                onToggleBrightness = onToggleBrightness,
                currentBrightness = readingLevel
            )
        }

        // Tools
        composable("history") { HistoryScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
        composable("composition_fixer") { CompositionFixerScreen(navController) }
        composable("book_worm") { BookWormScreen(navController) }
        composable("exam_countdown") { ExamCountdownScreen(navController) }
        composable("timetable") { TimetableScreen(navController) }
        composable("ai_dictionary") { AiDictionaryScreen(navController) }
        composable("science_fact") { ScienceFactScreen(navController) }
        composable("atlas") { AtlasScreen(navController) }

        // Settings & Admin
        composable("theme_picker") {
            ThemePickerScreen(
                navController = navController,
                currentTheme = themeId,
                onSetTheme = onSetTheme,
                darkMode = darkMode,
                onToggleDarkMode = onToggleTheme
            )
        }
        composable("admin_panel") { AdminPanelScreen(onBack = { navController.popBackStack() }) }

        // AI
        composable("scan_solve") { ScanSolveScreen(navController) }
        composable("ai_oral_exam") { OralExamScreen(navController) }
        composable("history_chat") { HistoryChatScreen(navController) }
        composable("be_the_teacher") { BeTheTeacherScreen(navController) }

        // Language
        composable("english_chat_buddy") { EnglishChatBuddyScreen(navController) }
        composable("accent_coach") { AccentCoachScreen(navController) }
        composable("arabic_coach") { ArabicCoachScreen(navController) }
        composable("word_hunt") { WordHuntScreen(navController) }

        composable("level_locked") { LevelLockedScreen(navController) }

        // Games
        composable("games") { GamesScreen(navController) }
        composable("robo_logic") { RoboticCodingScreen(navController) }
        composable("math_rally") { MathRallyScreen(navController) }
        composable("game_science") { FenBilimleriRallyScreen(navController) }
        composable("game_social") { SosyalBilgilerRallyScreen(navController) }
        composable("game_turkish") { TurkceRallyScreen(navController) }
        composable("game_english") { IngilizceRallyScreen(navController) }

        // Exams
        composable("practice_exam_screen") { PracticeExamScreen(navController) }
        composable("paragraph_practice_screen") { ParagraphPracticeScreen(navController) }

        // Standard quizzes
        composable("math") { QuizScreen(navController, "Matematik", 10) }
        composable("turkce") { QuizScreen(navController, "Türkçe", 10) }
        composable("fen") { QuizScreen(navController, "Fen Bilimleri", 10) }
        composable("sosyal") { QuizScreen(navController, "Sosyal Bilgiler", 10) }
        composable("ingilizce") { EnglishLevelScreen(navController) }
        composable("arapca") { QuizScreen(navController, "Arapça", 10) }
        composable("din_kulturu") { QuizScreen(navController, "Din Kültürü", 10) }

        composable("turkce_paragraf_gunluk") {
            QuizScreen(
                navController = navController,
                lessonTitle = "Türkçe Paragraf",
                questionCount = 20
            )
        }

        composable("turkce_paragraf_haftasonu") {
            QuizScreen(
                navController = navController,
                lessonTitle = "Türkçe Paragraf",
                questionCount = 30
            )
        }

        // Extra safety routes
        composable("quiz_turkce_paragraf_gunluk") {
            QuizScreen(
                navController = navController,
                lessonTitle = "Türkçe Paragraf",
                questionCount = 20
            )
        }
        composable("quiz_turkce_paragraf_haftasonu") {
            QuizScreen(
                navController = navController,
                lessonTitle = "Türkçe Paragraf",
                questionCount = 30
            )
        }

        // Dynamic quiz
        composable(
            route = "quiz_screen/{lessonName}/{count}?duration={duration}",
            arguments = listOf(
                navArgument("lessonName") { type = NavType.StringType },
                navArgument("count") { type = NavType.IntType },
                navArgument("duration") { type = NavType.IntType; defaultValue = 0 }
            )
        ) { backStackEntry ->
            val lessonName = backStackEntry.arguments?.getString("lessonName") ?: "Test"
            val count = backStackEntry.arguments?.getInt("count") ?: 10
            val duration = backStackEntry.arguments?.getInt("duration") ?: 0

            QuizScreen(
                navController = navController,
                lessonTitle = if (lessonName == "Karma") "Deneme Sınavı" else lessonName,
                questionCount = count,
                preLoadedQuestions = emptyList(),
                examDurationMinutes = duration
            )
        }

        // JSON data quiz start
        composable(
            route = "quiz_screen/{data}",
            arguments = listOf(navArgument("data") { type = NavType.StringType })
        ) { backStack ->
            val encoded = backStack.arguments?.getString("data") ?: ""
            val list = try {
                val decoded = URLDecoder.decode(encoded, "UTF-8")
                Gson().fromJson(decoded, Array<QuestionModel>::class.java).toList()
            } catch (e: Exception) {
                emptyList()
            }

            val title = list.firstOrNull()?.lesson ?: "Pratik Test"

            QuizScreen(
                navController = navController,
                lessonTitle = title,
                questionCount = list.size,
                preLoadedQuestions = list
            )
        }

        composable("class_duel") { DuelScreen(navController) }

        // Exam result
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
                totalQuestions = total,
                correctCount = correct,
                wrongCount = wrong,
                durationText = time
            )
        }
    }
}
