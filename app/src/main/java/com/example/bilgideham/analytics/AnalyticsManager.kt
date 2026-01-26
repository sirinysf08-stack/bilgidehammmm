package com.example.bilgideham.analytics

import android.content.Context
import com.example.bilgideham.HistoryRepository
import com.example.bilgideham.SolvedQuestionEntity
import java.text.SimpleDateFormat
import java.util.*

/**
 * Gelişmiş Analitik Yöneticisi
 * Zayıf konu tespiti, öğrenme eğrisi, haftalık/aylık raporlar
 */
object AnalyticsManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val weekFormat = SimpleDateFormat("yyyy-'W'ww", Locale.US)
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)

    // ==================== VERİ MODELLERİ ====================

    data class SubjectAnalysis(
        val subject: String,
        val totalQuestions: Int,
        val correctCount: Int,
        val wrongCount: Int,
        val successRate: Float,
        val trend: Trend, // İyileşme/Kötüleşme/Sabit
        val weakTopics: List<String>,
        val lastWeekRate: Float,
        val thisWeekRate: Float
    )

    data class WeeklyReport(
        val weekLabel: String,
        val startDate: String,
        val endDate: String,
        val totalQuestions: Int,
        val correctCount: Int,
        val wrongCount: Int,
        val successRate: Float,
        val studyDays: Int,
        val averageDaily: Float,
        val subjectBreakdown: Map<String, Pair<Int, Int>>
    )

    data class MonthlyReport(
        val monthLabel: String,
        val totalQuestions: Int,
        val correctCount: Int,
        val successRate: Float,
        val weeklyTrend: List<Float>,
        val mostStudiedSubject: String,
        val weakestSubject: String
    )

    data class LearningCurvePoint(
        val date: String,
        val cumulativeCorrect: Int,
        val cumulativeTotal: Int,
        val successRate: Float
    )

    data class TrialExamAnalysis(
        val examId: Int,
        val date: String,
        val score: Int,
        val correct: Int,
        val wrong: Int,
        val rank: String, // "Çok İyi", "İyi", "Orta", "Geliştirilmeli"
        val improvement: Int // Önceki sınava göre değişim
    )

    enum class Trend { IMPROVING, DECLINING, STABLE }

    data class OverallAnalytics(
        val totalQuestions: Int,
        val totalCorrect: Int,
        val totalWrong: Int,
        val overallSuccessRate: Float,
        val studyStreak: Int, // Ardışık çalışma günü
        val totalStudyDays: Int,
        val averageDaily: Float,
        val strongSubjects: List<String>,
        val weakSubjects: List<String>,
        val recentTrend: Trend
    )


    // ==================== ANALİZ FONKSİYONLARI ====================

    suspend fun getOverallAnalytics(): OverallAnalytics {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }
        
        if (allHistory.isEmpty()) {
            return OverallAnalytics(0, 0, 0, 0f, 0, 0, 0f, emptyList(), emptyList(), Trend.STABLE)
        }

        val totalCorrect = allHistory.count { it.isCorrect }
        val totalWrong = allHistory.count { !it.isCorrect }
        val totalQuestions = allHistory.size
        val overallRate = if (totalQuestions > 0) (totalCorrect.toFloat() / totalQuestions) * 100 else 0f

        // Çalışma günleri
        val studyDays = allHistory.map { it.dateParams.take(10) }.distinct().size
        val averageDaily = if (studyDays > 0) totalQuestions.toFloat() / studyDays else 0f

        // Çalışma serisi (streak)
        val streak = calculateStudyStreak(allHistory)

        // Ders bazlı analiz
        val subjectStats = analyzeSubjects(allHistory)
        val strongSubjects = subjectStats.filter { it.successRate >= 70f }.sortedByDescending { it.successRate }.take(3).map { it.subject }
        val weakSubjects = subjectStats.filter { it.successRate < 60f }.sortedBy { it.successRate }.take(3).map { it.subject }

        // Son trend
        val recentTrend = calculateRecentTrend(allHistory)

        return OverallAnalytics(
            totalQuestions = totalQuestions,
            totalCorrect = totalCorrect,
            totalWrong = totalWrong,
            overallSuccessRate = overallRate,
            studyStreak = streak,
            totalStudyDays = studyDays,
            averageDaily = averageDaily,
            strongSubjects = strongSubjects,
            weakSubjects = weakSubjects,
            recentTrend = recentTrend
        )
    }

    suspend fun getSubjectAnalytics(): List<SubjectAnalysis> {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }
        return analyzeSubjects(allHistory)
    }

    private fun analyzeSubjects(allHistory: List<SolvedQuestionEntity>): List<SubjectAnalysis> {
        val subjectMap = mutableMapOf<String, MutableList<SolvedQuestionEntity>>()
        
        allHistory.forEach { item ->
            val subject = normalizeSubjectName(item.lesson)
            subjectMap.getOrPut(subject) { mutableListOf() }.add(item)
        }

        val cal = Calendar.getInstance()
        val thisWeekStart = cal.apply { 
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
        }.timeInMillis
        
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        val lastWeekStart = cal.timeInMillis
        val lastWeekEnd = thisWeekStart

        return subjectMap.map { (subject, items) ->
            val correct = items.count { it.isCorrect }
            val wrong = items.count { !it.isCorrect }
            val total = items.size
            val rate = if (total > 0) (correct.toFloat() / total) * 100 else 0f

            // Haftalık karşılaştırma (basitleştirilmiş)
            val thisWeekItems = items.takeLast(items.size / 2)
            val lastWeekItems = items.take(items.size / 2)
            
            val thisWeekRate = if (thisWeekItems.isNotEmpty()) 
                (thisWeekItems.count { it.isCorrect }.toFloat() / thisWeekItems.size) * 100 else 0f
            val lastWeekRate = if (lastWeekItems.isNotEmpty()) 
                (lastWeekItems.count { it.isCorrect }.toFloat() / lastWeekItems.size) * 100 else 0f

            val trend = when {
                thisWeekRate > lastWeekRate + 5 -> Trend.IMPROVING
                thisWeekRate < lastWeekRate - 5 -> Trend.DECLINING
                else -> Trend.STABLE
            }

            // Zayıf konular (yanlış yapılan sorulardan)
            val weakTopics = items.filter { !it.isCorrect }
                .groupBy { extractTopic(it.questionText) }
                .entries
                .sortedByDescending { it.value.size }
                .take(3)
                .map { it.key }

            SubjectAnalysis(
                subject = subject,
                totalQuestions = total,
                correctCount = correct,
                wrongCount = wrong,
                successRate = rate,
                trend = trend,
                weakTopics = weakTopics,
                lastWeekRate = lastWeekRate,
                thisWeekRate = thisWeekRate
            )
        }.sortedByDescending { it.totalQuestions }
    }

    suspend fun getWeeklyReports(weekCount: Int = 4): List<WeeklyReport> {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }
        
        if (allHistory.isEmpty()) return emptyList()

        val cal = Calendar.getInstance()
        val reports = mutableListOf<WeeklyReport>()

        repeat(weekCount) { weekOffset ->
            cal.time = Date()
            cal.add(Calendar.WEEK_OF_YEAR, -weekOffset)
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val weekStart = cal.time
            cal.add(Calendar.DAY_OF_WEEK, 6)
            val weekEnd = cal.time

            val weekItems = allHistory.filter { item ->
                try {
                    val itemDate = parseItemDate(item.dateParams)
                    itemDate != null && itemDate >= weekStart && itemDate <= weekEnd
                } catch (e: Exception) { false }
            }

            if (weekItems.isNotEmpty()) {
                val correct = weekItems.count { it.isCorrect }
                val wrong = weekItems.count { !it.isCorrect }
                val total = weekItems.size
                val rate = if (total > 0) (correct.toFloat() / total) * 100 else 0f
                val studyDays = weekItems.map { it.dateParams.take(10) }.distinct().size

                val subjectBreakdown = weekItems.groupBy { normalizeSubjectName(it.lesson) }
                    .mapValues { (_, items) -> 
                        items.count { it.isCorrect } to items.count { !it.isCorrect }
                    }

                reports.add(WeeklyReport(
                    weekLabel = "Hafta ${weekCount - weekOffset}",
                    startDate = SimpleDateFormat("dd MMM", Locale("tr")).format(weekStart),
                    endDate = SimpleDateFormat("dd MMM", Locale("tr")).format(weekEnd),
                    totalQuestions = total,
                    correctCount = correct,
                    wrongCount = wrong,
                    successRate = rate,
                    studyDays = studyDays,
                    averageDaily = if (studyDays > 0) total.toFloat() / studyDays else 0f,
                    subjectBreakdown = subjectBreakdown
                ))
            }
        }

        return reports.reversed()
    }

    suspend fun getMonthlyReport(): MonthlyReport? {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }
        
        if (allHistory.isEmpty()) return null

        val cal = Calendar.getInstance()
        val currentMonth = monthFormat.format(cal.time)
        
        val monthItems = allHistory.filter { item ->
            try {
                val itemDate = parseItemDate(item.dateParams)
                itemDate != null && monthFormat.format(itemDate) == currentMonth
            } catch (e: Exception) { false }
        }

        if (monthItems.isEmpty()) return null

        val correct = monthItems.count { it.isCorrect }
        val wrong = monthItems.count { !it.isCorrect }
        val total = monthItems.size
        val rate = if (total > 0) (correct.toFloat() / total) * 100 else 0f

        // Haftalık trend
        val weeklyTrend = (0..3).map { weekOffset ->
            cal.time = Date()
            cal.add(Calendar.WEEK_OF_YEAR, -weekOffset)
            val weekItems = monthItems.filter { item ->
                try {
                    val itemDate = parseItemDate(item.dateParams)
                    itemDate != null && weekFormat.format(itemDate) == weekFormat.format(cal.time)
                } catch (e: Exception) { false }
            }
            if (weekItems.isNotEmpty()) {
                (weekItems.count { it.isCorrect }.toFloat() / weekItems.size) * 100
            } else 0f
        }.reversed()

        // En çok çalışılan ve en zayıf ders
        val subjectStats = monthItems.groupBy { normalizeSubjectName(it.lesson) }
            .mapValues { (_, items) -> 
                val c = items.count { it.isCorrect }
                val t = items.size
                Triple(t, c, if (t > 0) (c.toFloat() / t) * 100 else 0f)
            }

        val mostStudied = subjectStats.maxByOrNull { it.value.first }?.key ?: "Bilinmiyor"
        val weakest = subjectStats.filter { it.value.first >= 5 }.minByOrNull { it.value.third }?.key ?: "Bilinmiyor"

        return MonthlyReport(
            monthLabel = SimpleDateFormat("MMMM yyyy", Locale("tr")).format(cal.time),
            totalQuestions = total,
            correctCount = correct,
            successRate = rate,
            weeklyTrend = weeklyTrend,
            mostStudiedSubject = mostStudied,
            weakestSubject = weakest
        )
    }

    suspend fun getLearningCurve(days: Int = 30): List<LearningCurvePoint> {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }
        
        if (allHistory.isEmpty()) return emptyList()

        val cal = Calendar.getInstance()
        val points = mutableListOf<LearningCurvePoint>()
        var cumulativeCorrect = 0
        var cumulativeTotal = 0

        repeat(days) { dayOffset ->
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -(days - 1 - dayOffset))
            val dayKey = dateFormat.format(cal.time)

            val dayItems = allHistory.filter { item ->
                item.dateParams.take(10) == dayKey
            }

            cumulativeCorrect += dayItems.count { it.isCorrect }
            cumulativeTotal += dayItems.size

            val rate = if (cumulativeTotal > 0) (cumulativeCorrect.toFloat() / cumulativeTotal) * 100 else 0f

            points.add(LearningCurvePoint(
                date = SimpleDateFormat("dd/MM", Locale("tr")).format(cal.time),
                cumulativeCorrect = cumulativeCorrect,
                cumulativeTotal = cumulativeTotal,
                successRate = rate
            ))
        }

        return points
    }

    suspend fun getTrialExamAnalysis(): List<TrialExamAnalysis> {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }
        
        val trialItems = allHistory.filter { item ->
            item.lesson.contains("Deneme", true) ||
            item.lesson.contains("Maraton", true) ||
            item.lesson.contains("Karma", true)
        }

        if (trialItems.isEmpty()) return emptyList()

        val examGroups = trialItems.groupBy { it.dateParams.take(10) }
        val exams = mutableListOf<TrialExamAnalysis>()
        var previousScore = 0

        examGroups.entries.sortedBy { it.key }.forEachIndexed { index, (date, items) ->
            val correct = items.count { it.isCorrect }
            val wrong = items.count { !it.isCorrect }
            val total = correct + wrong
            val score = if (total > 0) (correct * 100) / total else 0

            val rank = when {
                score >= 85 -> "Çok İyi"
                score >= 70 -> "İyi"
                score >= 50 -> "Orta"
                else -> "Geliştirilmeli"
            }

            val improvement = if (index > 0) score - previousScore else 0
            previousScore = score

            exams.add(TrialExamAnalysis(
                examId = index + 1,
                date = date,
                score = score,
                correct = correct,
                wrong = wrong,
                rank = rank,
                improvement = improvement
            ))
        }

        return exams.reversed()
    }


    // ==================== YARDIMCI FONKSİYONLAR ====================

    private fun calculateStudyStreak(history: List<SolvedQuestionEntity>): Int {
        if (history.isEmpty()) return 0

        val studyDates = history.map { it.dateParams.take(10) }.distinct().sorted()
        if (studyDates.isEmpty()) return 0

        val cal = Calendar.getInstance()
        var streak = 0
        var currentDate = dateFormat.format(cal.time)

        // Bugünden geriye doğru kontrol et
        while (studyDates.contains(currentDate)) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
            currentDate = dateFormat.format(cal.time)
        }

        return streak
    }

    private fun calculateRecentTrend(history: List<SolvedQuestionEntity>): Trend {
        if (history.size < 20) return Trend.STABLE

        val recentItems = history.takeLast(history.size / 2)
        val olderItems = history.take(history.size / 2)

        val recentRate = if (recentItems.isNotEmpty()) 
            (recentItems.count { it.isCorrect }.toFloat() / recentItems.size) * 100 else 0f
        val olderRate = if (olderItems.isNotEmpty()) 
            (olderItems.count { it.isCorrect }.toFloat() / olderItems.size) * 100 else 0f

        return when {
            recentRate > olderRate + 5 -> Trend.IMPROVING
            recentRate < olderRate - 5 -> Trend.DECLINING
            else -> Trend.STABLE
        }
    }

    private fun normalizeSubjectName(lesson: String): String {
        return when {
            lesson.contains("Matematik", true) -> "Matematik"
            lesson.contains("Fen", true) -> "Fen Bilimleri"
            lesson.contains("Sosyal", true) -> "Sosyal Bilgiler"
            lesson.contains("Türkçe", true) || lesson.contains("Turkce", true) -> "Türkçe"
            lesson.contains("Türk Dili", true) || lesson.contains("Edebiyat", true) -> "Türk Dili"
            lesson.contains("İngilizce", true) || lesson.contains("Ingilizce", true) -> "İngilizce"
            lesson.contains("Din", true) -> "Din Kültürü"
            lesson.contains("Arapça", true) -> "Arapça"
            lesson.contains("Paragraf", true) -> "Paragraf"
            lesson.contains("Tarih", true) -> "Tarih"
            lesson.contains("Coğrafya", true) -> "Coğrafya"
            lesson.contains("Fizik", true) -> "Fizik"
            lesson.contains("Kimya", true) -> "Kimya"
            lesson.contains("Biyoloji", true) -> "Biyoloji"
            lesson.contains("Felsefe", true) -> "Felsefe"
            lesson.contains("Vatandaş", true) -> "Vatandaşlık"
            lesson.contains("Güncel", true) -> "Güncel"
            lesson.contains("Deneme", true) || lesson.contains("Karma", true) -> "Deneme Sınavı"
            else -> "Diğer"
        }
    }

    private fun extractTopic(question: String): String {
        // Soru metninden konu çıkarma (basitleştirilmiş)
        val keywords = listOf(
            "denklem" to "Denklemler",
            "geometri" to "Geometri",
            "kesir" to "Kesirler",
            "yüzde" to "Yüzde Problemleri",
            "oran" to "Oran-Orantı",
            "paragraf" to "Paragraf",
            "anlam" to "Anlam Bilgisi",
            "yazım" to "Yazım Kuralları",
            "cümle" to "Cümle Bilgisi",
            "hücre" to "Hücre",
            "canlı" to "Canlılar",
            "madde" to "Madde ve Özellikleri",
            "kuvvet" to "Kuvvet ve Hareket",
            "tarih" to "Tarih",
            "coğrafya" to "Coğrafya"
        )

        val lowerQuestion = question.lowercase()
        return keywords.firstOrNull { lowerQuestion.contains(it.first) }?.second ?: "Genel"
    }

    private fun parseItemDate(dateParams: String): Date? {
        return try {
            // Format: "dd MMM HH:mm" veya benzeri
            val formats = listOf(
                SimpleDateFormat("dd MMM HH:mm", Locale("tr")),
                SimpleDateFormat("yyyy-MM-dd", Locale.US),
                SimpleDateFormat("dd/MM/yyyy", Locale("tr"))
            )
            for (format in formats) {
                try {
                    return format.parse(dateParams)
                } catch (e: Exception) { }
            }
            null
        } catch (e: Exception) { null }
    }

    // ==================== VELİ RAPORU ====================

    data class ParentReport(
        val studentName: String,
        val reportDate: String,
        val overallStats: OverallAnalytics,
        val weeklyProgress: List<WeeklyReport>,
        val subjectAnalysis: List<SubjectAnalysis>,
        val trialExams: List<TrialExamAnalysis>,
        val recommendations: List<String>
    )

    suspend fun generateParentReport(context: Context): ParentReport {
        val prefs = context.getSharedPreferences("bilgideham_prefs", Context.MODE_PRIVATE)
        val studentName = prefs.getString("student_name", "Öğrenci") ?: "Öğrenci"

        val overall = getOverallAnalytics()
        val weekly = getWeeklyReports(4)
        val subjects = getSubjectAnalytics()
        val trials = getTrialExamAnalysis()

        val recommendations = generateRecommendations(overall, subjects, trials)

        return ParentReport(
            studentName = studentName,
            reportDate = SimpleDateFormat("dd MMMM yyyy", Locale("tr")).format(Date()),
            overallStats = overall,
            weeklyProgress = weekly,
            subjectAnalysis = subjects,
            trialExams = trials,
            recommendations = recommendations
        )
    }

    private fun generateRecommendations(
        overall: OverallAnalytics,
        subjects: List<SubjectAnalysis>,
        trials: List<TrialExamAnalysis>
    ): List<String> {
        val recommendations = mutableListOf<String>()

        // Genel başarı oranına göre
        when {
            overall.overallSuccessRate < 50 -> {
                recommendations.add("📚 Genel başarı oranı düşük. Temel konuları tekrar etmesi önerilir.")
            }
            overall.overallSuccessRate < 70 -> {
                recommendations.add("💪 Başarı oranı orta seviyede. Düzenli çalışma ile gelişim sağlanabilir.")
            }
            overall.overallSuccessRate >= 85 -> {
                recommendations.add("🌟 Harika bir performans! Bu tempoyu korumaya devam edin.")
            }
        }

        // Çalışma düzenine göre
        if (overall.studyStreak < 3) {
            recommendations.add("📅 Düzenli çalışma alışkanlığı kazanması önemli. Her gün en az 15-20 soru çözmeyi hedefleyin.")
        } else if (overall.studyStreak >= 7) {
            recommendations.add("🔥 ${overall.studyStreak} günlük çalışma serisi harika! Bu disiplini sürdürün.")
        }

        // Zayıf derslere göre
        if (overall.weakSubjects.isNotEmpty()) {
            val weakList = overall.weakSubjects.take(2).joinToString(", ")
            recommendations.add("⚠️ $weakList derslerinde ek çalışma yapılması önerilir.")
        }

        // Deneme sınavlarına göre
        if (trials.isNotEmpty()) {
            val lastExam = trials.first()
            when {
                lastExam.improvement > 10 -> {
                    recommendations.add("📈 Son denemede ${lastExam.improvement} puanlık artış var. Tebrikler!")
                }
                lastExam.improvement < -10 -> {
                    recommendations.add("📉 Son denemede düşüş var. Motivasyonu artırmak için destek olun.")
                }
            }
        }

        // Trend'e göre
        when (overall.recentTrend) {
            Trend.IMPROVING -> recommendations.add("✨ Son dönemde belirgin bir gelişim var. Devam!")
            Trend.DECLINING -> recommendations.add("🔍 Son dönemde performans düşüşü var. Nedenini araştırın.")
            Trend.STABLE -> { /* Sabit, özel öneri yok */ }
        }

        return recommendations.take(5)
    }
}
