package com.example.bilgideham.analytics

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * Veli Rapor Ekranı
 * Veliler için detaylı öğrenci performans raporu
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentReportScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var report by remember { mutableStateOf<AnalyticsManager.ParentReport?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            report = AnalyticsManager.generateParentReport(context)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Veli Raporu 📋", fontWeight = FontWeight.Bold)
                        report?.let {
                            Text(it.reportDate, fontSize = 12.sp, color = cs.onPrimary.copy(alpha = 0.7f))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E35B1),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF5E35B1))
                    Spacer(Modifier.height(16.dp))
                    Text("Rapor hazırlanıyor...", color = Color.Gray)
                }
            }
        } else {
            report?.let { r ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Öğrenci Bilgi Kartı
                    item {
                        StudentInfoCard(r.studentName, r.overallStats)
                    }

                    // Öneriler
                    if (r.recommendations.isNotEmpty()) {
                        item {
                            RecommendationsCard(r.recommendations)
                        }
                    }

                    // Haftalık İlerleme
                    if (r.weeklyProgress.isNotEmpty()) {
                        item {
                            SectionTitle("Haftalık İlerleme")
                        }
                        items(r.weeklyProgress) { week ->
                            WeeklyProgressCard(week)
                        }
                    }

                    // Ders Analizi
                    if (r.subjectAnalysis.isNotEmpty()) {
                        item {
                            SectionTitle("Ders Bazlı Performans")
                        }
                        item {
                            SubjectSummaryCard(r.subjectAnalysis)
                        }
                    }

                    // Deneme Sınavları
                    if (r.trialExams.isNotEmpty()) {
                        item {
                            SectionTitle("Deneme Sınavları")
                        }
                        item {
                            TrialExamsSummaryCard(r.trialExams)
                        }
                    }

                    item { Spacer(Modifier.height(32.dp)) }
                }
            } ?: run {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text("Rapor oluşturulamadı", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF37474F),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun StudentInfoCard(name: String, stats: AnalyticsManager.OverallAnalytics) {
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
    val trendText = when (stats.recentTrend) {
        AnalyticsManager.Trend.IMPROVING -> "Yükselişte"
        AnalyticsManager.Trend.DECLINING -> "Düşüşte"
        AnalyticsManager.Trend.STABLE -> "Sabit"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF5E35B1), Color(0xFF7E57C2))
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍🎓", fontSize = 32.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(trendIcon, null, tint = trendColor, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(trendText, color = trendColor, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatBox("Toplam Soru", stats.totalQuestions.toString())
                    StatBox("Başarı", "%.1f%%".format(stats.overallSuccessRate))
                    StatBox("Çalışma Günü", stats.totalStudyDays.toString())
                    StatBox("Seri", "${stats.studyStreak} 🔥")
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
private fun RecommendationsCard(recommendations: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Lightbulb, null, tint = Color(0xFFFFB300), modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text("Öneriler", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFE65100))
            }

            Spacer(Modifier.height(16.dp))

            recommendations.forEach { rec ->
                Text(
                    text = rec,
                    fontSize = 14.sp,
                    color = Color(0xFF5D4037),
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun WeeklyProgressCard(week: AnalyticsManager.WeeklyReport) {
    val rateColor = when {
        week.successRate >= 80 -> Color(0xFF4CAF50)
        week.successRate >= 60 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(week.weekLabel, fontWeight = FontWeight.Bold)
                Text("${week.startDate} - ${week.endDate}", fontSize = 12.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("${week.totalQuestions} soru", fontSize = 13.sp, color = Color.DarkGray)
                    Text(" • ", color = Color.Gray)
                    Text("${week.studyDays} gün", fontSize = 13.sp, color = Color.DarkGray)
                }
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(rateColor.copy(alpha = 0.1f))
                    .border(2.dp, rateColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("%.0f".format(week.successRate), fontWeight = FontWeight.Bold, color = rateColor, fontSize = 16.sp)
                    Text("%", fontSize = 10.sp, color = rateColor)
                }
            }
        }
    }
}

@Composable
private fun SubjectSummaryCard(subjects: List<AnalyticsManager.SubjectAnalysis>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            subjects.take(6).forEach { subject ->
                val rateColor = when {
                    subject.successRate >= 80 -> Color(0xFF4CAF50)
                    subject.successRate >= 60 -> Color(0xFFFFB74D)
                    else -> Color(0xFFE57373)
                }
                val trendEmoji = when (subject.trend) {
                    AnalyticsManager.Trend.IMPROVING -> "📈"
                    AnalyticsManager.Trend.DECLINING -> "📉"
                    AnalyticsManager.Trend.STABLE -> "➡️"
                }

                Row(
                    Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(subject.subject, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                    Text(trendEmoji, fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "%.0f%%".format(subject.successRate),
                        color = rateColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (subjects.indexOf(subject) < subjects.size - 1) {
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
private fun TrialExamsSummaryCard(exams: List<AnalyticsManager.TrialExamAnalysis>) {
    val avgScore = exams.map { it.score }.average().toInt()
    val lastExam = exams.firstOrNull()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Ortalama Puan", fontSize = 13.sp, color = Color.Gray)
                    Text("$avgScore", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = Color(0xFF5E35B1))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Toplam Deneme", fontSize = 13.sp, color = Color.Gray)
                    Text("${exams.size}", fontWeight = FontWeight.Bold, fontSize = 28.sp)
                }
            }

            lastExam?.let { exam ->
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))

                Text("Son Deneme", fontSize = 13.sp, color = Color.Gray)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${exam.score} puan (${exam.rank})", fontWeight = FontWeight.Medium)
                    if (exam.improvement != 0) {
                        val impColor = if (exam.improvement > 0) Color(0xFF4CAF50) else Color(0xFFE57373)
                        val impText = if (exam.improvement > 0) "+${exam.improvement}" else "${exam.improvement}"
                        Text(impText, color = impColor, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
