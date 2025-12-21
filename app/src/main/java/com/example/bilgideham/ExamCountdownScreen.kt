package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID

// --- VERİ MODELİ VE KAYIT İŞLEMLERİ ---
private const val EX_PREFS = "exam_countdown_prefs"
private const val EX_KEY = "exams_json"

data class ExamItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dateIso: String, // yyyy-MM-dd
    val colorHex: Long
)

// Seçilebilir Ders Seçeneği Modeli
data class ExamSubjectOption(
    val name: String,
    val color: Long,
    val icon: ImageVector
)

private fun loadExams(context: Context): List<ExamItem> {
    val sp = context.getSharedPreferences(EX_PREFS, Context.MODE_PRIVATE)
    val json = sp.getString(EX_KEY, null) ?: return emptyList()
    return runCatching {
        val type = object : TypeToken<List<ExamItem>>() {}.type
        Gson().fromJson<List<ExamItem>>(json, type) ?: emptyList()
    }.getOrElse { emptyList() }
}

private fun saveExams(context: Context, exams: List<ExamItem>) {
    val sp = context.getSharedPreferences(EX_PREFS, Context.MODE_PRIVATE)
    sp.edit().putString(EX_KEY, Gson().toJson(exams)).apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamCountdownScreen(navController: NavController) {
    val context = LocalContext.current
    val exams = remember { mutableStateListOf<ExamItem>() }
    var showAddDialog by remember { mutableStateOf(false) }

    // --- EN YAKIN SINAV STATE'LERİ ---
    var targetExamTitle by remember { mutableStateOf("HEDEF ARANIYOR...") }
    var targetExamColor by remember { mutableLongStateOf(0xFFFF6F00) } // Varsayılan Turuncu
    var timeLeft by remember { mutableStateOf(Duration.ZERO) }
    var hasActiveExam by remember { mutableStateOf(false) }

    // Verileri Yükle
    LaunchedEffect(Unit) {
        exams.clear()
        val loaded = loadExams(context)
        if (loaded.isNotEmpty()) {
            exams.addAll(loaded)
        } else {
            // Varsayılan Örnek Sınavlar
            exams.add(ExamItem(title = "Matematik", dateIso = LocalDate.now().plusDays(5).toString(), colorHex = 0xFF1976D2))
            exams.add(ExamItem(title = "Deneme Sınavı", dateIso = LocalDate.now().plusDays(12).toString(), colorHex = 0xFFFFD700))
            saveExams(context, exams)
        }
    }

    fun deleteExam(exam: ExamItem) {
        exams.remove(exam)
        saveExams(context, exams)
    }

    // Sayaç Animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    val animatedColor by animateColorAsState(targetValue = Color(targetExamColor), label = "color")

    // --- ZAMANLAYICI DÖNGÜSÜ & EN YAKIN SINAVI BULMA ---
    LaunchedEffect(exams.size, exams.toList()) {
        while (true) {
            val now = LocalDateTime.now()

            // Gelecekteki sınavları bul
            val upcomingExams = exams.map { exam ->
                val date = runCatching { LocalDate.parse(exam.dateIso) }.getOrElse { LocalDate.MAX }
                val dateTime = date.atTime(9, 0)
                exam to dateTime
            }.filter { it.second.isAfter(now) }
                .sortedBy { it.second }

            val nearest = upcomingExams.firstOrNull()

            if (nearest != null) {
                hasActiveExam = true
                targetExamTitle = nearest.first.title.uppercase()
                targetExamColor = nearest.first.colorHex
                timeLeft = Duration.between(now, nearest.second)
            } else {
                hasActiveExam = false
                targetExamTitle = "YAKLAŞAN SINAV YOK"
                targetExamColor = 0xFF9E9E9E // Gri
                timeLeft = Duration.ZERO
            }
            delay(1000)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sınav Sayacı ⏳", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFFE65100))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFF3E0))
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFFFF3E0)) // Krem Rengi Arka Plan
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. DİNAMİK BÜYÜK HEDEF KARTI ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(animatedColor, animatedColor.copy(alpha = 0.7f))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = targetExamTitle,
                            color = Color.White.copy(alpha = 0.95f),
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        if (hasActiveExam) {
                            Spacer(Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth().scale(scale)
                            ) {
                                TimeBox(timeLeft.toDays().toString(), "GÜN")
                                Spacer(Modifier.width(8.dp))
                                TimeColon()
                                Spacer(Modifier.width(8.dp))
                                TimeBox((timeLeft.toHours() % 24).toString(), "SAAT")
                                Spacer(Modifier.width(8.dp))
                                TimeColon()
                                Spacer(Modifier.width(8.dp))
                                TimeBox((timeLeft.toMinutes() % 60).toString(), "DK")
                            }

                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50)
                            ) {
                                Text(
                                    "🚀 Başarı seni bekliyor!",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Spacer(Modifier.height(16.dp))
                            Icon(Icons.Default.EventAvailable, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Yeni bir hedef ekle!", color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // --- 2. YAKLAŞAN SINAVLAR BAŞLIĞI ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EditCalendar, null, tint = Color(0xFF5D4037))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Sınav Listesi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                }
                TextButton(onClick = { showAddDialog = true }) {
                    Text("+ Ekle", fontWeight = FontWeight.Bold)
                }
            }

            // --- 3. SINAV LİSTESİ ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(exams.sortedBy { it.dateIso }) { exam ->
                    ExamCard(exam, onDelete = { deleteExam(exam) })
                }
            }
        }
    }

    // --- EKLEME DİYALOĞU ---
    if (showAddDialog) {
        AddExamDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, dateIso, color ->
                exams.add(ExamItem(title = title, dateIso = dateIso, colorHex = color))
                saveExams(context, exams)
                showAddDialog = false
            }
        )
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun TimeBox(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(60.dp),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = value.padStart(2, '0'),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF37474F)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = Color(0xFF5D4037), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimeColon() {
    Text(
        ":",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF5D4037).copy(alpha = 0.5f),
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
fun ExamCard(exam: ExamItem, onDelete: () -> Unit) {
    val date = runCatching { LocalDate.parse(exam.dateIso) }.getOrElse { LocalDate.MAX }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM EEEE")
    val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), date)
    val isPast = daysLeft < 0

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Renkli Şerit
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(exam.colorHex))
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(exam.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(date.format(formatter), fontSize = 12.sp, color = Color.Gray)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    color = when {
                        isPast -> Color(0xFFEEEEEE)
                        daysLeft <= 3 -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isPast) {
                            Text("Bitti", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)
                        } else {
                            Text("$daysLeft", fontWeight = FontWeight.Black, fontSize = 16.sp, color = if(daysLeft<=3) Color(0xFFD32F2F) else Color(0xFF1976D2))
                            Text("Gün", fontSize = 10.sp, color = if(daysLeft<=3) Color(0xFFD32F2F) else Color(0xFF1976D2))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp).clickable { onDelete() }
                )
            }
        }
    }
}

// --- GÜNCELLENMİŞ EKLEME DİYALOĞU ---
@Composable
private fun AddExamDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, dateIso: String, color: Long) -> Unit
) {
    // Tanımlı Dersler Listesi
    val subjects = listOf(
        ExamSubjectOption("Matematik", 0xFF1976D2, Icons.Rounded.Calculate),
        ExamSubjectOption("Türkçe", 0xFFD32F2F, Icons.Rounded.MenuBook),
        ExamSubjectOption("Fen Bilimleri", 0xFF388E3C, Icons.Rounded.Science),
        ExamSubjectOption("Sosyal Bilgiler", 0xFFF57C00, Icons.Rounded.Public),
        ExamSubjectOption("İngilizce", 0xFF7B1FA2, Icons.Rounded.Translate),
        ExamSubjectOption("Arapça", 0xFF00897B, Icons.Rounded.Translate),
        ExamSubjectOption("Din Kültürü", 0xFF00ACC1, Icons.Rounded.Mosque),
        ExamSubjectOption("Deneme Sınavı", 0xFFFFD700, Icons.Rounded.Assignment),
        ExamSubjectOption("Bursluluk", 0xFF6D4C41, Icons.Rounded.School)
    )

    var selectedSubject by remember { mutableStateOf<ExamSubjectOption?>(null) }
    var day by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("2025") }
    var error by remember { mutableStateOf<String?>(null) }

    fun digits(s: String) = s.filter { it.isDigit() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sınav Türü Seç") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 500.dp) // Uzun ekranlarda taşmayı önler
            ) {
                // 1. DERS SEÇİM IZGARASI
                Text("Hangi dersin sınavı?", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(240.dp) // Grid yüksekliği
                ) {
                    items(subjects) { subject ->
                        val isSelected = selectedSubject == subject
                        val bg = if (isSelected) Color(subject.color) else Color(subject.color).copy(alpha = 0.1f)
                        val contentColor = if (isSelected) Color.White else Color(subject.color)

                        Card(
                            onClick = { selectedSubject = subject; error = null },
                            colors = CardDefaults.cardColors(containerColor = bg),
                            border = if(isSelected) BorderStroke(2.dp, Color.Black.copy(alpha=0.1f)) else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .height(60.dp), // Kart yüksekliği
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(subject.icon, null, tint = contentColor, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    subject.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = contentColor,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 2. TARİH GİRİŞİ
                Text("Sınav Tarihi", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { day = digits(it).take(2); error = null },
                        label = { Text("Gün") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = digits(it).take(2); error = null },
                        label = { Text("Ay") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = digits(it).take(4); error = null },
                        label = { Text("Yıl") },
                        singleLine = true,
                        modifier = Modifier.weight(1.4f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val d = day.toIntOrNull()
                    val m = month.toIntOrNull()
                    val y = year.toIntOrNull()

                    if (selectedSubject == null) { error = "Lütfen bir ders seçin."; return@Button }
                    if (d == null || m == null || y == null) { error = "Tarih eksik."; return@Button }

                    val iso = runCatching {
                        val date = LocalDate.of(y, m, d)
                        date.toString()
                    }.getOrNull()

                    if (iso == null) { error = "Geçersiz tarih!"; return@Button }

                    onAdd(selectedSubject!!.name, iso, selectedSubject!!.color)
                }
            ) { Text("Kaydet") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}