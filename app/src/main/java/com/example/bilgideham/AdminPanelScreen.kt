package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdminPanelScreen(onBack: () -> Unit) {
    var isAuthenticated by remember { mutableStateOf(false) }
    if (isAuthenticated) AdminDashboardContent(onBack) else AdminLoginScreen(
        onLoginSuccess = { isAuthenticated = true },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val adminPassword = "787878"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yönetici Girişi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1E88E5).copy(alpha = 0.12f),
                modifier = Modifier.size(96.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lock, null, tint = Color(0xFF1E88E5), modifier = Modifier.size(44.dp))
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("Admin Paneli Kilitli", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; isError = false },
                label = { Text("Şifre") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                    }
                },
                isError = isError,
                modifier = Modifier.fillMaxWidth(0.84f),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            if (isError) {
                Text("Hatalı şifre!", color = Color(0xFFD32F2F), fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = { if (password == adminPassword) onLoginSuccess() else isError = true },
                modifier = Modifier.fillMaxWidth(0.84f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Icon(Icons.Default.Login, null)
                Spacer(Modifier.width(10.dp))
                Text("GİRİŞ YAP", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardContent(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val isCompact = config.screenWidthDp < 380

    // Responsive tipografi ölçüleri
    val summaryTitleSp = if (isCompact) 10.sp else 12.sp
    val summaryValueSp = if (isCompact) 20.sp else 24.sp
    val statValueSp = if (isCompact) 16.sp else 20.sp
    val statLabelSp = if (isCompact) 10.sp else 12.sp
    val chipTextSp = if (isCompact) 10.sp else 11.sp
    val pillTextSp = if (isCompact) 11.sp else 12.sp
    val headerTextSp = if (isCompact) 12.sp else 13.sp

    // --- KRİTİK DÜZELTME: Veritabanlarını başlatıyoruz ---
    LaunchedEffect(Unit) {
        runCatching { LessonRepositoryLocal.init(context) }
        runCatching { GameRepositoryNew.init(context) } // <-- EKLENEN KISIM: Oyun veritabanını başlatır.
    }

    var selectedLesson by remember { mutableStateOf("Matematik") }

    // İstatistikler
    var firestoreCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var roomCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var lessonCacheCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }

    var isWorking by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Hazır") }
    var gameDataStatus by remember { mutableStateOf("Hazır") }
    var lessonCacheStatus by remember { mutableStateOf("Hazır") }

    var isSmartBalancingRunning by remember { mutableStateOf(false) }
    var currentTaskName by remember { mutableStateOf("") }
    var progressPercent by remember { mutableFloatStateOf(0f) }
    var detailedLogs by remember { mutableStateOf("Sistem hazır. Bekleniyor...") }

    // Foreground Service durumunu UI'a bağla (ekranda kalma zorunluluğunu kaldırır)
    val turboState by TurboBalancingBus.state.collectAsState()
    LaunchedEffect(turboState) {
        isSmartBalancingRunning = turboState.isRunning
        currentTaskName = turboState.task
        progressPercent = turboState.progress
        if (turboState.logs.isNotBlank()) detailedLogs = turboState.logs
        turboState.lastCloudCounts?.let { firestoreCounts = it }
    }

    val lessons = listOf(
        "Matematik", "Türkçe", "Fen Bilimleri", "Sosyal Bilgiler", "Din Kültürü",
        "Arapça", "Paragraf", "İngilizce (A1)", "İngilizce (A2)", "İngilizce (B1)", "Deneme Sınavı"
    )

    fun mapLessonToLocalTag(title: String): String {
        val t = title.lowercase()
        return when {
            t.contains("fen") -> "Fen"
            t.contains("sosyal") -> "Sosyal"
            t.contains("türkçe") || t.contains("turkce") -> "Turkce"
            t.contains("ingilizce") || t.contains("english") || t.contains("ing") -> "Ingilizce"
            t.contains("mat") -> "Matematik"
            t.contains("arap") -> "Arapca"
            t.contains("din") -> "Din"
            t.contains("paragraf") -> "Paragraf"
            t.contains("deneme") -> "Deneme"
            else -> title
        }
    }

    suspend fun refreshAllStats() {
        firestoreCounts = QuestionRepository.getQuestionCounts()
        roomCounts = GameRepositoryNew.getStats() // Cihazdaki oyun soru sayısı

        val tags = listOf("Matematik", "Turkce", "Fen", "Sosyal", "Ingilizce", "Din", "Arapca", "Paragraf", "Deneme")
        val map = mutableMapOf<String, Int>()
        for (tag in tags) {
            val count = runCatching { LessonRepositoryLocal.getAllQuestions(tag).size }.getOrDefault(0)
            map[tag] = count
        }
        lessonCacheCounts = map
    }

    LaunchedEffect(Unit) { refreshAllStats() }

    val totalCloud = firestoreCounts.values.sum()
    val totalGameLocal = roomCounts.values.sum()
    val totalLessonLocal = lessonCacheCounts.values.sum()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yönetici Paneli", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isWorking = true
                                detailedLogs = "🔄 İstatistikler yenileniyor...\n$detailedLogs"
                                runCatching { refreshAllStats() }
                                isWorking = false
                            }
                        }
                    ) { Icon(Icons.Default.Refresh, null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E88E5))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- ÖZET KARTLARI ---
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard(
                    title = "Sınav\nBulut",
                    value = totalCloud,
                    icon = Icons.Default.Cloud,
                    color = Color(0xFF1E88E5),
                    modifier = Modifier.weight(1f),
                    titleSp = summaryTitleSp,
                    valueSp = summaryValueSp
                )
                SummaryCard(
                    title = "Oyun\nCihaz",
                    value = totalGameLocal,
                    icon = Icons.Default.Gamepad,
                    color = Color(0xFFE91E63),
                    modifier = Modifier.weight(1f),
                    titleSp = summaryTitleSp,
                    valueSp = summaryValueSp
                )
                SummaryCard(
                    title = "Ders\nÖnbellek",
                    value = totalLessonLocal,
                    icon = Icons.Default.Storage,
                    color = Color(0xFF009688),
                    modifier = Modifier.weight(1f),
                    titleSp = summaryTitleSp,
                    valueSp = summaryValueSp
                )
            }

            Spacer(Modifier.height(18.dp))

            // --- 1. OYUN HAVUZU YÖNETİMİ (ARTIK BULUTA YÜKLER) ---
            SectionHeader("OYUN HAVUZU (BULUT YÖNETİMİ)", Icons.Default.Gamepad, Color(0xFFE91E63), headerTextSp)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Buradaki istatistikler admin cihazındaki mevcut oyun sorularıdır. Diğer kullanıcılar buluttan çeker.",
                        fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp)
                    )

                    StatRow(
                        items = listOf(
                            Triple("Fen", roomCounts["Fen"] ?: 0, Color(0xFF303F9F)),
                            Triple("Sosyal", roomCounts["Sosyal"] ?: 0, Color(0xFFE91E63)),
                            Triple("Türkçe", roomCounts["Turkce"] ?: 0, Color(0xFF009688)),
                            Triple("İng.", roomCounts["Ingilizce"] ?: 0, Color(0xFFFF9800))
                        ),
                        valueSp = statValueSp,
                        labelSp = statLabelSp
                    )

                    Spacer(Modifier.height(12.dp))

                    // --- KRİTİK DEĞİŞİKLİK: BULUTA YÜKLEME BUTONU ---
                    Button(
                        onClick = {
                            scope.launch {
                                isWorking = true
                                gameDataStatus = "⏳ Buluta Yükleniyor..."
                                detailedLogs = "☁️ OYUN SORULARI OLUŞTURULUYOR VE BULUTA GÖNDERİLİYOR...\n$detailedLogs"
                                try {
                                    // 1. Yeni sorular üret ve Firebase'e yükle (Tüm kullanıcılar için)
                                    GameRepositoryNew.generateAndUploadToCloud()

                                    // 2. Adminin kendi telefonuna da indir (Test etmek için)
                                    GameRepositoryNew.syncFromCloudToDevice()

                                    refreshAllStats()
                                    gameDataStatus = "✅ Buluta Yüklendi!"
                                    detailedLogs = "✅ Başarılı! Tüm kullanıcılar oyuna girdiğinde bu soruları alacak.\n$detailedLogs"
                                } catch (e: Exception) {
                                    gameDataStatus = "❌ Hata"
                                    detailedLogs = "❌ Bulut Hatası: ${e.message}\n$detailedLogs"
                                }
                                isWorking = false
                            }
                        },
                        enabled = !isWorking,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
                    ) {
                        if (isWorking) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.CloudUpload, null) // İkon değişti
                            Spacer(Modifier.width(10.dp))
                            Text("OYUN HAVUZUNU DOLDUR (BULUT)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    if (gameDataStatus != "Hazır") {
                        Spacer(Modifier.height(8.dp))
                        InfoPill(gameDataStatus, if (gameDataStatus.contains("✅")) Color(0xFF2E7D32) else Color(0xFFC62828), pillTextSp)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // --- 2. DERS ÖNBELLEĞİ ---
            SectionHeader("DERS ÖNBELLEĞİ (CİHAZ)", Icons.Default.Storage, Color(0xFF009688), headerTextSp)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    StatRow(
                        items = listOf(
                            Triple("Mat", lessonCacheCounts["Matematik"] ?: 0, Color(0xFF1976D2)),
                            Triple("Trk", lessonCacheCounts["Turkce"] ?: 0, Color(0xFF009688)),
                            Triple("Fen", lessonCacheCounts["Fen"] ?: 0, Color(0xFF303F9F)),
                            Triple("Sos", lessonCacheCounts["Sosyal"] ?: 0, Color(0xFFE91E63))
                        ),
                        valueSp = statValueSp,
                        labelSp = statLabelSp
                    )
                    Spacer(Modifier.height(10.dp))
                    StatRow(
                        items = listOf(
                            Triple("İng", lessonCacheCounts["Ingilizce"] ?: 0, Color(0xFFFF9800)),
                            Triple("Din", lessonCacheCounts["Din"] ?: 0, Color(0xFF6D4C41)),
                            Triple("Ara", lessonCacheCounts["Arapca"] ?: 0, Color(0xFF455A64)),
                            Triple("Prg", lessonCacheCounts["Paragraf"] ?: 0, Color(0xFF5E35B1))
                        ),
                        valueSp = statValueSp,
                        labelSp = statLabelSp
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    isWorking = true
                                    lessonCacheStatus = "⏳ Temizleniyor..."
                                    val tag = mapLessonToLocalTag(selectedLesson)
                                    detailedLogs = "🧹 Ders önbelleği temizleniyor: $tag\n$detailedLogs"
                                    runCatching { LessonRepositoryLocal.clearLesson(tag) }
                                    runCatching { refreshAllStats() }
                                    lessonCacheStatus = "✅ Temizlendi: $tag"
                                    isWorking = false
                                }
                            },
                            enabled = !isWorking,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.DeleteSweep, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Seçili Dersi\nTemizle", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    isWorking = true
                                    lessonCacheStatus = "⏳ Tümü temizleniyor..."
                                    detailedLogs = "🧹 Tüm ders önbelleği temizleniyor...\n$detailedLogs"
                                    val tags = listOf("Matematik", "Turkce", "Fen", "Sosyal", "Ingilizce", "Din", "Arapca", "Paragraf", "Deneme")
                                    tags.forEach { runCatching { LessonRepositoryLocal.clearLesson(it) } }
                                    runCatching { refreshAllStats() }
                                    lessonCacheStatus = "✅ Tüm ders önbelleği temizlendi"
                                    isWorking = false
                                }
                            },
                            enabled = !isWorking,
                            modifier = Modifier.weight(1f).height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
                        ) {
                            Icon(Icons.Default.DeleteForever, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Tümünü\nTemizle", fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }

                    if (lessonCacheStatus != "Hazır") {
                        Spacer(Modifier.height(8.dp))
                        InfoPill(lessonCacheStatus, if (lessonCacheStatus.contains("✅")) Color(0xFF2E7D32) else Color(0xFFC62828), pillTextSp)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // --- 3. SINAV HAVUZU (BULUT) ---
            SectionHeader("SINAV HAVUZU (BULUT)", Icons.Default.Analytics, Color(0xFF1E88E5), headerTextSp)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    val allStatLessons = listOf(
                        "Matematik", "Türkçe", "Fen Bilimleri", "Sosyal Bilgiler",
                        "İngilizce (A1)", "İngilizce (A2)", "İngilizce (B1)",
                        "Din Kültürü", "Arapça", "Paragraf", "Deneme Sınavı"
                    )

                    allStatLessons.chunked(3).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            row.forEach { l ->
                                val shortName = when {
                                    l.contains("İngilizce") -> l.replace("İngilizce", "İng")
                                    l.contains("Bilimleri") -> "Fen"
                                    l.contains("Bilgiler") -> "Sosyal"
                                    l.contains("Sınavı") -> "Deneme"
                                    else -> l
                                }
                                val count = firestoreCounts[l] ?: 0
                                TinyStatChip(shortName, count, chipTextSp)
                            }
                            repeat((3 - row.size).coerceAtLeast(0)) { Spacer(Modifier.width(1.dp)) }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // --- 4. AKILLI OTOMASYON (LOGLAR VB) ---
            SectionHeader("AKILLI OTOMASYON", Icons.Default.Bolt, Color(0xFFFF9800), headerTextSp)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = if (isSmartBalancingRunning) Color(0xFFE65100) else Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Turbo Dengeleme UI (Aynı kalıyor)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSmartBalancingRunning) Color.White.copy(alpha = 0.18f) else Color(0xFFFF9800).copy(alpha = 0.14f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AutoMode, null, tint = if (isSmartBalancingRunning) Color.White else Color(0xFFE65100))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "AKILLI DENGELEME (TURBO)",
                                fontWeight = FontWeight.Bold,
                                color = if (isSmartBalancingRunning) Color.White else Color(0xFF37474F),
                                fontSize = 13.sp
                            )
                            Text(
                                "En az soru olan dersi bulur ve 15’er 15’er ekler (Deneme hariç).",
                                fontSize = 11.sp,
                                color = if (isSmartBalancingRunning) Color.White.copy(alpha = 0.85f) else Color.Gray,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = {
                                val targets = lessons.filter { it != "Deneme Sınavı" }
                                if (isSmartBalancingRunning) {
                                    detailedLogs = "⛔ Turbo dengeleme durduruluyor...\n$detailedLogs"
                                    TurboForegroundService.stop(context)
                                } else {
                                    detailedLogs = "🚀 Turbo dengeleme arka planda başlatılıyor...\n$detailedLogs"
                                    TurboForegroundService.start(context, targets)
                                }
                            }
                        ) {
                            Icon(
                                if (isSmartBalancingRunning) Icons.Default.Close else Icons.Default.AutoAwesome,
                                null,
                                tint = if (isSmartBalancingRunning) Color.White else Color(0xFFE65100)
                            )
                        }
                    }

                    if (isSmartBalancingRunning) {
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { progressPercent.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            currentTaskName,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.95f),
                            modifier = Modifier.align(Alignment.End),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Log Paneli
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().height(170.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(12.dp).verticalScroll(rememberScrollState())) {
                    Text("SİSTEM LOGLARI:", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text(detailedLogs, color = Color.White, fontSize = 11.sp, lineHeight = 14.sp)
                }
            }

            Spacer(Modifier.height(18.dp))

            // --- 5. TEKİL EKLEME (SINAV) ---
            SectionHeader("TEKİL EKLEME & DENEME (SINAV)", Icons.Default.Add, Color(0xFF607D8B), headerTextSp)
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    ScrollableTabRow(
                        selectedTabIndex = lessons.indexOf(selectedLesson).coerceAtLeast(0),
                        edgePadding = 0.dp,
                        containerColor = Color.Transparent,
                        indicator = { tabPositions ->
                            val idx = lessons.indexOf(selectedLesson)
                            if (idx != -1) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[idx]),
                                    color = Color(0xFF1E88E5)
                                )
                            }
                        },
                        divider = {}
                    ) {
                        lessons.forEach { l ->
                            Tab(
                                selected = selectedLesson == l,
                                onClick = { selectedLesson = l },
                                text = {
                                    Text(
                                        l.replace("İngilizce", "İng.").replace("Deneme Sınavı", "DENEME"),
                                        color = if (selectedLesson == l) Color(0xFF1E88E5) else Color.Gray,
                                        fontWeight = if (selectedLesson == l) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    var countText by remember { mutableStateOf("15") }
                    OutlinedTextField(
                        value = countText,
                        onValueChange = { countText = it.filter { ch -> ch.isDigit() }.take(3) },
                        label = { Text("Kaç soru eklensin?") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                isWorking = true
                                statusMessage = "⏳ Üretiliyor..."
                                val count = countText.toIntOrNull()?.coerceIn(1, 200) ?: 15
                                detailedLogs = "➕ Tekil ekleme: $selectedLesson ($count)\n$detailedLogs"

                                try {
                                    val qs = AiQuestionGenerator().generateBatch(selectedLesson, count)
                                    val saved = QuestionRepository.saveQuestionsToFirestore(qs)
                                    statusMessage = "✅ Eklendi: $saved"
                                    detailedLogs = "✅ $selectedLesson: $saved soru eklendi.\n$detailedLogs"
                                    refreshAllStats()
                                } catch (e: Exception) {
                                    statusMessage = "❌ Hata"
                                    detailedLogs = "❌ Hata: ${e.message}\n$detailedLogs"
                                }
                                isWorking = false
                            }
                        },
                        enabled = !isWorking,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
                    ) {
                        if (isWorking) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AddCircle, null)
                            Spacer(Modifier.width(10.dp))
                            Text("SEÇİLİ DERSE EKLE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    if (statusMessage != "Hazır") {
                        Spacer(Modifier.height(8.dp))
                        InfoPill(statusMessage, if (statusMessage.contains("✅")) Color(0xFF2E7D32) else Color(0xFFC62828), pillTextSp)
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/* ----------------- UI BİLEŞENLERİ ----------------- */

@Composable
fun SummaryCard(
    title: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
    titleSp: androidx.compose.ui.unit.TextUnit,
    valueSp: androidx.compose.ui.unit.TextUnit
) {
    ElevatedCard(
        modifier = modifier.height(86.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(14.dp), color = color.copy(alpha = 0.12f), modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color) }
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(title, fontSize = titleSp, color = Color.Gray, lineHeight = 12.sp)
                Text("$value", fontSize = valueSp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF263238))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector, color: Color, textSp: androidx.compose.ui.unit.TextUnit) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.12f), modifier = Modifier.size(36.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) }
        }
        Spacer(Modifier.width(10.dp))
        Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF37474F), fontSize = textSp)
    }
}

@Composable
fun StatRow(
    items: List<Triple<String, Int, Color>>,
    valueSp: androidx.compose.ui.unit.TextUnit,
    labelSp: androidx.compose.ui.unit.TextUnit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        items.forEach { (label, value, color) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$value", fontSize = valueSp, fontWeight = FontWeight.ExtraBold, color = color)
                Text(label, fontSize = labelSp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TinyStatChip(label: String, value: Int, textSp: androidx.compose.ui.unit.TextUnit) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFFF1F3F5)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = textSp, color = Color(0xFF37474F), fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(6.dp))
            Surface(shape = RoundedCornerShape(999.dp), color = Color.White) {
                Text(
                    value.toString(),
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                    fontSize = textSp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E88E5),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun InfoPill(text: String, color: Color, textSp: androidx.compose.ui.unit.TextUnit) {
    Surface(shape = RoundedCornerShape(999.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = textSp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* ----------------- AKILLI DENGELEME (Eski coroutine operasyonu - artık Service ile çalışıyor) ----------------- */

suspend fun runSmartBalancingOperation(
    targetLessons: List<String>,
    generator: AiQuestionGenerator,
    checkRunning: () -> Boolean,
    onProgress: (String, Float, String) -> Unit,
    onRefreshStats: suspend () -> Unit
) {
    withContext(Dispatchers.IO) {
        while (checkRunning()) {
            val currentCounts = QuestionRepository.getQuestionCounts()

            // Hedef dersler arasında en az soruya sahip olanı bul
            val minEntry = currentCounts.filter { entry ->
                val key = entry.key
                val isTarget = targetLessons.any { t ->
                    val mappedTarget = when (t) {
                        "İngilizce (A1)" -> "İngilizce (A1)"
                        "İngilizce (A2)" -> "İngilizce (A2)"
                        "İngilizce (B1)" -> "İngilizce (B1)"
                        else -> t
                    }
                    key == mappedTarget
                }
                isTarget && key != "Deneme Sınavı"
            }.minByOrNull { it.value } ?: break

            val targetLessonName = targetLessons.find {
                val mappedKey = when (it) {
                    "İngilizce (A1)" -> "İngilizce (A1)"
                    "İngilizce (A2)" -> "İngilizce (A2)"
                    "İngilizce (B1)" -> "İngilizce (B1)"
                    else -> it
                }
                mappedKey == minEntry.key
            } ?: targetLessons.first()

            val currentCount = minEntry.value

            withContext(Dispatchers.Main) {
                onProgress("Hedef: $targetLessonName ($currentCount)", 0.5f, "📉 En az: $targetLessonName ($currentCount). Eşitleniyor...")
            }

            try {
                // 15 adet soru üret (Sınav Sorusu)
                val questions = generator.generateBatch(targetLessonName, 15)

                if (questions.isNotEmpty()) {
                    // Buluta kaydet
                    val saved = QuestionRepository.saveQuestionsToFirestore(questions)
                    withContext(Dispatchers.Main) {
                        onProgress("$targetLessonName +$saved", 0.8f, "✅ $targetLessonName: $saved yeni benzersiz soru eklendi.")
                        onRefreshStats()
                    }
                } else {
                    withContext(Dispatchers.Main) { onProgress("$targetLessonName Pas", 0.5f, "⚠️ Soru üretilemedi, geçiliyor.") }
                    delay(1000)
                }
                delay(4000) // Yükleme arası bekleme
            } catch (e: Exception) {
                if (e.message == "Durduruldu") throw e
                withContext(Dispatchers.Main) { onProgress("Hata", 0f, "❌ Hata: ${e.message}") }
                delay(5000)
            }
        }
    }
}
