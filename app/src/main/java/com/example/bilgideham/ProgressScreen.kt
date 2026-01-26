package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlin.math.max

// Grafik verisi modeli
data class ProgressDayPoint(
    val label: String,
    val correct: Int,
    val wrong: Int
)

// Deneme Sınavı Modeli
data class TrialExamSession(
    val id: Int,
    val date: String,
    val correct: Int,
    val wrong: Int,
    val score: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current

    // Kullanıcının eğitim bilgilerini al
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    val userLevel = educationPrefs.level
    val userGrade = educationPrefs.grade

    // Seviyeye göre dinamik ders listesi
    val lessonsUi = remember(userLevel, educationPrefs.schoolType) {
        getLessonsForLevel(userLevel, educationPrefs.schoolType)
    }

    // State tanımları
    var totalCorrect by remember { mutableIntStateOf(0) }
    var totalWrong by remember { mutableIntStateOf(0) }
    var lessonStats by remember { mutableStateOf<Map<String, Pair<Int, Int>>>(emptyMap()) }
    val dailyPoints = remember { mutableStateListOf<ProgressDayPoint>() }
    val trialExamsList = remember { mutableStateListOf<TrialExamSession>() }
    var averageScore by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val allHistory = try { HistoryRepository.getStatsData() } catch (e: Exception) { emptyList() }

        val statsMap = mutableMapOf<String, Pair<Int, Int>>()
        var tc = 0
        var tw = 0
        val trialGroups = mutableMapOf<String, MutableList<Boolean>>()

        allHistory.forEach { item ->
            // Deneme Sınavı Tespiti
            val isTrial = item.lesson.contains("Deneme", true) ||
                    item.lesson.contains("Maraton", true) ||
                    item.lesson.contains("Karma", true)

            if (isTrial) {
                val dateKey = if (item.dateParams.length >= 10) item.dateParams.take(10) else "Bilinmeyen"
                trialGroups.getOrPut(dateKey) { mutableListOf() }.add(item.isCorrect)
            }

            // Ders İstatistikleri - Genişletilmiş eşleştirme
            val lessonName = mapLessonName(item.lesson)

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

        // Deneme sınavlarını oluştur
        val unsortedTrials = trialGroups.map { (dateKey, results) ->
            val c = results.count { it }
            val w = results.count { !it }
            val total = c + w
            TrialExamSession(0, dateKey, c, w, if (total > 0) (c * 100) / total else 0)
        }.sortedBy { it.date }

        trialExamsList.clear()
        unsortedTrials.forEachIndexed { index, session ->
            trialExamsList.add(session.copy(id = index + 1))
        }
        if (trialExamsList.isNotEmpty()) {
            averageScore = trialExamsList.map { it.score }.average().toInt()
        }
        trialExamsList.reverse()

        // Günlük grafik - Düzeltilmiş tarih formatı
        val dateMap = mutableMapOf<String, Pair<Int, Int>>()
        allHistory.forEach {
            // Tarih formatı: "dd MMM HH:mm" -> İlk 6 karakteri al (örn: "23 Ara")
            val dayKey = if (it.dateParams.length >= 6) it.dateParams.take(6).trim() else it.dateParams
            val current = dateMap.getOrDefault(dayKey, 0 to 0)
            if (it.isCorrect) dateMap[dayKey] = (current.first + 1) to current.second
            else dateMap[dayKey] = current.first to (current.second + 1)
        }
        dailyPoints.clear()
        dateMap.entries.toList().takeLast(7).forEach {
            dailyPoints.add(ProgressDayPoint(it.key, it.value.first, it.value.second))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Gelişim Raporu 🚀", color = cs.onPrimary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${userLevel.icon} ${educationPrefs.schoolType.displayName}" +
                                    (userGrade?.let { " • $it. Sınıf" } ?: ""),
                            fontSize = 12.sp,
                            color = cs.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
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
            // Seviye Bilgi Kartı
            LevelInfoCard(userLevel, educationPrefs.schoolType, userGrade)

            // AI Yorumu
            AiCommentCard(totalCorrect, totalWrong, lessonStats, userLevel)

            // Deneme Sınavları
            if (trialExamsList.isNotEmpty()) {
                SectionTitle("Deneme Sınavı Karnesi 🏆")
                AverageScoreCard(averageScore)
                Spacer(Modifier.height(12.dp))
                trialExamsList.forEach { TrialExamItem(it) }
            } else {
                EmptyTrialCard()
            }

            // Ders İstatistikleri - Dinamik
            SectionTitle("Ders Bazlı Detaylar 📊")
            LessonStatsCard(lessonsUi, lessonStats)

            // Günlük Grafik
            SectionTitle("Son 7 Günlük Performans 📅")
            DailyChartCard(dailyPoints)

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ==================== YARDIMCI FONKSİYONLAR ====================

/** Seviyeye göre ders listesi döndürür */
private fun getLessonsForLevel(level: EducationLevel, schoolType: SchoolType): List<Pair<String, String>> {
    return when (level) {
        EducationLevel.ILKOKUL -> listOf(
            "Türkçe" to "📖", "Matematik" to "🔢", "Hayat Bilgisi" to "🌍",
            "Fen Bilimleri" to "🔬", "Sosyal Bilgiler" to "🏛️",
            "İngilizce" to "🇬🇧", "Din Kültürü" to "☪️"
        )
        EducationLevel.ORTAOKUL -> {
            val base = mutableListOf(
                "Türkçe" to "📘", "Matematik" to "🧮", "Fen Bilimleri" to "🧪",
                "Sosyal Bilgiler" to "🌍", "İngilizce" to "🇬🇧",
                "Din Kültürü" to "🕋", "Paragraf" to "📝"
            )
            if (schoolType.name.contains("IMAM_HATIP")) {
                base.add("Arapça" to "🕌")
                base.add("Kur'an-ı Kerim" to "📖")
            }
            base
        }
        EducationLevel.LISE -> {
            val base = mutableListOf(
                "Türk Dili" to "📘", "Matematik" to "🧮", "Tarih" to "📜",
                "Coğrafya" to "🗺️", "İngilizce" to "🇬🇧", "Fizik" to "⚛️",
                "Kimya" to "🧪", "Biyoloji" to "🧬", "Felsefe" to "🤔"
            )
            if (schoolType.name.contains("IMAM_HATIP")) {
                base.add("Arapça" to "🕌")
            }
            base
        }
        EducationLevel.KPSS -> listOf(
            "Türkçe" to "📘", "Matematik" to "🧮", "Tarih" to "📜",
            "Coğrafya" to "🗺️", "Vatandaşlık" to "⚖️", "Güncel" to "📰"
        )
        EducationLevel.AGS -> listOf(
            "Sözel Yetenek" to "📖", "Sayısal Yetenek" to "🔢", "Tarih" to "🏛️",
            "Türkiye Coğrafyası" to "🗺️", "Eğitimin Temelleri" to "🎓", "Mevzuat" to "📜",
            "ÖABT" to "📚"
        )
    }
}

/** Ders adını standart formata dönüştürür */
private fun mapLessonName(lesson: String): String {
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
        lesson.contains("Eğitim", true) -> "Eğitim Bilimleri"
        lesson.contains("Hayat", true) -> "Hayat Bilgisi"
        lesson.contains("Kur'an", true) || lesson.contains("Kuran", true) -> "Kur'an-ı Kerim"
        else -> "Diğer"
    }
}

// ==================== UI BİLEŞENLERİ ====================

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF37474F)
    )
}

@Composable
private fun LevelInfoCard(level: EducationLevel, schoolType: SchoolType, grade: Int?) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(level.colorHex)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(level.icon, fontSize = 36.sp)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = schoolType.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                grade?.let {
                    Text(
                        text = "$it. Sınıf Öğrencisi",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AiCommentCard(
    totalCorrect: Int,
    totalWrong: Int,
    lessonStats: Map<String, Pair<Int, Int>>,
    level: EducationLevel
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Psychology, null, tint = Color(0xFFFFB300), modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Yapay Zeka Analizi", fontWeight = FontWeight.Bold, color = Color(0xFFEF6C00), fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))

                val mostWrongLesson = lessonStats.filter { it.value.second > 0 }
                    .maxByOrNull { it.value.second }?.key

                val aiComment = when {
                    (totalCorrect + totalWrong) == 0 -> when (level) {
                        EducationLevel.ILKOKUL -> "Merhaba küçük öğrenci! 🌟 Soru çözerek başla, seni takip edeceğim!"
                        EducationLevel.KPSS -> "KPSS hazırlığına başla! Deneme çözerek analizi aktifleştir."
                        else -> "Henüz veri yok. Soru çözerek analizi başlat!"
                    }
                    totalWrong == 0 -> "Mükemmel! 🎉 Hiç yanlışın yok. Bu performansı sürdür!"
                    totalCorrect > totalWrong * 4 -> "Harika gidiyorsun! 💪 Küçük hatalarını gidersen zirvedesin."
                    mostWrongLesson != null -> "Genel durumun iyi. '$mostWrongLesson' dersine biraz daha çalışmalısın."
                    else -> "İyi gidiyorsun! Düzenli çalışmaya devam et."
                }
                Text(aiComment, fontSize = 14.sp, color = Color(0xFF5D4037), lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun AverageScoreCard(averageScore: Int) {
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
}

@Composable
private fun EmptyTrialCard() {
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

@Composable
fun TrialExamItem(exam: TrialExamSession) {
    val scoreColor = when {
        exam.score >= 90 -> Color(0xFF4CAF50)
        exam.score >= 70 -> Color(0xFF2196F3)
        exam.score >= 50 -> Color(0xFFFF9800)
        else -> Color(0xFFE57373)
    }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(scoreColor.copy(alpha = 0.1f), CircleShape)
                    .border(2.dp, scoreColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("${exam.score}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = scoreColor)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Deneme Sınavı #${exam.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF263238))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(exam.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("✅ ${exam.correct}", fontSize = 13.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                Text("❌ ${exam.wrong}", fontSize = 13.sp, color = Color(0xFFE57373), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LessonStatsCard(lessonsUi: List<Pair<String, String>>, lessonStats: Map<String, Pair<Int, Int>>) {
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
                    HorizontalDivider(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonStatRow(title: String, correct: Int, wrong: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
private fun DailyChartCard(points: List<ProgressDayPoint>) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            DailyBarChart(points = points)
        }
    }
}

@Composable
private fun DailyBarChart(points: List<ProgressDayPoint>) {
    val cs = MaterialTheme.colorScheme

    if (points.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BarChart, null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                Spacer(Modifier.height(8.dp))
                Text("Grafik için veri bekleniyor...", color = Color.Gray, fontSize = 12.sp)
            }
        }
        return
    }

    val maxTotal = max(1, points.maxOfOrNull { it.correct + it.wrong } ?: 1)

    Row(
        modifier = Modifier.fillMaxWidth().height(160.dp).padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        points.forEach { p ->
            val total = (p.correct + p.wrong).coerceAtLeast(0)
            val correctH = if (total > 0) ((p.correct.toFloat() / maxTotal) * 100f).coerceAtLeast(3f) else 3f
            val wrongH = if (total > 0) ((p.wrong.toFloat() / maxTotal) * 100f).coerceAtLeast(0f) else 0f

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
            ) {
                // Toplam sayı
                Text("$total", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                // Yanlış bar (kırmızı)
                if (wrongH > 0) {
                    Box(
                        modifier = Modifier
                            .width(20.dp)
                            .height(wrongH.dp)
                            .background(Color(0xFFE57373), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                    )
                }

                // Doğru bar (yeşil)
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(correctH.dp)
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF81C784))),
                            if (wrongH > 0) RoundedCornerShape(0.dp) else RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                        )
                )

                Spacer(Modifier.height(6.dp))
                Text(p.label, fontSize = 9.sp, color = Color.Gray, maxLines = 1)
            }
        }
    }

    // Açıklama
    Spacer(Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).background(Color(0xFF4CAF50), RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(4.dp))
            Text("Doğru", fontSize = 11.sp, color = Color.Gray)
        }
        Spacer(Modifier.width(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).background(Color(0xFFE57373), RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(4.dp))
            Text("Yanlış", fontSize = 11.sp, color = Color.Gray)
        }
    }
}
