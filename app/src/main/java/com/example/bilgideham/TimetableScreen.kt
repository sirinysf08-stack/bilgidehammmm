package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

// --- VERİ YAPILARI VE KAYIT ---
private const val TT_PREFS = "timetable_prefs"
private const val TT_KEY = "timetable_json"
private const val NOTE_KEY = "daily_notes_json"

// Ders Hücresi
private data class TimeCell(
    val dayIndex: Int,   // 0..4
    val slotIndex: Int,  // 0..8 (9 Ders)
    val lesson: String
)

// Derslere göre renk ve ikon tanımları
fun getLessonAttr(name: String): Pair<Color, ImageVector> {
    return when (name.lowercase()) {
        // Temel Dersler
        "matematik" -> Pair(Color(0xFF1976D2), Icons.Default.Calculate) // Koyu Mavi
        "türkçe" -> Pair(Color(0xFFD32F2F), Icons.Default.MenuBook) // Koyu Kırmızı
        "fen bilimleri", "fen" -> Pair(Color(0xFF388E3C), Icons.Default.Science) // Koyu Yeşil
        "sosyal bilgiler", "sosyal", "inkılap" -> Pair(Color(0xFFF57C00), Icons.Default.Public) // Turuncu
        "ingilizce" -> Pair(Color(0xFF7B1FA2), Icons.Default.Translate) // Mor
        "din kültürü", "din" -> Pair(Color(0xFF0097A7), Icons.Default.Mosque) // Turkuaz
        "beden eğitimi", "beden" -> Pair(Color(0xFF455A64), Icons.Default.SportsBasketball) // Gri Mavi
        "görsel sanatlar", "resim" -> Pair(Color(0xFFC2185B), Icons.Default.Palette) // Koyu Pembe
        "müzik" -> Pair(Color(0xFFFFA000), Icons.Default.MusicNote) // Amber
        "bilişim" -> Pair(Color(0xFF303F9F), Icons.Default.Computer) // İndigo
        "rehberlik" -> Pair(Color(0xFF5D4037), Icons.Default.SupervisorAccount) // Kahve

        // Yeni Eklenen Dini Dersler
        "arapça" -> Pair(Color(0xFF00695C), Icons.Default.Translate) // Koyu Yeşil-Mavi
        "peygamberimizin hayatı", "siyer" -> Pair(Color(0xFF558B2F), Icons.Default.AutoStories) // Zeytin Yeşili
        "kuranı kerim", "kuran" -> Pair(Color(0xFFFBC02D), Icons.Default.MenuBook) // Altın Sarısı

        // Yeni Eklenen İngilizce Bölümleri
        "main course" -> Pair(Color(0xFF283593), Icons.Default.School) // Koyu Lacivert
        "reading" -> Pair(Color(0xFFAD1457), Icons.Default.ChromeReaderMode) // Koyu Pembe
        "listening" -> Pair(Color(0xFF4527A0), Icons.Default.Headphones) // Derin Mor
        "speaking" -> Pair(Color(0xFFEF6C00), Icons.Default.RecordVoiceOver) // Koyu Turuncu

        else -> Pair(Color(0xFF757575), Icons.Default.Event) // Varsayılan Gri
    }
}

// --- KAYIT FONKSİYONLARI ---
private fun loadTable(context: Context): MutableList<TimeCell> {
    val sp = context.getSharedPreferences(TT_PREFS, Context.MODE_PRIVATE)
    val json = sp.getString(TT_KEY, null) ?: return mutableListOf()
    return runCatching {
        val type = object : TypeToken<List<TimeCell>>() {}.type
        Gson().fromJson<List<TimeCell>>(json, type).toMutableList()
    }.getOrElse { mutableListOf() }
}

private fun saveTable(context: Context, cells: List<TimeCell>) {
    val sp = context.getSharedPreferences(TT_PREFS, Context.MODE_PRIVATE)
    sp.edit().putString(TT_KEY, Gson().toJson(cells)).apply()
}

private fun loadNotes(context: Context): MutableMap<Int, String> {
    val sp = context.getSharedPreferences(TT_PREFS, Context.MODE_PRIVATE)
    val json = sp.getString(NOTE_KEY, null) ?: return mutableMapOf()
    return runCatching {
        val type = object : TypeToken<Map<Int, String>>() {}.type
        Gson().fromJson<Map<Int, String>>(json, type).toMutableMap()
    }.getOrElse { mutableMapOf() }
}

private fun saveNotes(context: Context, notes: Map<Int, String>) {
    val sp = context.getSharedPreferences(TT_PREFS, Context.MODE_PRIVATE)
    sp.edit().putString(NOTE_KEY, Gson().toJson(notes)).apply()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(navController: NavController) {
    val context = LocalContext.current
    val days = listOf("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma")

    // State
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val cells = remember { mutableStateListOf<TimeCell>() }
    val notes = remember { mutableStateMapOf<Int, String>() }
    var editSlotIndex by remember { mutableStateOf<Int?>(null) }

    // Tema Renkleri
    val cs = MaterialTheme.colorScheme

    // Verileri Yükle
    LaunchedEffect(Unit) {
        cells.clear()
        cells.addAll(loadTable(context))
        notes.clear()
        notes.putAll(loadNotes(context))
    }

    // İşlemler
    fun setLesson(day: Int, slot: Int, lessonName: String) {
        val idx = cells.indexOfFirst { it.dayIndex == day && it.slotIndex == slot }
        if (lessonName.isBlank()) {
            if (idx >= 0) cells.removeAt(idx)
        } else {
            if (idx >= 0) cells[idx] = cells[idx].copy(lesson = lessonName.trim())
            else cells.add(TimeCell(day, slot, lessonName.trim()))
        }
        saveTable(context, cells.toList())
    }

    fun saveDayNote(text: String) {
        notes[selectedDayIndex] = text
        saveNotes(context, notes.toMap())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color(0xFFF5F5F7),
        topBar = {
            // --- MODERN BAŞLIK ALANI ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(cs.primary, cs.tertiary)
                        )
                    )
            ) {
                // Yıldız Tozu Efekti
                TimetableStarDustEffect(color = Color.White.copy(alpha = 0.2f))

                // Dekoratif Arka Plan İkonu
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .offset(x = 20.dp, y = 10.dp)
                        .rotate(-15f)
                ) {
                    Icon(
                        Icons.Rounded.DateRange,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.size(140.dp)
                    )
                }

                // İçerik
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Geri Butonu
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Başlık Yazıları
                    Text(
                        "Ders Programım",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        "${days[selectedDayIndex]} Günü",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
        ) {
            // --- 1. GÜN SEÇİCİ ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(bottom = 12.dp, top = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(days) { index, dayName ->
                    val isSelected = index == selectedDayIndex
                    val animColor by animateColorAsState(if (isSelected) cs.primary else Color.White, label = "color")
                    val contentColor = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f)
                    val borderColor = if (isSelected) Color.Transparent else Color.LightGray.copy(alpha = 0.4f)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(animColor)
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { selectedDayIndex = index }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName.take(3),
                            color = contentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // --- 2. DERS LİSTESİ ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(9) { slotIndex ->
                    val currentLesson = cells.find { it.dayIndex == selectedDayIndex && it.slotIndex == slotIndex }?.lesson ?: ""

                    ModernLessonCard(
                        slotIndex = slotIndex,
                        lessonName = currentLesson,
                        onClick = { editSlotIndex = slotIndex }
                    )
                }
            }

            // --- 3. ALT PANEL (NOTLAR) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .imePadding(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(cs.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Edit, null, tint = cs.onSecondaryContainer, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Günün Notu & Ödevler",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A4A4A),
                            fontSize = 16.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = notes[selectedDayIndex] ?: "",
                        onValueChange = { saveDayNote(it) },
                        placeholder = { Text("Yarın için çanta hazırla...", fontSize = 14.sp, color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cs.primary,
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedContainerColor = Color(0xFFFAFAFA),
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )
                }
            }
        }
    }

    // --- DÜZENLEME POP-UP ---
    if (editSlotIndex != null) {
        val commonLessons = listOf(
            // Temel Dersler
            "Matematik", "Türkçe", "Fen Bilimleri", "Sosyal Bilgiler",
            "İngilizce", "Din Kültürü", "Beden Eğitimi", "Görsel Sanatlar",
            "Müzik", "Bilişim", "Rehberlik",
            // Eklenen Dersler
            "Arapça", "Peygamberimizin Hayatı", "Kuranı Kerim",
            "Main Course", "Reading", "Listening", "Speaking",
            // Diğer
            "Boş"
        )

        AlertDialog(
            onDismissRequest = { editSlotIndex = null },
            containerColor = Color.White,
            title = {
                Text(
                    "${editSlotIndex!! + 1}. Ders",
                    fontWeight = FontWeight.Bold,
                    color = cs.primary
                )
            },
            text = {
                Column(modifier = Modifier.heightIn(max = 500.dp)) {
                    Text("Listeden seç veya kendin yaz:", fontSize = 13.sp, color = Color.Gray)
                    Spacer(Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false) // Pop-up'ın ekranı taşmasını önler
                    ) {
                        items(commonLessons) { lesson ->
                            val (color, icon) = getLessonAttr(lesson)
                            val isClear = lesson == "Boş"

                            Card(
                                onClick = {
                                    setLesson(selectedDayIndex, editSlotIndex!!, if (isClear) "" else lesson)
                                    editSlotIndex = null
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isClear) Color(0xFFFFEBEE) else color.copy(alpha = 0.1f)
                                ),
                                border = if(isClear) BorderStroke(1.dp, Color.Red.copy(alpha=0.2f)) else null
                            ) {
                                Row(
                                    Modifier
                                        .padding(vertical = 10.dp, horizontal = 8.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    if(!isClear) {
                                        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                    }
                                    Text(
                                        if(isClear) "Temizle" else lesson,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if(isClear) Color.Red.copy(alpha=0.7f) else color,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(16.dp))

                    var customText by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = customText,
                        onValueChange = { customText = it },
                        placeholder = { Text("Örn: Robotik Kodlama") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                setLesson(selectedDayIndex, editSlotIndex!!, customText)
                                editSlotIndex = null
                            }) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                            }
                        }
                    )
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun ModernLessonCard(slotIndex: Int, lessonName: String, onClick: () -> Unit) {
    val isEmpty = lessonName.isBlank()
    val (color, icon) = getLessonAttr(lessonName)

    if (isEmpty) {
        // --- BOŞ DURUM ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable { onClick() }
                .drawBehind {
                    val stroke = Stroke(
                        width = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                    )
                    drawRoundRect(
                        color = Color.Gray.copy(alpha = 0.3f),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = stroke
                    )
                }
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${slotIndex + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
                Spacer(Modifier.width(12.dp))
                Icon(Icons.Rounded.Add, null, tint = Color.Gray.copy(alpha = 0.5f))
                Spacer(Modifier.width(4.dp))
                Text("Ders Ekle", color = Color.Gray.copy(alpha = 0.5f), fontWeight = FontWeight.Medium)
            }
        }
    } else {
        // --- DOLU DURUM ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Renkli Sol Şerit
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .background(color)
                )

                Spacer(Modifier.width(12.dp))

                // İkon Kutusu
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
                }

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${slotIndex + 1}. Ders",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = lessonName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D2D2D)
                    )
                }

                Icon(
                    Icons.Rounded.Edit,
                    null,
                    tint = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(end = 16.dp).size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TimetableStarDustEffect(color: Color = Color.White) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars_movement")

    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -50f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "moveY"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse), label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val r = Random(42) // Sabit seed
        repeat(20) {
            val startX = r.nextFloat() * size.width
            val startY = r.nextFloat() * size.height
            val radius = r.nextFloat() * 2.5.dp.toPx() + 1.dp.toPx()
            val speedFactor = (it % 3) + 1
            val currentY = (startY + moveY * speedFactor) % size.height
            val drawY = if (currentY < 0) size.height + currentY else currentY

            drawCircle(
                color = color,
                radius = radius,
                center = Offset(startX, drawY),
                alpha = alpha * r.nextFloat()
            )
        }
    }
}