package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush // <-- BU SATIR EKLENDİ (Hatayı Çözen Kısım)
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

// Grafik verisi modeli
data class ProgressDayPoint(
    val label: String,
    val correct: Int,
    val wrong: Int
)

// Deneme Sınavı Modeli (UI için)
data class TrialExamSession(
    val id: Int,            // Deneme #1, #2 gibi
    val date: String,       // Tarih
    val correct: Int,
    val wrong: Int,
    val score: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme

    // --- STATE TANIMLARI ---
    var totalCorrect by remember { mutableIntStateOf(0) }
    var totalWrong by remember { mutableIntStateOf(0) }
    var lessonStats by remember { mutableStateOf<Map<String, Pair<Int, Int>>>(emptyMap()) }
    val dailyPoints = remember { mutableStateListOf<ProgressDayPoint>() }

    // YENİ: Deneme Sınavı Listesi
    val trialExamsList = remember { mutableStateListOf<TrialExamSession>() }
    var averageScore by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        // 1. Veriyi Çek
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }

        // ---------------------------------------------------------
        // A) GENEL İSTATİSTİK & DERS ANALİZİ
        // ---------------------------------------------------------
        val statsMap = mutableMapOf<String, Pair<Int, Int>>()
        var tc = 0
        var tw = 0

        val trialGroups = mutableMapOf<String, MutableList<Boolean>>() // Tarih -> [Doğru/Yanlış Listesi]

        allHistory.forEach { item ->
            // --- Deneme Sınavı Tespiti ---
            val isTrial = item.lesson.contains("Deneme", true) ||
                    item.lesson.contains("Maraton", true) ||
                    item.lesson.contains("Karma", true)

            if (isTrial) {
                // Tarihi "Gün" bazında grupla (Örn: 2025-12-14)
                val dateKey = if (item.dateParams.length >= 10) item.dateParams.take(10) else "Bilinmeyen Tarih"

                if (!trialGroups.containsKey(dateKey)) {
                    trialGroups[dateKey] = mutableListOf()
                }
                trialGroups[dateKey]?.add(item.isCorrect)
            }

            // --- Ders İstatistikleri ---
            val lessonName = when {
                item.lesson.contains("Matematik", true) -> "Matematik"
                item.lesson.contains("Fen", true) -> "Fen Bilimleri"
                item.lesson.contains("Sosyal", true) -> "Sosyal Bilgiler"
                item.lesson.contains("Türkçe", true) || item.lesson.contains("Turkce", true) -> "Türkçe"
                item.lesson.contains("İngilizce", true) || item.lesson.contains("Ingilizce", true) -> "İngilizce"
                item.lesson.contains("Din", true) -> "Din Kültürü"
                item.lesson.contains("Arapça", true) -> "Arapça"
                item.lesson.contains("Paragraf", true) -> "Paragraf"
                else -> "Diğer"
            }

            val current = statsMap.getOrDefault(lessonName, 0 to 0)
            if (item.isCorrect) {
                statsMap[lessonName] = (current.first + 1) to current.second
                tc++
            } else {
                statsMap[lessonName] = current.first to (current.second + 1)
                tw++
            }
        }

        lessonStats = statsMap
        totalCorrect = tc
        totalWrong = tw

        // ---------------------------------------------------------
        // B) DENEME SINAVLARINI OLUŞTURMA VE SIRALAMA
        // ---------------------------------------------------------
        val unsortedTrials = mutableListOf<TrialExamSession>()

        trialGroups.forEach { (dateKey, results) ->
            val c = results.count { it }
            val w = results.count { !it }
            val total = c + w
            val score = if (total > 0) (c * 100) / total else 0

            unsortedTrials.add(
                TrialExamSession(
                    id = 0, // Sonra atanacak
                    date = dateKey,
                    correct = c,
                    wrong = w,
                    score = score
                )
            )
        }

        unsortedTrials.sortBy { it.date }

        trialExamsList.clear()
        unsortedTrials.forEachIndexed { index, session ->
            trialExamsList.add(session.copy(id = index + 1))
        }

        if (trialExamsList.isNotEmpty()) {
            averageScore = trialExamsList.map { it.score }.average().toInt()
        }

        trialExamsList.reverse() // En yeni en üstte

        // ---------------------------------------------------------
        // C) GÜNLÜK GRAFİK VERİSİ
        // ---------------------------------------------------------
        val dateMap = mutableMapOf<String, Pair<Int, Int>>()
        allHistory.forEach {
            val dayKey = if (it.dateParams.length >= 5) it.dateParams.take(5) else it.dateParams
            val current = dateMap.getOrDefault(dayKey, 0 to 0)
            if (it.isCorrect) dateMap[dayKey] = (current.first + 1) to current.second
            else dateMap[dayKey] = current.first to (current.second + 1)
        }
        dailyPoints.clear()
        dateMap.entries.toList().takeLast(7).forEach {
            dailyPoints.add(ProgressDayPoint(it.key, it.value.first, it.value.second))
        }
    }

    val lessonsUi = listOf(
        "Matematik" to "🧮", "Türkçe" to "📘", "Fen Bilimleri" to "🧪", "Sosyal Bilgiler" to "🌍",
        "İngilizce" to "🇬🇧", "Arapça" to "🕌", "Din Kültürü" to "🕋", "Paragraf" to "📝"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gelişim Raporu 🚀", color = cs.onPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "geri", tint = cs.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
        ) {
            // 1. YAPAY ZEKA YORUMU
            itemAiComment(totalCorrect, totalWrong, lessonStats)

            // 2. DENEME SINAVLARI
            if (trialExamsList.isNotEmpty()) {
                Text(
                    "Deneme Sınavı Karnesi 🏆",
                    modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF37474F)
                )

                // Ortalama Puan Kartı
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3F51B5)),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Genel Ortalama", color = Color.White.copy(0.8f), fontSize = 14.sp)
                            Text("$averageScore Puan", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Box(
                            modifier = Modifier.size(50.dp).background(Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Timeline, null, tint = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Sınav Listesi
                trialExamsList.forEach { exam ->
                    TrialExamItem(exam)
                }
            } else {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color.Gray)
                        Spacer(Modifier.width(12.dp))
                        Text("Henüz deneme sınavı çözülmedi.", color = Color.Gray)
                    }
                }
            }

            // 3. DERS İSTATİSTİKLERİ
            Text("Ders Bazlı Detaylar 📊", modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))

            Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    lessonsUi.forEachIndexed { i, (name, icon) ->
                        val stats = lessonStats[name] ?: (0 to 0)
                        LessonStatRow(title = "$icon $name", correct = stats.first, wrong = stats.second)
                        if (i != lessonsUi.lastIndex) {
                            Divider(color = Color.LightGray.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))
                        }
                    }
                }
            }

            // 4. GÜNLÜK GRAFİK
            Text("Son 7 Günlük Performans 📅", modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))

            Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    DailyBarChartSafe(points = dailyPoints)
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun TrialExamItem(exam: TrialExamSession) {
    val scoreColor = when {
        exam.score >= 90 -> Color(0xFF4CAF50) // Yeşil
        exam.score >= 70 -> Color(0xFF2196F3) // Mavi
        exam.score >= 50 -> Color(0xFFFF9800) // Turuncu
        else -> Color(0xFFE57373) // Kırmızı
    }

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Taraf: Puan Yuvarlağı
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(scoreColor.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, scoreColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${exam.score}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )
            }

            Spacer(Modifier.width(16.dp))

            // Orta Kısım: Başlık ve Tarih
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Deneme Sınavı #${exam.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF263238)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(text = exam.date, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Sağ Taraf: Doğru/Yanlış
            Column(horizontalAlignment = Alignment.End) {
                Text("✅ ${exam.correct}", fontSize = 13.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                Text("❌ ${exam.wrong}", fontSize = 13.sp, color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun itemAiComment(totalCorrect: Int, totalWrong: Int, lessonStats: Map<String, Pair<Int, Int>>) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Psychology, null, tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Analiz & Tavsiye", fontWeight = FontWeight.Bold, color = Color(0xFFEF6C00), fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))

                val mostWrongLesson = lessonStats.maxByOrNull { it.value.second }?.key ?: "Yok"
                val aiComment = when {
                    (totalCorrect + totalWrong) == 0 -> "Henüz yeterli veri yok. Deneme çözerek analizi başlat!"
                    totalWrong == 0 -> "Harikasın! Hiç yanlışın yok. Bu tempoyu koru!"
                    totalCorrect > totalWrong * 4 -> "Çok başarılısın! Küçük dikkatsizlikleri de giderirsen zirvedesin."
                    else -> "Genel durumun iyi ancak '$mostWrongLesson' dersine biraz daha ağırlık vermelisin."
                }
                Text(aiComment, fontSize = 14.sp, color = Color(0xFF5D4037), lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun LessonStatRow(title: String, correct: Int, wrong: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 15.sp, color = Color(0xFF455A64), fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text("$correct D", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            Box(Modifier.background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text("$wrong Y", fontSize = 12.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DailyBarChartSafe(points: List<ProgressDayPoint>) {
    val cs = MaterialTheme.colorScheme
    if (points.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Text("Grafik için veri bekleniyor...", color = Color.Gray, fontSize = 12.sp)
        }
        return
    }
    val maxTotal = max(1, points.map { (it.correct + it.wrong).coerceAtLeast(0) }.maxOrNull() ?: 1)

    Row(
        modifier = Modifier.fillMaxWidth().height(140.dp).padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        points.forEach { p ->
            val total = (p.correct + p.wrong).coerceAtLeast(0)
            val h = ((total.toFloat() / maxTotal.toFloat()) * 100f).coerceAtLeast(5f).dp

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                // Bar (Mavi Degrade)
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(h)
                        .background(
                            Brush.verticalGradient(listOf(cs.primary, cs.primary.copy(alpha=0.5f))),
                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                        )
                )
                Spacer(Modifier.height(6.dp))
                Text(p.label, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
            }
        }
    }
}