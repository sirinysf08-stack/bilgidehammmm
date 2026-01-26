package com.example.bilgideham.analytics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilgideham.AppPrefs
import kotlinx.coroutines.launch

/**
 * Öğrenci Analizi Ekranı
 * Detaylı performans analizi ve istatistikler
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedAnalyticsScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State
    var overallStats by remember { mutableStateOf<AnalyticsManager.OverallAnalytics?>(null) }
    var subjectAnalysis by remember { mutableStateOf<List<AnalyticsManager.SubjectAnalysis>>(emptyList()) }
    var weeklyReports by remember { mutableStateOf<List<AnalyticsManager.WeeklyReport>>(emptyList()) }
    var learningCurve by remember { mutableStateOf<List<AnalyticsManager.LearningCurvePoint>>(emptyList()) }
    var trialExams by remember { mutableStateOf<List<AnalyticsManager.TrialExamAnalysis>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }

    LaunchedEffect(Unit) {
        scope.launch {
            overallStats = AnalyticsManager.getOverallAnalytics()
            subjectAnalysis = AnalyticsManager.getSubjectAnalytics()
            weeklyReports = AnalyticsManager.getWeeklyReports(4)
            learningCurve = AnalyticsManager.getLearningCurve(14)
            trialExams = AnalyticsManager.getTrialExamAnalysis()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Öğrenci Analizi 📊", fontWeight = FontWeight.Bold)
                        Text(
                            "${educationPrefs.schoolType.displayName}",
                            fontSize = 12.sp,
                            color = cs.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.primary,
                    titleContentColor = cs.onPrimary,
                    navigationIconContentColor = cs.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.padding(padding)) {
                // Tab Row - Scrollable for more tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 16.dp,
                    containerColor = cs.surface,
                    contentColor = cs.primary
                ) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📊", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Genel Bakış")
                        }
                    }
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📚", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Ders Analizi")
                        }
                    }
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📅", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Haftalık Rapor")
                        }
                    }
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("📝", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Denemeler")
                        }
                    }
                    Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("🎯", fontSize = 16.sp)
                            Spacer(Modifier.width(6.dp))
                            Text("Hedefler")
                        }
                    }
                }

                when (selectedTab) {
                    0 -> OverallTab(overallStats, learningCurve)
                    1 -> SubjectsTab(subjectAnalysis)
                    2 -> WeeklyTab(weeklyReports)
                    3 -> TrialExamsTab(trialExams)
                    4 -> GoalsTab(overallStats)
                }
            }
        }
    }
}


// ==================== GENEL TAB ====================

@Composable
private fun OverallTab(
    stats: AnalyticsManager.OverallAnalytics?,
    learningCurve: List<AnalyticsManager.LearningCurvePoint>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stats?.let { s ->
            // Özet Kartı
            item {
                OverallSummaryCard(s)
            }

            // Çalışma Serisi
            item {
                StudyStreakCard(s.studyStreak, s.totalStudyDays, s.averageDaily)
            }

            // Öğrenme Eğrisi
            if (learningCurve.isNotEmpty()) {
                item {
                    LearningCurveCard(learningCurve)
                }
            }

            // Güçlü ve Zayıf Dersler
            item {
                StrengthWeaknessCard(s.strongSubjects, s.weakSubjects)
            }
        }
    }
}

@Composable
private fun OverallSummaryCard(stats: AnalyticsManager.OverallAnalytics) {
    val trendIcon = when (stats.recentTrend) {
        AnalyticsManager.Trend.IMPROVING -> Icons.AutoMirrored.Rounded.TrendingUp
        AnalyticsManager.Trend.DECLINING -> Icons.AutoMirrored.Rounded.TrendingDown
        AnalyticsManager.Trend.STABLE -> Icons.Rounded.Remove
    }
    val trendColor = when (stats.recentTrend) {
        AnalyticsManager.Trend.IMPROVING -> Color(0xFF4CAF50)
        AnalyticsManager.Trend.DECLINING -> Color(0xFFE57373)
        AnalyticsManager.Trend.STABLE -> Color(0xFFFFB74D)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5))
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Genel Performans", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.weight(1f))
                Icon(trendIcon, null, tint = trendColor, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("Toplam Soru", stats.totalQuestions.toString(), Color.White)
                StatItem("Başarı", "%.1f%%".format(stats.overallSuccessRate), Color.White)
                StatItem("Doğru", stats.totalCorrect.toString(), Color(0xFF81C784))
                StatItem("Yanlış", stats.totalWrong.toString(), Color(0xFFE57373))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 12.sp, color = color.copy(alpha = 0.8f))
    }
}

@Composable
private fun StudyStreakCard(streak: Int, totalDays: Int, avgDaily: Float) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFB74D).copy(alpha = 0.15f))
    ) {
        Row(
            Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🔥", fontSize = 32.sp)
                Text("$streak Gün", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                Text("Seri", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📅", fontSize = 32.sp)
                Text("$totalDays Gün", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                Text("Toplam", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📊", fontSize = 32.sp)
                Text("%.1f".format(avgDaily), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                Text("Günlük Ort.", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun LearningCurveCard(points: List<AnalyticsManager.LearningCurvePoint>) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("Öğrenme Eğrisi 📈", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
            Text("Son 14 günlük kümülatif başarı", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
            
            Spacer(Modifier.height(16.dp))
            
            // Basit çizgi grafik
            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                if (points.isEmpty()) return@Canvas
                
                val maxRate = 100f
                val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                
                val path = Path()
                points.forEachIndexed { index, point ->
                    val x = index * stepX
                    val y = size.height - (point.successRate / maxRate * size.height)
                    
                    if (index == 0) path.moveTo(x, y)
                    else path.lineTo(x, y)
                }
                
                drawPath(
                    path = path,
                    color = Color(0xFF1E88E5),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Noktalar
                points.forEachIndexed { index, point ->
                    val x = index * stepX
                    val y = size.height - (point.successRate / maxRate * size.height)
                    drawCircle(Color(0xFF1E88E5), radius = 4.dp.toPx(), center = Offset(x, y))
                }
            }
            
            // Son değer
            points.lastOrNull()?.let { last ->
                Text(
                    "Güncel Başarı: %.1f%%".format(last.successRate),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun StrengthWeaknessCard(strong: List<String>, weak: List<String>) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface)
    ) {
        Column(Modifier.padding(20.dp)) {
            // Güçlü Dersler
            if (strong.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💪", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Güçlü Dersler", fontWeight = FontWeight.Bold, color = cs.onSurface)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    strong.forEach { subject ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                        ) {
                            Text(
                                subject,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFF4CAF50),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            if (strong.isNotEmpty() && weak.isNotEmpty()) {
                HorizontalDivider(Modifier.padding(vertical = 16.dp), color = cs.outlineVariant)
            }

            // Zayıf Dersler
            if (weak.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠️", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Geliştirilmesi Gereken", fontWeight = FontWeight.Bold, color = cs.onSurface)
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    weak.forEach { subject ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE57373).copy(alpha = 0.15f)
                        ) {
                            Text(
                                subject,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFFE57373),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            if (strong.isEmpty() && weak.isEmpty()) {
                Text("Henüz yeterli veri yok", color = cs.onSurface.copy(alpha = 0.6f))
            }
        }
    }
}


// ==================== DERSLER TAB ====================

@Composable
private fun SubjectsTab(subjects: List<AnalyticsManager.SubjectAnalysis>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (subjects.isEmpty()) {
            item {
                EmptyStateCard("Henüz ders verisi yok", "Soru çözerek analizi başlatın")
            }
        } else {
            items(subjects) { subject ->
                SubjectAnalysisCard(subject)
            }
        }
    }
}

@Composable
private fun SubjectAnalysisCard(subject: AnalyticsManager.SubjectAnalysis) {
    val cs = MaterialTheme.colorScheme
    val trendIcon = when (subject.trend) {
        AnalyticsManager.Trend.IMPROVING -> "📈"
        AnalyticsManager.Trend.DECLINING -> "📉"
        AnalyticsManager.Trend.STABLE -> "➡️"
    }
    val rateColor = when {
        subject.successRate >= 80 -> Color(0xFF4CAF50)
        subject.successRate >= 60 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(subject.subject, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f), color = cs.onSurface)
                Text(trendIcon, fontSize = 18.sp)
            }

            Spacer(Modifier.height(12.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(cs.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(subject.successRate / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(rateColor)
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("%.1f%% Başarı".format(subject.successRate), color = rateColor, fontWeight = FontWeight.Bold)
                Text("${subject.totalQuestions} Soru", color = cs.onSurface.copy(alpha = 0.6f), fontSize = 13.sp)
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("✅ ${subject.correctCount} Doğru", color = Color(0xFF4CAF50), fontSize = 13.sp)
                Text("❌ ${subject.wrongCount} Yanlış", color = Color(0xFFE57373), fontSize = 13.sp)
            }

            // Zayıf Konular
            if (subject.weakTopics.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Zayıf Konular:", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    subject.weakTopics.take(3).forEach { topic ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF3E0)
                        ) {
                            Text(
                                topic,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            // Haftalık Karşılaştırma
            if (subject.lastWeekRate > 0 || subject.thisWeekRate > 0) {
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Geçen Hafta: %.0f%%".format(subject.lastWeekRate), fontSize = 12.sp, color = Color.Gray)
                    Text("Bu Hafta: %.0f%%".format(subject.thisWeekRate), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

// ==================== HAFTALIK TAB ====================

@Composable
private fun WeeklyTab(reports: List<AnalyticsManager.WeeklyReport>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (reports.isEmpty()) {
            item {
                EmptyStateCard("Haftalık rapor yok", "Düzenli çalışarak raporları oluşturun")
            }
        } else {
            items(reports) { report ->
                WeeklyReportCard(report)
            }
        }
    }
}

@Composable
private fun WeeklyReportCard(report: AnalyticsManager.WeeklyReport) {
    val cs = MaterialTheme.colorScheme
    val rateColor = when {
        report.successRate >= 80 -> Color(0xFF4CAF50)
        report.successRate >= 60 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(report.weekLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
                    Text("${report.startDate} - ${report.endDate}", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(rateColor.copy(alpha = 0.1f))
                        .border(2.dp, rateColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("%.0f".format(report.successRate), fontWeight = FontWeight.Bold, color = rateColor)
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${report.totalQuestions}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                    Text("Soru", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${report.studyDays}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                    Text("Gün", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%.1f".format(report.averageDaily), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onSurface)
                    Text("Günlük", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                }
            }

            // Ders Dağılımı
            if (report.subjectBreakdown.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = cs.outlineVariant)
                Spacer(Modifier.height(12.dp))
                Text("Ders Dağılımı", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = cs.onSurface)
                Spacer(Modifier.height(8.dp))
                report.subjectBreakdown.entries.take(4).forEach { (subject, stats) ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(subject, fontSize = 13.sp, color = cs.onSurface.copy(alpha = 0.8f))
                        Text("${stats.first}D / ${stats.second}Y", fontSize = 13.sp, color = cs.onSurface.copy(alpha = 0.6f))
                    }
                }
            }
        }
    }
}

// ==================== DENEMELER TAB ====================

@Composable
private fun TrialExamsTab(exams: List<AnalyticsManager.TrialExamAnalysis>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (exams.isEmpty()) {
            item {
                EmptyStateCard("Deneme sınavı yok", "Deneme çözerek analizleri görün")
            }
        } else {
            // Ortalama
            item {
                val avgScore = exams.map { it.score }.average().toInt()
                TrialAverageCard(avgScore, exams.size)
            }

            items(exams) { exam ->
                TrialExamCard(exam)
            }
        }
    }
}

@Composable
private fun TrialAverageCard(avgScore: Int, examCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF673AB7))
    ) {
        Row(
            Modifier.padding(20.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Deneme Ortalaması", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("$avgScore Puan", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("$examCount deneme sınavı", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Text("🏆", fontSize = 48.sp)
        }
    }
}

@Composable
private fun TrialExamCard(exam: AnalyticsManager.TrialExamAnalysis) {
    val cs = MaterialTheme.colorScheme
    val scoreColor = when {
        exam.score >= 85 -> Color(0xFF4CAF50)
        exam.score >= 70 -> Color(0xFF2196F3)
        exam.score >= 50 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface)
    ) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(scoreColor.copy(alpha = 0.1f))
                    .border(2.dp, scoreColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("${exam.score}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = scoreColor)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Deneme #${exam.examId}", fontWeight = FontWeight.Bold, color = cs.onSurface)
                Text(exam.date, fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                Text(exam.rank, fontSize = 13.sp, color = scoreColor, fontWeight = FontWeight.Medium)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("✅ ${exam.correct}", color = Color(0xFF4CAF50), fontSize = 13.sp)
                Text("❌ ${exam.wrong}", color = Color(0xFFE57373), fontSize = 13.sp)
                if (exam.improvement != 0) {
                    val impColor = if (exam.improvement > 0) Color(0xFF4CAF50) else Color(0xFFE57373)
                    val impText = if (exam.improvement > 0) "+${exam.improvement}" else "${exam.improvement}"
                    Text(impText, color = impColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==================== ORTAK BİLEŞENLER ====================

@Composable
private fun EmptyStateCard(title: String, subtitle: String) {
    val cs = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant)
    ) {
        Column(
            Modifier.padding(32.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📊", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
            Text(subtitle, color = cs.onSurface.copy(alpha = 0.6f), fontSize = 14.sp)
        }
    }
}

// ==================== HEDEFLER TAB ====================

@Composable
private fun GoalsTab(stats: AnalyticsManager.OverallAnalytics?) {
    val cs = MaterialTheme.colorScheme
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Günlük Hedef
        item {
            GoalCard(
                title = "Günlük Hedef",
                emoji = "🎯",
                current = stats?.averageDaily?.toInt() ?: 0,
                target = 30,
                unit = "soru",
                color = Color(0xFF2196F3),
                description = "Her gün en az 30 soru çöz"
            )
        }
        
        // Haftalık Hedef
        item {
            GoalCard(
                title = "Haftalık Hedef",
                emoji = "📅",
                current = (stats?.averageDaily?.times(7))?.toInt() ?: 0,
                target = 200,
                unit = "soru",
                color = Color(0xFF9C27B0),
                description = "Haftada 200 soru hedefle"
            )
        }
        
        // Başarı Oranı Hedefi
        item {
            GoalCard(
                title = "Başarı Hedefi",
                emoji = "🏆",
                current = stats?.overallSuccessRate?.toInt() ?: 0,
                target = 80,
                unit = "%",
                color = Color(0xFF4CAF50),
                description = "%80 üzeri başarı oranı"
            )
        }
        
        // Çalışma Serisi Hedefi
        item {
            GoalCard(
                title = "Çalışma Serisi",
                emoji = "🔥",
                current = stats?.studyStreak ?: 0,
                target = 7,
                unit = "gün",
                color = Color(0xFFFF9800),
                description = "7 gün üst üste çalış"
            )
        }
        
        // Motivasyon Kartı
        item {
            MotivationCard(stats)
        }
        
        // İpuçları
        item {
            TipsCard()
        }
    }
}

@Composable
private fun GoalCard(
    title: String,
    emoji: String,
    current: Int,
    target: Int,
    unit: String,
    color: Color,
    description: String
) {
    val cs = MaterialTheme.colorScheme
    val progress = (current.toFloat() / target).coerceIn(0f, 1f)
    val isCompleted = current >= target
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) color.copy(alpha = 0.15f) else cs.surface
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 32.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
                    Text(description, fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                }
                if (isCompleted) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4CAF50)
                    ) {
                        Icon(
                            Icons.Rounded.Check,
                            null,
                            tint = Color.White,
                            modifier = Modifier.padding(4.dp).size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(cs.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(color, color.copy(alpha = 0.7f))
                            )
                        )
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "$current $unit",
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    "Hedef: $target $unit",
                    color = cs.onSurface.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun MotivationCard(stats: AnalyticsManager.OverallAnalytics?) {
    val cs = MaterialTheme.colorScheme
    val motivationText = when {
        (stats?.overallSuccessRate ?: 0f) >= 80 -> "Harika gidiyorsun! 🌟 Bu performansı sürdür!"
        (stats?.overallSuccessRate ?: 0f) >= 60 -> "İyi ilerliyorsun! 💪 Biraz daha çaba ile zirveye ulaşabilirsin!"
        (stats?.studyStreak ?: 0) >= 3 -> "Düzenli çalışman harika! 🔥 Devam et!"
        else -> "Her yeni gün bir fırsat! 🚀 Bugün başla!"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF673AB7)
        )
    ) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("💡", fontSize = 40.sp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Motivasyon",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    motivationText,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
private fun TipsCard() {
    val cs = MaterialTheme.colorScheme
    val tips = listOf(
        "📖 Her gün düzenli çalış, az ama sürekli",
        "⏰ Sabah saatleri öğrenme için en verimli",
        "🎯 Zayıf konulara öncelik ver",
        "💤 Yeterli uyku hafızayı güçlendirir",
        "🏃 Ara ara mola ver, zihnini dinlendir"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("💡", fontSize = 24.sp)
                Spacer(Modifier.width(8.dp))
                Text("Çalışma İpuçları", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
            }
            
            Spacer(Modifier.height(16.dp))
            
            tips.forEach { tip ->
                Text(
                    tip,
                    fontSize = 14.sp,
                    color = cs.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}
