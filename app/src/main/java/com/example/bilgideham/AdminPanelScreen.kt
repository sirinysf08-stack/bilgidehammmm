package com.example.bilgideham

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ADMIN_PANEL"

// AI Modu seçimi için enum
enum class AiMode {
    GEMINI,  // Tek Gemini (Firebase VertexAI)
    KARMA    // 3 Gemini paralel - 3x hız!
}

@Composable
fun AdminPanelScreen(navController: androidx.navigation.NavController, onBack: () -> Unit) {
    var isAuthenticated by remember { mutableStateOf(false) }
    if (isAuthenticated) {
        AdminDashboard(navController, onBack)
    } else {
        AdminLoginScreen(onLoginSuccess = { isAuthenticated = true }, onBack = onBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)), // Slate-900
        contentAlignment = Alignment.Center
    ) {
        // Geri Butonu (Sol Üst)
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
        }
        
        // Login Kartı
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo / İkon
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEFF6FF), // Blue-50
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Shield,
                            null,
                            tint = Color(0xFF2563EB), // Blue-600
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Başlıklar
                Text(
                    "Yönetici Girişi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B) // Slate-800
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Bilgi Deham Admin Paneli",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B) // Slate-500
                )
                
                Spacer(Modifier.height(32.dp))
                
                // Şifre Alanı
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Erişim Şifresi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            isError = false 
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else StarVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF1E293B), // Slate-800
                            unfocusedTextColor = Color(0xFF1E293B),
                            focusedBorderColor = Color(0xFF2563EB),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color(0xFFF8FAFC),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    null,
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        isError = isError
                    )
                    if (isError) {
                        Text(
                            "Hatalı erişim şifresi", 
                            color = Color(0xFFEF4444), 
                            fontSize = 12.sp, 
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                
                // Giriş Butonu
                Button(
                    onClick = { 
                        if (password == "787878") onLoginSuccess() else isError = true 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB) // Blue-600
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 1.dp
                    )
                ) {
                    Icon(Icons.Default.Lock, null, modifier = Modifier.size(20.dp), tint = Color.White)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "GÜVENLİ GİRİŞ", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ==================== ANA PANEL ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: androidx.navigation.NavController, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // İşlem durumları
    var isRunning by remember { mutableStateOf(false) }
    var isTekliMode by remember { mutableStateOf(false) }
    var isBackgroundRunning by remember { mutableStateOf(false) }
    var currentTask by remember { mutableStateOf("") } // Hedef Ders
    
    // Detaylı loglar
    val logList = remember { mutableStateListOf<LogEntry>() }

    // Log ekleme fonksiyonu - Yeni loglar en başa eklenir (0. index)
    fun addLog(message: String, type: LogType = LogType.INFO) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(java.util.Date())
        logList.add(0, LogEntry(time, message, type))
        if (logList.size > 200) {
            logList.removeAt(logList.lastIndex)
        }
    }

    // İstatistikler
    var totalQuestionsAddedSession by remember { mutableIntStateOf(0) } // Bu oturumda
    var totalSystemQuestions by remember { mutableIntStateOf(0) }     // Veritabanı Toplamı
    var systemStats by remember { mutableStateOf<QuestionRepository.SystemStats?>(QuestionRepository.getEmptySystemStatistics()) }
    var isLoadingStats by remember { mutableStateOf(false) }
    var lastUpdateTime by remember { mutableStateOf("") }
    
    // AGS Tarih state'leri
    var agsTarihQuestionCount by remember { mutableIntStateOf(0) }
    var agsTarihUniteCounts by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var isAgsTarihDeleting by remember { mutableStateOf(false) }

    var agsMebLessonCounts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var agsOabtUnitCountsByField by remember { mutableStateOf<Map<String, List<Pair<String, Int>>>>(emptyMap()) }

     var agsOabtSelectedField by rememberSaveable { mutableStateOf("kimya") }
     var agsOabtSelectedUnitIndex by rememberSaveable { mutableIntStateOf(1) }
     var agsOabtSelectedUnitQuestionCount by remember { mutableIntStateOf(0) }
    
    // Koyu Tema Kontrolü
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    // Gradient Background
    val bgBrush = if (isDark) {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                Color(0xFF121212), // Dark Background
                Color(0xFF263238)  // Dark Blue-Grey
            )
        )
    } else {
        androidx.compose.ui.graphics.Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE3F2FD), // Light Blue
                Color(0xFFF3E5F5)  // Light Purple/Pinkish
            )
        )
    }
    
    // Chart Question Screen navigation state
    var showChartScreen by remember { mutableStateOf(false) }

     var secretDeleteUnlocked by rememberSaveable { mutableStateOf(false) }
     var secretDeleteTapCount by rememberSaveable { mutableIntStateOf(0) }
    
    // Eğer ChartScreen gösterilecekse, direkt o ekranı render et
    if (showChartScreen) {
        ChartQuestionScreen(onBack = { showChartScreen = false })
        return
    }

    // Stats Refresh Function
    val refreshStats = {
        scope.launch {
            isLoadingStats = true
            try {
                // Burada parallel istek atılacak
                val stats = QuestionRepository.getAllSystemStatistics()
                systemStats = stats
                totalSystemQuestions = stats.totalQuestions
                lastUpdateTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                
                // AGS Tarih soru sayısını da çek
                agsTarihQuestionCount = QuestionRepository.getAgsTarihQuestionCount()
                agsTarihUniteCounts = QuestionRepository.getAgsTarihUniteCounts()

                agsMebLessonCounts = QuestionRepository.getAgsMebLessonCounts()
                agsOabtUnitCountsByField = QuestionRepository.getAgsOabtUnitCountsByField()
            } catch (e: Exception) {
                addLog("İstatistik hatası: ${e.message}", LogType.ERROR)
            } finally {
                isLoadingStats = false
            }
        }
    }

    // İlk yüklemede çalıştır
    LaunchedEffect(Unit) {
        // İlk açılışta zaten boş stats var, hemen gerçeğini çekelim
        refreshStats()
    }

     LaunchedEffect(agsOabtSelectedField, agsOabtSelectedUnitIndex) {
         val subjects = runCatching { AppPrefs.getAgsOabtUnitSubjects(agsOabtSelectedField) }.getOrDefault(emptyList())
         val subject = subjects.getOrNull(agsOabtSelectedUnitIndex - 1)
         if (subject == null) {
             agsOabtSelectedUnitQuestionCount = 0
             return@LaunchedEffect
         }

         agsOabtSelectedUnitQuestionCount = runCatching {
             val col = Firebase.firestore
                 .collection("question_pools")
                 .document("AGS")
                 .collection("AGS_OABT")
                 .document("general")
                 .collection(subject.id)
             col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
         }.getOrDefault(0)
     }
    
    // İşlem bittiğinde istatistikleri güncelle (Polling yerine trigger ile)
    LaunchedEffect(isRunning) {
        if (!isRunning) {
             refreshStats()
        }
    }



    // ANA LAYOUT
    // Ana Arka Plan Rengi (Düz ve Temiz)
    val mainBgColor = if (isDark) Color(0xFF121212) else Color(0xFFF5F7FA)

    // Box içinde Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // MODERN HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = if(isDark) 
                                listOf(Color(0xFF0D47A1), Color(0xFF1976D2)) 
                            else 
                                listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                        )
                    )
            ) {
                // Background Pattern (Opsiyonel: Hafif daireler vs eklenebilir ama sadelik iyidir)
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 24.dp, start = 24.dp, end = 24.dp) // Top padding status bar için
                ) {
                    
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Geri Butonu (Modern Glassmorphism)
                            Surface(
                                onClick = onBack,
                                enabled = !isRunning,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f), // Semi-transparent white
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        null,
                                        tint = Color.White // İkon Beyaz
                                    )
                                }
                            }
                            
                            Spacer(Modifier.width(16.dp))
                            
                            Column {
                                    Text(
                                        "Yönetim Masası",
                                        modifier = Modifier.clickable {
                                            if (!secretDeleteUnlocked) {
                                                secretDeleteTapCount++
                                                if (secretDeleteTapCount >= 5) {
                                                    secretDeleteUnlocked = true
                                                    secretDeleteTapCount = 0
                                                    Toast.makeText(context, "Gizli silme paneli açıldı", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        },
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White // Beyaz
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Storage, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Sistemde Toplam: $totalSystemQuestions Soru",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White.copy(alpha = 0.9f)
                                        )
                                    }
                            }
                        }
                        
                        // Session Badge (Oturumda Eklenen)
                        if (totalQuestionsAddedSession > 0) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White, // Beyaz zemin
                                shadowElevation = 4.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text("Yeni", fontSize = 10.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold)
                                    Text(
                                        "+$totalQuestionsAddedSession",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                            }
                        }
                        
                        // Refresh Button
                        IconButton(onClick = { refreshStats() }) {
                             Icon(Icons.Default.Refresh, contentDescription = "Yenile", tint = Color.White)   
                        }
                    }
                }
            }

            // CONTENT BODY
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. ÜST KOLON: İSTATİSTİKLER (Scrollable List)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp), // Mobilde yer kaplamasın diye sabit yükseklik + iç scroll
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E1E1E) else Color(0xFFF8F9FA)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Sistem Durumu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if(isDark) Color.White else Color(0xFF1E293B)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        if (isLoadingStats) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        
                        // Always show the list, even if loading (show old data or empty)
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // If stats are null but loading, we could show skeletons, but for now just show nothing until first load
                            // OR better: initialize systemStats with empty values on ViewModel init so it's never null?
                            // For this quick fix, just show distinct items if they exist.
                            
                            systemStats?.detailedStats?.forEach { (level, schoolStats) ->
                                    item {
                                        Text(
                                            level.displayName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(level.colorHex),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    }
                                    
                                    items(schoolStats) { schoolStat ->
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if(isDark) Color(0xFF2C2C2C) else Color(0xFFF8FAFC)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        schoolStat.type.displayName, 
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if(isDark) Color.White else Color(0xFF1E293B)
                                                    )
                                                    Text(
                                                        "${schoolStat.totalQuestions} Soru", 
                                                        fontWeight = FontWeight.Bold, 
                                                        color = Color(0xFF2563EB)
                                                    )
                                                }
                                                
                                                schoolStat.classStats.forEach { classStat ->
                                                    Spacer(Modifier.height(4.dp))
                                                    Text(
                                                        if(classStat.grade == 0) "Genel" else "${classStat.grade}. Sınıf",
                                                        fontSize = 13.sp,
                                                        color = if(isDark) Color.Gray else Color(0xFF64748B), // Slate 500 for secondary
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    classStat.lessonCounts.forEach { (lesson, count) ->
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                "- $lesson", 
                                                                fontSize = 12.sp,
                                                                color = if(isDark) Color(0xFFE0E0E0) else Color(0xFF334155) // Slate 700
                                                            )
                                                            Text(
                                                                "$count", 
                                                                fontSize = 12.sp, 
                                                                fontWeight = FontWeight.Bold,
                                                                color = if(isDark) Color.White else Color.Black
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            
                            // AGS TARİH ÜNİTELERİ
                            if (agsTarihUniteCounts.isNotEmpty()) {
                                item {
                                    Text(
                                        "🏛️ AGS Tarih Üniteleri",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF795548),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                                
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if(isDark) Color(0xFF2C2C2C) else Color(0xFFF8FAFC)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    "Tarih Öğretmenliği (ÖABT)", 
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if(isDark) Color.White else Color(0xFF1E293B)
                                                )
                                                Text(
                                                    "$agsTarihQuestionCount Soru", 
                                                    fontWeight = FontWeight.Bold, 
                                                    color = Color(0xFF795548)
                                                )
                                            }
                                            
                                            Spacer(Modifier.height(8.dp))
                                            
                                            val uniteNames = listOf(
                                                "Tarih Bilimi", "Osmanlı Türkçesi", "Uygarlığın Doğuşu",
                                                "İlk Türk Devletleri", "İslam Tarihi", "Türk İslam Devletleri",
                                                "Türk Dünyası", "Osmanlı Tarihi", "En Uzun Yüzyıl",
                                                "XX. Yüzyıl Başları", "Milli Mücadele", "Atatürk Dönemi",
                                                "Dünya Tarihi", "Çağdaş Tarih"
                                            )
                                            
                                            uniteNames.forEachIndexed { index, uniteName ->
                                                val uniteId = index + 1
                                                val count = agsTarihUniteCounts[uniteId] ?: 0
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        "$uniteId. $uniteName", 
                                                        fontSize = 12.sp,
                                                        color = if(isDark) Color(0xFFE0E0E0) else Color(0xFF334155)
                                                    )
                                                    Text(
                                                        "$count", 
                                                        fontSize = 12.sp, 
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (count == 0) Color(0xFFEF5350) else if(isDark) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (agsMebLessonCounts.isNotEmpty()) {
                                item {
                                    Text(
                                        "📌 MEB AGS Dersleri",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1565C0),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if(isDark) Color(0xFF2C2C2C) else Color(0xFFF8FAFC)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            val total = agsMebLessonCounts.values.sum()
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    "1. Oturum (MEB AGS)",
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if(isDark) Color.White else Color(0xFF1E293B)
                                                )
                                                Text(
                                                    "$total Soru",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1565C0)
                                                )
                                            }

                                            Spacer(Modifier.height(8.dp))

                                            agsMebLessonCounts.forEach { (lesson, count) ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 2.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        "- $lesson",
                                                        fontSize = 12.sp,
                                                        color = if(isDark) Color(0xFFE0E0E0) else Color(0xFF334155)
                                                    )
                                                    Text(
                                                        "$count",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (count == 0) Color(0xFFEF5350) else if(isDark) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (agsOabtUnitCountsByField.isNotEmpty()) {
                                item {
                                    Text(
                                        "🧩 AGS ÖABT Ünite Dersleri",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7B1FA2),
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }

                                val fieldTitles = mapOf(
                                    "turkce" to "Türkçe",
                                    "ilkmat" to "İlköğretim Matematik",
                                    "fen" to "Fen Bilimleri",
                                    "sosyal" to "Sosyal Bilgiler",
                                    "edebiyat" to "Türk Dili ve Edebiyatı",
                                    "cografya" to "Coğrafya",
                                    "matematik" to "Matematik",
                                    "fizik" to "Fizik",
                                    "kimya" to "Kimya",
                                    "biyoloji" to "Biyoloji",
                                    "rehberlik" to "Rehberlik",
                                    "sinif" to "Sınıf Öğretmenliği",
                                    "okoncesi" to "Okul Öncesi",
                                    "beden" to "Beden Eğitimi",
                                    "din" to "Din Kültürü"
                                )

                                val orderedKeys = listOf(
                                    "turkce",
                                    "ilkmat",
                                    "fen",
                                    "sosyal",
                                    "edebiyat",
                                    "cografya",
                                    "matematik",
                                    "fizik",
                                    "kimya",
                                    "biyoloji",
                                    "rehberlik",
                                    "sinif",
                                    "okoncesi",
                                    "beden",
                                    "din"
                                )

                                orderedKeys.forEach { field ->
                                    val units = agsOabtUnitCountsByField[field].orEmpty()
                                    if (units.isEmpty()) return@forEach

                                    item {
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if(isDark) Color(0xFF2C2C2C) else Color(0xFFF8FAFC)
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                val title = fieldTitles[field] ?: field
                                                val total = units.sumOf { it.second }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        title,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if(isDark) Color.White else Color(0xFF1E293B)
                                                    )
                                                    Text(
                                                        "$total Soru",
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF7B1FA2)
                                                    )
                                                }

                                                Spacer(Modifier.height(8.dp))

                                                units.forEachIndexed { idx, pair ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 2.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            "${idx + 1}. ${pair.first}",
                                                            fontSize = 12.sp,
                                                            color = if(isDark) Color(0xFFE0E0E0) else Color(0xFF334155)
                                                        )
                                                        Text(
                                                            "${pair.second}",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (pair.second == 0) Color(0xFFEF5350) else if(isDark) Color.White else Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            }
                        }
                    }
                // End of Stats Card

                // 2. ALT KOLON: AKSİYONLAR VE LOGLAR
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 📊 GRAFİKLİ SORU ÜRETİCİ KART
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showChartScreen = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF2C2C2C) else Color(0xFFFFF8E1)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color(0xFFFFE0B2),
                                    shape = CircleShape,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("📊", fontSize = 24.sp)
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Grafikli Soru Üretici",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFFE65100)
                                    )
                                    Text(
                                        "Vega-Lite ile Bar, Line, Pie grafikleri",
                                        fontSize = 12.sp,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }
                            Icon(
                                Icons.Default.ChevronRight,
                                null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    // 📋 KPSS DENEME ÜRETİCİ KART
                    var kpssDenemePaketNo by remember { mutableIntStateOf(1) }
                    var kpssDenemProgress by remember { mutableIntStateOf(0) }
                    var kpssDenemTotal by remember { mutableIntStateOf(120) }
                    var kpssDenemStatus by remember { mutableStateOf("Hazır") }
                    var selectedKpssSeviye by remember { mutableStateOf(SchoolType.KPSS_LISANS) }
                    val kpssSeviyeler = listOf(SchoolType.KPSS_ORTAOGRETIM, SchoolType.KPSS_ONLISANS, SchoolType.KPSS_LISANS)
                    var isKpssDenemRunning by remember { mutableStateOf(false) }
                    
                    // 🌍 GLOBAL EŞİTLEME KARTI
                    var isGlobalSyncRunning by remember { mutableStateOf(false) }
                    var globalSyncProgress by remember { mutableIntStateOf(0) }
                    var globalSyncTotal by remember { mutableIntStateOf(0) }
                    var globalSyncStatus by remember { mutableStateOf("Hazır") }
                    var selectedGlobalLevel by remember { mutableStateOf<EducationLevel?>(null) }
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1B5E20) else Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFC8E6C9),
                                        shape = CircleShape,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("📋", fontSize = 24.sp)
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "KPSS Deneme Üretici",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text(
                                            "120 Soru (Türkçe, Mat, Tarih, Coğ, Vat, Gün)",
                                            fontSize = 12.sp,
                                            color = Color(0xFF757575)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            // Paket No Seçici
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Deneme Paket No:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B5E20)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (kpssDenemePaketNo > 1) kpssDenemePaketNo-- },
                                        enabled = !isKpssDenemRunning
                                    ) {
                                        Icon(Icons.Default.Remove, null, tint = Color(0xFF2E7D32))
                                    }
                                    Text(
                                        "$kpssDenemePaketNo. Deneme",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                    IconButton(
                                        onClick = { kpssDenemePaketNo++ },
                                        enabled = !isKpssDenemRunning
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = Color(0xFF2E7D32))
                                    }
                                }
                            }
                            
                            // Seviye Seçici
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "KPSS Seviye:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1B5E20)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    kpssSeviyeler.forEach { seviye ->
                                        FilterChip(
                                            selected = seviye == selectedKpssSeviye,
                                            onClick = { selectedKpssSeviye = seviye },
                                            label = { 
                                                Text(
                                                    when (seviye) {
                                                        SchoolType.KPSS_ORTAOGRETIM -> "Lise"
                                                        SchoolType.KPSS_ONLISANS -> "Önlisans"
                                                        SchoolType.KPSS_LISANS -> "Lisans"
                                                        else -> seviye.displayName
                                                    },
                                                    fontSize = 11.sp
                                                ) 
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFFA5D6A7),
                                                selectedLabelColor = Color(0xFF1B5E20)
                                            ),
                                            enabled = !isKpssDenemRunning
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            // Progress Bar
                            if (isKpssDenemRunning) {
                                Column {
                                    LinearProgressIndicator(
                                        progress = { kpssDenemProgress.toFloat() / kpssDenemTotal.toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF4CAF50),
                                        trackColor = Color(0xFFC8E6C9)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "$kpssDenemStatus ($kpssDenemProgress/$kpssDenemTotal)",
                                        fontSize = 12.sp,
                                        color = Color(0xFF388E3C)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            // Butonlar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isKpssDenemRunning) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                isKpssDenemRunning = true
                                                kpssDenemProgress = 0
                                                kpssDenemStatus = "Başlatılıyor..."
                                                addLog("🚀 KPSS Deneme #$kpssDenemePaketNo üretimi başlıyor...", LogType.INFO)
                                                
                                                // API key'leri yükle
                                                GeminiApiProvider.loadKeysFromAssets(context)
                                                
                                                // Progress callback
                                                KpssDenemGenerator.onProgressUpdate = { current, total, status ->
                                                    kpssDenemProgress = current
                                                    kpssDenemTotal = total
                                                    kpssDenemStatus = status
                                                }
                                                
                                                // Log callback
                                                KpssDenemGenerator.onLogMessage = { message ->
                                                    scope.launch(Dispatchers.Main) {
                                                        addLog(message, if (message.contains("✅")) LogType.SUCCESS 
                                                            else if (message.contains("❌") || message.contains("⚠️")) LogType.WARNING 
                                                            else LogType.INFO)
                                                    }
                                                }
                                                
                                                try {
                                                    val questions = withContext(Dispatchers.IO) {
                                                        KpssDenemGenerator.generateDenemePaketi(
                                                            paketNo = kpssDenemePaketNo,
                                                            seviye = selectedKpssSeviye
                                                        )
                                                    }
                                                    
                                                    if (questions.isNotEmpty()) {
                                                        // Firestore'a kaydet
                                                        val saved = withContext(Dispatchers.IO) {
                                                            QuestionRepository.saveKpssDenemePaketi(
                                                                paketNo = kpssDenemePaketNo,
                                                                questions = questions,
                                                                seviye = selectedKpssSeviye
                                                            )
                                                        }
                                                        
                                                        addLog("🎉 KPSS Deneme #$kpssDenemePaketNo tamamlandı: $saved soru kaydedildi", LogType.SUCCESS)
                                                        totalQuestionsAddedSession += saved
                                                        kpssDenemePaketNo++ // Sonraki deneme için artır
                                                    } else {
                                                        addLog("❌ KPSS Deneme üretilemedi", LogType.ERROR)
                                                    }
                                                } catch (e: Exception) {
                                                    addLog("❌ Hata: ${e.message?.take(50)}", LogType.ERROR)
                                                } finally {
                                                    isKpssDenemRunning = false
                                                    kpssDenemStatus = "Tamamlandı"
                                                    refreshStats()
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                    ) {
                                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Deneme Üret")
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            // Durdurmak için flag'i false yap
                                            isKpssDenemRunning = false
                                            addLog("⛔ KPSS Deneme durduruluyor...", LogType.WARNING)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                                    ) {
                                        Icon(Icons.Default.Stop, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Durdur")
                                    }
                                }
                            }
                        }
                    }
                    
                    // 🌍 GLOBAL SORU EŞİTLEME KARTI
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if(isDark) Color(0xFF1A237E) else Color(0xFFE8EAF6)
                        ),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = Color(0xFFC5CAE9),
                                        shape = CircleShape,
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🌍", fontSize = 24.sp)
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Global Soru Eşitleme",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF283593)
                                        )
                                        Text(
                                            "Tüm Seviyeleri 4x Paralel Eşitle",
                                            fontSize = 12.sp,
                                            color = Color(0xFF757575)
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(12.dp))
                            
                            // Seviye Seçici
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Seviye Filtresi:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF283593)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    val levels = listOf(null) + EducationLevel.entries
                                    levels.forEach { level ->
                                        FilterChip(
                                            selected = level == selectedGlobalLevel,
                                            onClick = { selectedGlobalLevel = level },
                                            label = { 
                                                Text(
                                                    level?.displayName ?: "Tümü",
                                                    fontSize = 11.sp
                                                ) 
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF9FA8DA),
                                                selectedLabelColor = Color(0xFF1A237E)
                                            ),
                                            enabled = !isGlobalSyncRunning
                                        )
                                    }
                                }
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            // Progress Bar
                            if (isGlobalSyncRunning) {
                                Column {
                                    LinearProgressIndicator(
                                        progress = { 
                                            if (globalSyncTotal > 0) globalSyncProgress.toFloat() / globalSyncTotal.toFloat() 
                                            else 0f 
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = Color(0xFF5C6BC0),
                                        trackColor = Color(0xFFC5CAE9)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "$globalSyncStatus ($globalSyncProgress/$globalSyncTotal)",
                                        fontSize = 12.sp,
                                        color = Color(0xFF3949AB)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            // Butonlar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (!isGlobalSyncRunning) {
                                    // Normal Mod (UI'da çalışır)
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                isGlobalSyncRunning = true
                                                globalSyncProgress = 0
                                                globalSyncTotal = 0
                                                globalSyncStatus = "Başlatılıyor..."
                                                
                                                val targetLevels = selectedGlobalLevel?.let { listOf(it) } 
                                                    ?: EducationLevel.entries.toList()
                                                
                                                addLog("🌍 Global Eşitleme başlıyor: ${targetLevels.joinToString { it.displayName }}", LogType.INFO)
                                                
                                                // API key'leri yükle
                                                GeminiApiProvider.loadKeysFromAssets(context)
                                                val keyCount = GeminiApiProvider.getLoadedKeyCount()
                                                addLog("🔑 $keyCount API key yüklendi", LogType.INFO)
                                                
                                                try {
                                                    withContext(Dispatchers.IO) {
                                                        // ADIM 1: TÜM SEVİYELERDEKİ TÜM DERSLERİ TOPLA
                                                        addLog("🔍 Tüm dersler taranıyor...", LogType.INFO)
                                                        
                                                        data class GlobalTarget(
                                                            val level: EducationLevel,
                                                            val schoolType: SchoolType,
                                                            val grade: Int?,
                                                            val subject: SubjectConfig,
                                                            val count: Int
                                                        )
                                                        
                                                        val allGlobalTargets = mutableListOf<GlobalTarget>()
                                                        
                                                        for (level in targetLevels) {
                                                            if (!isGlobalSyncRunning) break
                                                            
                                                            val schoolTypes = CurriculumManager.getSchoolTypesFor(level)
                                                            
                                                            for (schoolType in schoolTypes) {
                                                                if (!isGlobalSyncRunning) break
                                                                
                                                                val grades = if (schoolType.grades.isEmpty()) {
                                                                    listOf<Int?>(null)
                                                                } else {
                                                                    schoolType.grades.map { it as Int? }
                                                                }
                                                                
                                                                for (grade in grades) {
                                                                    if (!isGlobalSyncRunning) break
                                                                    
                                                                    val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                                                                    val counts = QuestionRepository.getQuestionCountsForLevel(level, schoolType, grade)
                                                                    
                                                                    for (subj in subjects) {
                                                                        val count = counts[subj.id] ?: 0
                                                                        allGlobalTargets.add(
                                                                            GlobalTarget(level, schoolType, grade, subj, count)
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        
                                                        if (!isGlobalSyncRunning) return@withContext
                                                        
                                                        // ADIM 2: GLOBAL OLARAK EN DÜŞÜK N DERSİ BUL
                                                        val sortedGlobalTargets = allGlobalTargets.sortedBy { it.count }
                                                        
                                                        withContext(Dispatchers.Main) {
                                                            addLog("📊 Toplam ${allGlobalTargets.size} ders tarandı", LogType.INFO)
                                                            addLog("🎯 En düşük 10 ders:", LogType.INFO)
                                                            sortedGlobalTargets.take(10).forEach { t ->
                                                                addLog("   - [${t.level.displayName}] ${t.subject.displayName}: ${t.count} soru", LogType.INFO)
                                                            }
                                                        }
                                                        
                                                        // ADIM 3: SONSUZ DÖNGÜ - EN DÜŞÜK N DERSİ SÜREKLI EŞİTLE
                                                        var roundCount = 0
                                                        while (isGlobalSyncRunning) {
                                                            roundCount++
                                                            
                                                            // Her turda güncel soru sayılarını çek
                                                            val currentTargets = mutableListOf<GlobalTarget>()
                                                            for (t in allGlobalTargets) {
                                                                if (!isGlobalSyncRunning) break
                                                                val counts = QuestionRepository.getQuestionCountsForLevel(t.level, t.schoolType, t.grade)
                                                                val currentCount = counts[t.subject.id] ?: 0
                                                                currentTargets.add(t.copy(count = currentCount))
                                                            }
                                                            
                                                            if (!isGlobalSyncRunning) break
                                                            
                                                            // En düşük N dersi al
                                                            val targets = currentTargets.sortedBy { it.count }.take(keyCount)
                                                            
                                                            withContext(Dispatchers.Main) {
                                                                globalSyncTotal = roundCount * keyCount
                                                                val targetStr = targets.mapIndexed { i, t -> 
                                                                    val emoji = listOf("🔵", "🟢", "🟣", "🟡")[i % 4]
                                                                    "$emoji[${t.level.displayName}]${t.subject.displayName}(${t.count})"
                                                                }.joinToString(" ")
                                                                addLog("🔄 TUR $roundCount: $targetStr", LogType.INFO)
                                                            }
                                                            
                                                            // PARALEL ÜRETIM - STAGGERED START
                                                            val jobs = mutableListOf<kotlinx.coroutines.Job>()
                                                            val emojis = listOf("🔵", "🟢", "🟣", "🟡")
                                                            
                                                            targets.forEachIndexed { index, target ->
                                                                jobs += kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                                                    // Staggered start: 0s, 1.5s, 3s, 4.5s
                                                                    delay(index * 1500L)
                                                                    
                                                                    val emoji = emojis[index % 4]
                                                                    try {
                                                                        val result = GeminiApiProvider.generateWithKey(
                                                                            index, 
                                                                            target.subject.displayName, 
                                                                            15, 
                                                                            target.level, 
                                                                            target.schoolType, 
                                                                            target.grade
                                                                        )
                                                                        
                                                                        if (result.first.isNotEmpty()) {
                                                                            val saved = QuestionRepository.saveQuestionsForLevel(
                                                                                result.first, 
                                                                                target.level, 
                                                                                target.schoolType, 
                                                                                target.grade, 
                                                                                target.subject.id
                                                                            )
                                                                            
                                                                            withContext(Dispatchers.Main) {
                                                                                globalSyncProgress++
                                                                                globalSyncStatus = "${target.subject.displayName}"
                                                                                totalQuestionsAddedSession += saved
                                                                                addLog("✅ $emoji [${target.level.displayName}] ${target.subject.displayName}: +$saved → ${target.count + saved} (${result.second})", LogType.SUCCESS)
                                                                                refreshStats()
                                                                            }
                                                                        } else {
                                                                            withContext(Dispatchers.Main) {
                                                                                globalSyncProgress++
                                                                                addLog("⚠️ $emoji ${result.second}: ${target.subject.displayName} - 0 soru", LogType.WARNING)
                                                                            }
                                                                        }
                                                                    } catch (e: Exception) {
                                                                        withContext(Dispatchers.Main) {
                                                                            globalSyncProgress++
                                                                            addLog("❌ $emoji ${target.subject.displayName}: ${e.message?.take(40)}", LogType.ERROR)
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            
                                                            // Tüm işlerin bitmesini bekle
                                                            jobs.forEach { it.join() }
                                                            
                                                            // Kısa bekleme (rate limit için)
                                                            delay(2000)
                                                        }
                                                    }
                                                    
                                                    withContext(Dispatchers.Main) {
                                                        addLog("🎉 Global Eşitleme tamamlandı!", LogType.SUCCESS)
                                                        globalSyncStatus = "Tamamlandı"
                                                        refreshStats()
                                                    }
                                                } catch (e: Exception) {
                                                    withContext(Dispatchers.Main) {
                                                        addLog("❌ Global Eşitleme hatası: ${e.message?.take(50)}", LogType.ERROR)
                                                    }
                                                } finally {
                                                    isGlobalSyncRunning = false
                                                }
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0))
                                    ) {
                                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("UI Modda Başlat", fontSize = 12.sp)
                                    }
                                    
                                    // 24/7 Arka Plan Modu
                                    Button(
                                        onClick = {
                                            GlobalSyncForegroundService.start(context, selectedGlobalLevel)
                                            addLog("🚀 24/7 Arka Plan Modu başlatıldı!", LogType.SUCCESS)
                                            addLog("📱 Uygulama kapansa bile çalışmaya devam edecek", LogType.INFO)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                                    ) {
                                        Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("24/7 Mod", fontSize = 12.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            isGlobalSyncRunning = false
                                            addLog("⛔ Global Eşitleme durduruluyor...", LogType.WARNING)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                                    ) {
                                        Icon(Icons.Default.Stop, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("UI Modunu Durdur")
                                    }
                                }
                            }
                            
                            // 24/7 Servis Durumu
                            if (GlobalSyncForegroundService.isServiceRunning()) {
                                Spacer(Modifier.height(8.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF00C853).copy(alpha = 0.1f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "🟢 24/7 Mod Aktif",
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00C853),
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                "Tur ${GlobalSyncForegroundService.currentRound.get()} | +${GlobalSyncForegroundService.totalQuestionsAdded.get()} soru",
                                                fontSize = 12.sp,
                                                color = Color(0xFF757575)
                                            )
                                            Text(
                                                GlobalSyncForegroundService.currentStatus,
                                                fontSize = 11.sp,
                                                color = Color(0xFF9E9E9E)
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                GlobalSyncForegroundService.stop(context)
                                                addLog("🛑 24/7 Mod durduruldu", LogType.WARNING)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text("Durdur", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 2. LOG KARTI (Üste alındı)
                    LogCard(logList)

                    // 1. AI KONTROL PANELİ
                    QuickAccessCard(
                        isRunning = isRunning,
                        isTekliMode = isTekliMode,
                        agsOabtSelectedField = agsOabtSelectedField,
                        agsOabtSelectedUnitIndex = agsOabtSelectedUnitIndex,
                        agsOabtSelectedUnitQuestionCount = agsOabtSelectedUnitQuestionCount,
                        onAgsOabtFieldChange = { newField ->
                            agsOabtSelectedField = newField
                            agsOabtSelectedUnitIndex = 1
                        },
                        onAgsOabtUnitIndexChange = { agsOabtSelectedUnitIndex = it },
                        onTekliStart = { level, schoolType, grade ->
                            scope.launch {
                                isRunning = true
                                isTekliMode = true
                                totalQuestionsAddedSession = 0
                                addLog("🚀 [$grade. Sınıf] Başlatıldı", LogType.INFO)

                                val generator = AiQuestionGenerator()
                                val subjects = CurriculumManager.getSubjectsFor(schoolType, grade)
                                val currentCounts = withContext(Dispatchers.IO) {
                                    QuestionRepository.getQuestionCountsForLevel(level, schoolType, grade)
                                }
                                val subjectCounts = subjects.map { subject -> subject to (currentCounts[subject.id] ?: 0) }
                                val lowestSubject = subjectCounts.minByOrNull { it.second }?.first

                                if (lowestSubject != null && isRunning) {
                                    currentTask = "${lowestSubject.icon} ${lowestSubject.displayName}"
                                    try {
                                        val questions = withContext(Dispatchers.IO) {
                                            generator.generateFastBatch(lowestSubject.displayName, 9, level, schoolType, grade)
                                        }
                                        if (questions.isNotEmpty()) {
                                            val saved = withContext(Dispatchers.IO) {
                                                QuestionRepository.saveQuestionsForLevel(questions, level, schoolType, grade, lowestSubject.id)
                                            }
                                            totalQuestionsAddedSession += saved
                                            addLog("✅ +$saved soru (${lowestSubject.displayName})", LogType.SUCCESS)
                                            refreshStats()
                                        } else {
                                            addLog("⚠️ ${lowestSubject.displayName}: Soru üretilemedi (3 deneme başarısız)", LogType.WARNING)
                                        }
                                    } catch (e: Exception) {
                                        val errorMsg = when {
                                            e.message?.contains("quota", ignoreCase = true) == true -> "API kotası aşıldı"
                                            e.message?.contains("rate", ignoreCase = true) == true -> "Rate limit"
                                            e.message?.contains("timeout", ignoreCase = true) == true -> "Zaman aşımı"
                                            else -> e.message?.take(40) ?: "Bilinmeyen hata"
                                        }
                                        addLog("❌ ${lowestSubject.displayName}: $errorMsg", LogType.ERROR)
                                    }
                                }
                                isRunning = false
                                isTekliMode = false
                                currentTask = "Hazır"
                                addLog("🏁 Tamamlandı", LogType.SUCCESS)
                            }
                        },
                        onTopluStart = { level, schoolType, _, aiMode ->
                            // Arka plan worker'ı da başlat (ekran kapatılırsa devam etsin)
                            QuestionSyncWorker.startContinuousSync(context)
                            isBackgroundRunning = true
                            
                            // Aynı zamanda görünür log'lu inline çalışmayı da başlat
                            scope.launch(Dispatchers.IO) {
                                isRunning = true
                                isTekliMode = false
                                totalQuestionsAddedSession = 0
                                
                                val modeLabel = when(aiMode) {
                                    AiMode.GEMINI -> "🔵 Gemini"
                                    AiMode.KARMA -> "⚡ Karma (4x Gemini)"
                                }
                                
                                withContext(Dispatchers.Main) {
                                    addLog("♾️ $modeLabel - Otomatik Eşitleme Başladı", LogType.INFO)
                                }
                                
                                val generator = AiQuestionGenerator()
                                val targetGrades = schoolType.grades.ifEmpty { listOf(0) }

                                while (isRunning && isActive) {
                                    // AGS ÖABT (2. Oturum) özel akış: ünite bazlı koleksiyonlara soru ekle
                                    if (level == EducationLevel.AGS && schoolType == SchoolType.AGS_OABT) {
                                        val subjects = AppPrefs.getAgsOabtUnitSubjects(agsOabtSelectedField)
                                        if (subjects.isEmpty()) {
                                            withContext(Dispatchers.Main) {
                                                addLog("⚠️ AGS ÖABT: '$agsOabtSelectedField' için ünite listesi bulunamadı", LogType.WARNING)
                                            }
                                            delay(3000)
                                            continue
                                        }

                                        val subjectCounts = mutableListOf<Pair<SubjectConfig, Int>>()
                                        for (subj in subjects) {
                                            if (!isRunning) break
                                            val count = runCatching {
                                                val col = Firebase.firestore
                                                    .collection("question_pools")
                                                    .document("AGS")
                                                    .collection("AGS_OABT")
                                                    .document("general")
                                                    .collection(subj.id)
                                                col.count().get(com.google.firebase.firestore.AggregateSource.SERVER).await().count.toInt()
                                            }.getOrDefault(0)
                                            subjectCounts.add(subj to count)
                                        }

                                        if (subjectCounts.isEmpty()) {
                                            delay(3000)
                                            continue
                                        }

                                        val sorted = subjectCounts.sortedBy { it.second }

                                        val dersName = when (agsOabtSelectedField) {
                                            "turkce" -> "Türkçe"
                                            "ilkmat" -> "İlköğretim Matematik"
                                            "fen" -> "Fen Bilimleri"
                                            "sosyal" -> "Sosyal Bilgiler"
                                            "edebiyat" -> "Türk Dili ve Edebiyatı"
                                            "cografya" -> "Coğrafya"
                                            "matematik" -> "Matematik"
                                            "fizik" -> "Fizik"
                                            "kimya" -> "Kimya"
                                            "biyoloji" -> "Biyoloji"
                                            "rehberlik" -> "Rehberlik"
                                            "sinif" -> "Sınıf Öğretmenliği"
                                            "okoncesi" -> "Okul Öncesi"
                                            "beden" -> "Beden Eğitimi"
                                            "din" -> "Din Kültürü"
                                            else -> agsOabtSelectedField
                                        }

                                        when (aiMode) {
                                            AiMode.GEMINI -> {
                                                val target = sorted.firstOrNull() ?: run {
                                                    delay(3000)
                                                    continue
                                                }
                                                val targetSubject = target.first
                                                val count = target.second
                                                val lessonTitle = "AGS $dersName - ${targetSubject.displayName} (${targetSubject.id})"

                                                withContext(Dispatchers.Main) {
                                                    currentTask = "AGS ÖABT ${targetSubject.displayName}"
                                                    addLog("📝 $currentTask: Soru üretiliyor... (mevcut: $count)", LogType.INFO)
                                                }

                                                val (questions, aiName) = generator.generateWithSource(
                                                    lessonTitle, 15, level, schoolType, null
                                                )
                                                if (questions.isNotEmpty()) {
                                                    val saved = QuestionRepository.saveQuestionsForLevel(
                                                        questions = questions,
                                                        level = EducationLevel.AGS,
                                                        schoolType = SchoolType.AGS_OABT,
                                                        grade = null,
                                                        subjectId = targetSubject.id
                                                    )
                                                    withContext(Dispatchers.Main) {
                                                        totalQuestionsAddedSession += saved
                                                        addLog("✅ ${targetSubject.displayName}: +$saved ($aiName)", LogType.SUCCESS)
                                                        refreshStats()
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        addLog("⚠️ ${targetSubject.displayName}: 0 soru ($aiName)", LogType.WARNING)
                                                    }
                                                }
                                            }

                                            AiMode.KARMA -> {
                                                GeminiApiProvider.loadKeysFromAssets(context)
                                                val keyCount = GeminiApiProvider.getLoadedKeyCount().coerceAtLeast(1)

                                                val targets = sorted.take(keyCount)
                                                val emojis = listOf("🔵", "🟢", "🟣", "🟡", "🟠", "🔴", "⚪")

                                                withContext(Dispatchers.Main) {
                                                    val targetStr = targets.mapIndexed { i, t ->
                                                        val emoji = emojis[i % emojis.size]
                                                        "$emoji${t.first.displayName}"
                                                    }.joinToString(" | ")
                                                    addLog("📝 ${keyCount}x PARALEL (AGS ÖABT/$dersName): $targetStr", LogType.INFO)
                                                }

                                                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                                                targets.forEachIndexed { index, target ->
                                                    val targetSubject = target.first
                                                    val lessonTitle = "AGS $dersName - ${targetSubject.displayName} (${targetSubject.id})"

                                                    jobs += kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                                        delay(index * 1500L)
                                                        val emoji = emojis[index % emojis.size]
                                                        try {
                                                            val result = GeminiApiProvider.generateWithKey(
                                                                keyIndex = index,
                                                                lesson = lessonTitle,
                                                                count = 15,
                                                                level = EducationLevel.AGS,
                                                                schoolType = SchoolType.AGS_OABT,
                                                                grade = null
                                                            )
                                                            if (result.first.isNotEmpty()) {
                                                                val saved = QuestionRepository.saveQuestionsForLevel(
                                                                    questions = result.first,
                                                                    level = EducationLevel.AGS,
                                                                    schoolType = SchoolType.AGS_OABT,
                                                                    grade = null,
                                                                    subjectId = targetSubject.id
                                                                )
                                                                withContext(Dispatchers.Main) {
                                                                    totalQuestionsAddedSession += saved
                                                                    addLog("✅ $emoji ${targetSubject.displayName}: +$saved (${result.second})", LogType.SUCCESS)
                                                                    refreshStats()
                                                                }
                                                            } else {
                                                                withContext(Dispatchers.Main) {
                                                                    addLog("⚠️ $emoji ${targetSubject.displayName}: 0 soru (${result.second})", LogType.WARNING)
                                                                }
                                                            }
                                                        } catch (e: Exception) {
                                                            withContext(Dispatchers.Main) {
                                                                addLog("❌ $emoji HATA: ${e.message?.take(40)}", LogType.ERROR)
                                                            }
                                                        }
                                                    }
                                                }

                                                jobs.forEach { it.join() }
                                                delay(2000)
                                                continue
                                            }
                                        }

                                        delay(1000)
                                        continue
                                    }

                                    // 1. GLOBAL TARAMA
                                    var bestTarget: Triple<Int, SubjectConfig, Int>? = null
                                    var minCount = Int.MAX_VALUE

                                    for (g in targetGrades) {
                                        if(!isRunning) break
                                        val subjects = CurriculumManager.getSubjectsFor(schoolType, g)
                                        val counts = QuestionRepository.getQuestionCountsForLevel(level, schoolType, g)
                                        
                                        for (subj in subjects) {
                                            val c = counts[subj.id] ?: 0
                                            if (c < minCount) {
                                                minCount = c
                                                bestTarget = Triple(g, subj, c)
                                            }
                                        }
                                    }

                                    if (bestTarget == null) {
                                        delay(3000)
                                        continue
                                    }

                                    val (targetGrade, targetSubject, count) = bestTarget
                                    withContext(Dispatchers.Main) {
                                        currentTask = "[$targetGrade. Sınıf] ${targetSubject.displayName}"
                                        addLog("📝 $currentTask: Soru üretiliyor... (mevcut: $count)", LogType.INFO)
                                    }

                                    try {
                                        // AI MODE'A GÖRE SORU ÜRETİMİ
                                        val allQuestions = mutableListOf<QuestionModel>()
                                        val aiLogs = mutableListOf<String>()
                                        
                                        when (aiMode) {
                                            AiMode.GEMINI -> {
                                                val (questions, aiName) = generator.generateWithSource(
                                                    targetSubject.displayName, 15, level, schoolType, targetGrade
                                                )
                                                allQuestions.addAll(questions)
                                                aiLogs.add("$aiName: ${questions.size} soru")
                                            }

                                            AiMode.KARMA -> {
                                                // API Key'leri yükle
                                                GeminiApiProvider.loadKeysFromAssets(context)
                                                
                                                // KARMA MOD: N Gemini PARALEL çalışır (yüklenen key sayısı kadar)
                                                val keyCount = GeminiApiProvider.getLoadedKeyCount()
                                                
                                                // En düşük N dersi bul
                                                val allTargets = mutableListOf<Triple<Int, SubjectConfig, Int>>()
                                                for (g in targetGrades) {
                                                    val subjects = CurriculumManager.getSubjectsFor(schoolType, g)
                                                    val counts = QuestionRepository.getQuestionCountsForLevel(level, schoolType, g)
                                                    for (subj in subjects) {
                                                        val c = counts[subj.id] ?: 0
                                                        allTargets.add(Triple(g, subj, c))
                                                    }
                                                }
                                                val sortedTargets = allTargets.sortedBy { it.third }
                                                
                                                // İlk N hedefi al
                                                val targets = (0 until keyCount).mapNotNull { sortedTargets.getOrNull(it) }
                                                
                                                // Hedefleri logla
                                                withContext(Dispatchers.Main) {
                                                    val targetStr = targets.mapIndexed { i, t -> 
                                                        val emoji = listOf("🔵", "🟢", "🟣", "🟡", "🟠", "🔴", "⚪")[i % 7]
                                                        "$emoji[${t.first}.Snf]${t.second.displayName}"
                                                    }.joinToString(" | ")
                                                    addLog("📝 ${keyCount}x PARALEL: $targetStr", LogType.INFO)
                                                }
                                                
                                                // N PARALEL COROUTINE - STAGGERED START (2sn arayla)
                                                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                                                val emojis = listOf("🔵", "🟢", "🟣", "🟡", "🟠", "🔴", "⚪")
                                                
                                                targets.forEachIndexed { index, target ->
                                                    jobs += kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                                        // Staggered start: 0s, 1.5s, 3s, 4.5s... (rate limiter 3sn olduğu için)
                                                        delay(index * 1500L)
                                                        
                                                        val emoji = emojis[index % 7]
                                                        try {
                                                            val result = GeminiApiProvider.generateWithKey(
                                                                index, target.second.displayName, 15, level, schoolType, target.first
                                                            )
                                                            if (result.first.isNotEmpty()) {
                                                                val saved = QuestionRepository.saveQuestionsForLevel(
                                                                    result.first, level, schoolType, target.first, target.second.id
                                                                )
                                                                withContext(Dispatchers.Main) {
                                                                    totalQuestionsAddedSession += saved
                                                                    addLog("✅ $emoji [${target.first}.Snf] ${target.second.displayName}: +$saved (${result.second})", LogType.SUCCESS)
                                                                    refreshStats()
                                                                }
                                                            } else {
                                                                withContext(Dispatchers.Main) {
                                                                    addLog("⚠️ $emoji ${result.second}: ${target.second.displayName} - 0 soru", LogType.WARNING)
                                                                }
                                                            }
                                                        } catch (e: Exception) {
                                                            withContext(Dispatchers.Main) {
                                                                addLog("❌ $emoji Gemini-${index+1} HATA: ${e.message?.take(40)}", LogType.ERROR)
                                                            }
                                                        }
                                                    }
                                                }
                                                
                                                // Tüm işlerin bitmesini bekle
                                                jobs.forEach { it.join() }
                                                
                                                // Kısa bekleme (rate limit için)
                                                delay(2000)
                                                continue // while döngüsüne dön
                                            }
                                        }
                                        
                                        if (allQuestions.isNotEmpty()) {
                                            val saved = QuestionRepository.saveQuestionsForLevel(allQuestions, level, schoolType, targetGrade, targetSubject.id)
                                            
                                            withContext(Dispatchers.Main) {
                                                totalQuestionsAddedSession += saved
                                                val logMsg = "✅ [$targetGrade. Snf] ${targetSubject.displayName}: +$saved soru (${aiLogs.joinToString(" | ")})"
                                                addLog(logMsg, LogType.SUCCESS)
                                                refreshStats()
                                            }
                                        } else {
                                            withContext(Dispatchers.Main) {
                                                addLog("⚠️ ${targetSubject.displayName}: Soru üretilemedi (${aiLogs.joinToString(" | ")})", LogType.WARNING)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        val errorMsg = when {
                                            e.message?.contains("quota", ignoreCase = true) == true -> "API kotası aşıldı"
                                            e.message?.contains("rate", ignoreCase = true) == true -> "Rate limit"
                                            e.message?.contains("timeout", ignoreCase = true) == true -> "Zaman aşımı"
                                            else -> e.message?.take(40) ?: "Bilinmeyen hata"
                                        }
                                        withContext(Dispatchers.Main) { 
                                            addLog("❌ ${targetSubject.displayName}: $errorMsg", LogType.ERROR) 
                                        }
                                    }
                                    
                                    delay(1000)
                                }
                                withContext(Dispatchers.Main) {
                                    isRunning = false
                                    addLog("⛔ Durduruldu", LogType.WARNING)
                                }
                            }
                        },
                        onStop = {
                            isRunning = false
                            isTekliMode = false
                            // Arka plan sync'i de durdur
                            QuestionSyncWorker.stopSync(context)
                            isBackgroundRunning = false
                            addLog("⛔ Eşitleme durduruldu", LogType.WARNING)
                        }
                    )


                    
                    // 3. GİZLİ SİLME PANELİ LİNKİ
                    if (secretDeleteUnlocked) {
                        SecretDeletePanelCard(
                            onNavigate = {
                                navController.navigate("admin_delete")
                            }
                        )
                    }
                    
                    // 4. AGS TARİH SORU EKLEME KARTI
                    AgsTarihQuestionCard(
                        isRunning = isRunning,
                        questionCount = agsTarihQuestionCount,
                        isDeleting = isAgsTarihDeleting,
                        onStart = {
                            scope.launch(Dispatchers.IO) {
                                isRunning = true

                                // PARALEL "AGS 4x GEMINI" MODU
                                // API Key'leri yükle
                                GeminiApiProvider.loadKeysFromAssets(context)
                                val keyCount = GeminiApiProvider.getLoadedKeyCount()
                                
                                withContext(Dispatchers.Main) {
                                    addLog("🏛️ AGS Tarih: ${keyCount}x PARALEL Üretim Başladı", LogType.INFO)
                                    addLog("♾️ En az sorusu olan ünitelere öncelik verilecek", LogType.INFO)
                                }
                                
                                val uniteList = listOf(
                                    "Tarih Bilimi", "Osmanlı Türkçesi", "Uygarlığın Doğuşu",
                                    "İlk Türk Devletleri", "İslam Tarihi", "Türk İslam Devletleri",
                                    "Türk Dünyası", "Osmanlı Tarihi", "En Uzun Yüzyıl",
                                    "XX. Yüzyıl Başları", "Milli Mücadele", "Atatürk Dönemi",
                                    "Dünya Tarihi", "Çağdaş Tarih"
                                )
                                
                                var totalAdded = 0
                                val emojis = listOf("🔵", "🟢", "🟣", "🟡", "🟠", "🔴", "⚪")
                                
                                // PARALEL JOBS
                                val jobs = mutableListOf<kotlinx.coroutines.Job>()
                                
                                repeat(keyCount) { keyIndex ->
                                    jobs += kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                        val emoji = emojis[keyIndex % 7]
                                        var roundCount = 0
                                        
                                        while (isRunning && isActive) {
                                            roundCount++
                                            
                                            // En az sorusu olan üniteyi bul (Her thread anlık duruma göre seçer)
                                            val uniteCounts = mutableListOf<Pair<Int, Int>>() // (uniteIndex, count)
                                            for (i in 1..14) {
                                                if (!isRunning) break
                                                val col = com.google.firebase.Firebase.firestore
                                                    .collection("question_pools")
                                                    .document("AGS")
                                                    .collection("AGS_OABT")
                                                    .document("general")
                                                    .collection("tarih_unite_$i")
                                                val count = try { col.get().await().size() } catch(e:Exception) { 0 }
                                                uniteCounts.add(i to count)
                                            }
                                            
                                            if (!isRunning) break
                                            
                                            // En az sorusu olan üniteyi bul
                                            val (lowestUniteIndex, lowestCount) = uniteCounts.minByOrNull { it.second } ?: (1 to 0)
                                            val unite = uniteList.getOrNull(lowestUniteIndex - 1) ?: uniteList[0]
                                            val subjectId = "tarih_unite_$lowestUniteIndex"
                                            val lessonTitle = "AGS Tarih - $unite"
                                            
                                            withContext(Dispatchers.Main) {
                                                addLog("$emoji Gemini-${keyIndex+1}: $unite hedefleniyor ($lowestCount soru)", LogType.INFO)
                                            }
                                            
                                            try {
                                                // Key'e özel üretim (15 soru iste)
                                                val result = GeminiApiProvider.generateWithKey(
                                                    keyIndex = keyIndex,
                                                    lesson = lessonTitle,
                                                    count = 15,
                                                    level = EducationLevel.AGS,
                                                    schoolType = SchoolType.AGS_OABT,
                                                    grade = null
                                                )
                                                
                                                val questions = result.first
                                                
                                                if (questions.isNotEmpty()) {
                                                    val saved = QuestionRepository.saveQuestionsForLevel(
                                                        questions = questions,
                                                        level = EducationLevel.AGS,
                                                        schoolType = SchoolType.AGS_OABT,
                                                        grade = null,
                                                        subjectId = subjectId
                                                    )
                                                    
                                                    // synchronized increment
                                                    synchronized(this@launch) { totalAdded += saved }
                                                    
                                                    withContext(Dispatchers.Main) {
                                                        totalQuestionsAddedSession += saved
                                                        addLog("✅ $emoji $unite: +$saved soru (Toplam: ${lowestCount + saved})", LogType.SUCCESS)
                                                        // İstatistikleri güncelle (Main thread'de UI güncellesin)
                                                        agsTarihQuestionCount = QuestionRepository.getAgsTarihQuestionCount()
                                                    }
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        addLog("⚠️ $emoji $unite: Soru üretilemedi", LogType.WARNING)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                withContext(Dispatchers.Main) {
                                                    addLog("❌ $emoji Hata: ${e.message?.take(30)}", LogType.ERROR)
                                                }
                                                delay(5000)
                                            }
                                            
                                            // Rate limit beklemesi (thread başına)
                                            delay(2000)
                                        }
                                    }
                                    // Start delay between threads to stagger requests
                                    delay(500)
                                }
                                
                                // Tüm jobların bitmesini bekle (isRunning false olana kadar çalışırlar)
                                jobs.forEach { it.join() }
                                
                                withContext(Dispatchers.Main) {
                                    isRunning = false
                                    currentTask = "Hazır"
                                    addLog("🏁 AGS Tarih durduruldu: Toplam +$totalAdded soru", LogType.SUCCESS)
                                    refreshStats()
                                }
                            }
                        },
                        onStop = {
                            isRunning = false
                            addLog("⛔ AGS Tarih durduruluyor...", LogType.WARNING)
                        },
                        onDelete = {
                            scope.launch {
                                isAgsTarihDeleting = true
                                addLog("🗑️ AGS Tarih soruları siliniyor...", LogType.WARNING)
                                try {
                                    val deleted = withContext(Dispatchers.IO) {
                                        QuestionRepository.deleteAgsTarihQuestions()
                                    }
                                    addLog("✅ AGS Tarih: $deleted soru silindi", LogType.SUCCESS)
                                    refreshStats()
                                } catch (e: Exception) {
                                    addLog("❌ Silme hatası: ${e.message}", LogType.ERROR)
                                } finally {
                                    isAgsTarihDeleting = false
                                }
                            }
                        }
                    )
                }
    }
}
}
}

// ==================== GÜNCELLENMİŞ QUICK ACCESS CARD ====================
@Composable
private fun QuickAccessCard(
    isRunning: Boolean,
    isTekliMode: Boolean,
    agsOabtSelectedField: String,
    agsOabtSelectedUnitIndex: Int,
    agsOabtSelectedUnitQuestionCount: Int,
    onAgsOabtFieldChange: (String) -> Unit,
    onAgsOabtUnitIndexChange: (Int) -> Unit,
    onTekliStart: (EducationLevel, SchoolType, Int?) -> Unit,
    onTopluStart: (EducationLevel, SchoolType, Int?, AiMode) -> Unit,
    onStop: () -> Unit
) {
    var selectedLevel by remember { mutableStateOf<EducationLevel?>(null) }
    var selectedSchoolType by remember { mutableStateOf<SchoolType?>(null) }
    var selectedGrade by remember { mutableStateOf<Int?>(null) }
    
    // Ortaokul İmam Hatip bug fix için launched effect
    LaunchedEffect(selectedLevel) {
        if (selectedLevel != null) {
             val types = CurriculumManager.getSchoolTypesFor(selectedLevel!!)
             if (selectedSchoolType == null || selectedSchoolType !in types) {
                 selectedSchoolType = types.first()
                 selectedGrade = selectedSchoolType?.grades?.firstOrNull()
             }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.SmartToy, null, tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("AI Soru Üretici", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                    Text("Otomatik Müfredat Analizi", fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(Modifier.height(20.dp))
            
            // 1. SEVİYE
            Text("1. EĞİTİM SEVİYESİ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
            Spacer(Modifier.height(8.dp))
            val allowedLevels = listOf(EducationLevel.ILKOKUL, EducationLevel.ORTAOKUL, EducationLevel.LISE, EducationLevel.KPSS, EducationLevel.AGS)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allowedLevels) { level ->
                    FilterChip(
                        selected = level == selectedLevel,
                        onClick = { selectedLevel = level },
                        label = { Text(level.displayName) },
                        leadingIcon = { Text(level.icon) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE3F2FD),
                            selectedLabelColor = Color(0xFF1565C0),
                            selectedLeadingIconColor = Color(0xFF1565C0)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = true,
                            borderColor = if (level == selectedLevel) Color(0xFF1565C0) else Color(0xFFCFD8DC)
                        )
                    )
                }
            }
            
            // 2. OKUL TÜRÜ
            selectedLevel?.let { level ->
                val schoolTypes = CurriculumManager.getSchoolTypesFor(level)
                
                if (schoolTypes.size > 1) {
                    Spacer(Modifier.height(16.dp))
                    Text("2. OKUL TÜRÜ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(schoolTypes) { type ->
                            InputChip(
                                selected = type == selectedSchoolType,
                                onClick = { 
                                    selectedSchoolType = type 
                                    selectedGrade = type.grades.firstOrNull()
                                },
                                label = { Text(type.displayName) },
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = Color(0xFFF3E5F5),
                                    selectedLabelColor = Color(0xFF7B1FA2)
                                ),
                                border = InputChipDefaults.inputChipBorder(
                                    enabled = true,
                                    selected = true,
                                    borderColor = if (type == selectedSchoolType) Color(0xFF7B1FA2) else Color(0xFFCFD8DC)
                                )
                            )
                        }
                    }
                }
            }
            
            // 3. SINIF
            selectedSchoolType?.let { type ->
                if (type.grades.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text("3. SINIF SEÇİMİ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                    Spacer(Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(type.grades) { grade ->
                            Surface(
                                onClick = { selectedGrade = grade },
                                shape = CircleShape,
                                color = if (grade == selectedGrade) Color(0xFF1565C0) else Color(0xFFF5F5F5),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        "$grade", 
                                        color = if (grade == selectedGrade) Color.White else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (selectedLevel == EducationLevel.AGS && selectedSchoolType == SchoolType.AGS_OABT) {
                Spacer(Modifier.height(16.dp))
                Text("3. ÖABT ALAN / ÜNİTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                Spacer(Modifier.height(8.dp))

                val fields = listOf(
                    "turkce" to "Türkçe",
                    "ilkmat" to "İlkmat",
                    "fen" to "Fen",
                    "sosyal" to "Sosyal",
                    "edebiyat" to "TDE",
                    "cografya" to "Coğrafya",
                    "matematik" to "Matematik",
                    "fizik" to "Fizik",
                    "kimya" to "Kimya",
                    "biyoloji" to "Biyoloji",
                    "rehberlik" to "Rehberlik",
                    "sinif" to "Sınıf",
                    "okoncesi" to "Ok. Öncesi",
                    "beden" to "Beden",
                    "din" to "Din"
                )
                val units = remember(agsOabtSelectedField) {
                    runCatching { AppPrefs.getAgsOabtUnitSubjects(agsOabtSelectedField) }.getOrDefault(emptyList())
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(fields) { (field, label) ->
                        FilterChip(
                            selected = field == agsOabtSelectedField,
                            onClick = { onAgsOabtFieldChange(field) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ünite", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = if (agsOabtSelectedUnitQuestionCount > 0) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "${agsOabtSelectedUnitQuestionCount}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                if (units.isNotEmpty()) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(units.size) { idx ->
                            val uniteId = idx + 1
                            FilterChip(
                                selected = uniteId == agsOabtSelectedUnitIndex,
                                onClick = { onAgsOabtUnitIndexChange(uniteId) },
                                label = { Text("$uniteId") }
                            )
                        }
                    }

                    val selectedUnitTitle = units.getOrNull(agsOabtSelectedUnitIndex - 1)?.displayName
                    if (!selectedUnitTitle.isNullOrBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(selectedUnitTitle, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            // AKSİYON BUTONLARI
            if (isRunning) {
                Button(
                    onClick = onStop,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Stop, null)
                    Spacer(Modifier.width(8.dp))
                    Text("DURDUR", fontWeight = FontWeight.Bold)
                }
            } else {
                // AI MOD SEÇİMİ - 3 BUTON
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🤖 AI MOD SEÇİMİ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                    Spacer(Modifier.height(4.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // 🔵 GEMINI BUTONU
                        Button(
                            onClick = { 
                                if(selectedLevel != null && selectedSchoolType != null) 
                                    onTopluStart(selectedLevel!!, selectedSchoolType!!, selectedGrade, AiMode.GEMINI) 
                            },
                            enabled = selectedLevel != null && selectedSchoolType != null,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔵", fontSize = 16.sp)
                                Text("Gemini", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                        
                        // ⚡ KARMA BUTONU (PARALEL)
                        Button(
                            onClick = { 
                                if(selectedLevel != null && selectedSchoolType != null) 
                                    onTopluStart(selectedLevel!!, selectedSchoolType!!, selectedGrade, AiMode.KARMA) 
                            },
                            enabled = selectedLevel != null && selectedSchoolType != null,
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("⚡", fontSize = 16.sp)
                                Text("Karma", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text("2x Hız", fontSize = 8.sp, color = Color.White.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== ISTATISTIK KARTI ====================
@Composable
private fun QuestionStatsCard(
    selectedLevel: EducationLevel,
    selectedSchoolType: SchoolType,
    selectedGrade: Int?,
    questionCounts: Map<String, Int>,
    isLoading: Boolean,
    lastUpdateTime: String,
    onLevelChange: (EducationLevel) -> Unit,
    onSchoolTypeChange: (SchoolType) -> Unit,
    onGradeChange: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    val schoolTypes = remember(selectedLevel) { CurriculumManager.getSchoolTypesFor(selectedLevel) }
    val subjects = remember(selectedSchoolType, selectedGrade) {
        CurriculumManager.getSubjectsFor(selectedSchoolType, selectedGrade)
    }
    
    // Dark Mode Kontrolü
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), // Padding eklendi (öncekinde yoktu ama iyi olur)
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E1E1E) else Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📊 Ders Bazlı Veriler", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if(isDark) Color(0xFFB0BEC5) else Color(0xFF455A64))
                IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                    if (isLoading) CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    else Icon(Icons.Default.Refresh, null, tint = Color.Gray)
                }
            }
            Text("Son güncelleme: $lastUpdateTime", fontSize = 10.sp, color = Color.Gray)
            
            Spacer(Modifier.height(16.dp))
            
            // Filtreler (Basit)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Seviye
                listOf(EducationLevel.ILKOKUL, EducationLevel.ORTAOKUL).forEach { lvl ->
                    val isSel = lvl == selectedLevel
                    Surface(
                        onClick = { onLevelChange(lvl) },
                        shape = RoundedCornerShape(8.dp),
                        color = if(isSel) Color(0xFFECEFF1) else Color.Transparent,
                        border = if(!isSel) BorderStroke(1.dp, Color(0xFFCFD8DC)) else null
                    ) {
                        Text(
                            lvl.displayName.take(4), 
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            color = if(isSel) (if(isDark) Color.Black else Color.Black) else Color.Gray
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            // Okul Türü Seçicisi (Fixed)
            if (schoolTypes.size > 1) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(schoolTypes) { st ->
                        val isSel = st == selectedSchoolType
                        Surface(
                            onClick = { onSchoolTypeChange(st) },
                            shape = RoundedCornerShape(8.dp),
                            color = if(isSel) Color(st.level.colorHex).copy(alpha = 0.1f) else Color.Transparent,
                            border = BorderStroke(1.dp, if(isSel) Color(st.level.colorHex) else Color(0xFFCFD8DC))
                        ) {
                            Text(
                                st.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = if(isSel) Color(st.level.colorHex) else Color.Gray,
                                fontWeight = if(isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            
            // Dersler Listesi
            Spacer(Modifier.height(8.dp))
            subjects.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { subj ->
                        val count = questionCounts[subj.id] ?: 0
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = if(isDark) {
                                if(count > 0) Color(0xFF1B5E20) else Color(0xFF3E2723)
                            } else {
                                if(count > 0) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(subj.icon, fontSize = 20.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        subj.displayName, 
                                        fontSize = 13.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        maxLines = 1,
                                        color = if(isDark) Color(0xFFE0E0E0) else Color(0xFF424242)
                                    )
                                    Text(
                                        "$count Soru", 
                                        fontSize = 14.sp, 
                                        fontWeight = FontWeight.Bold,
                                        color = if(isDark) {
                                            if(count > 0) Color(0xFFA5D6A7) else Color(0xFFFFAB91)
                                        } else {
                                            if(count > 0) Color(0xFF1B5E20) else Color(0xFFBF360C)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ==================== DELETE CARD ====================
@Composable
private fun DeleteAllCard(
    isRunning: Boolean,
    onDelete: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    
    val correctPassword = "636363"
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF3E2723) else Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.DeleteForever, null, tint = Color(0xFFEF5350))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Tüm Soruları Sil", fontWeight = FontWeight.Bold, color = if(androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFFEF9A9A) else Color(0xFFD32F2F))
                Text("Dikkat: Geri alınamaz!", fontSize = 10.sp, color = if(androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFFFFCDD2) else Color(0xFFB71C1C))
            }
            Button(
                onClick = { 
                    showPasswordDialog = true
                    password = ""
                    passwordError = false
                },
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("SİL", fontSize = 12.sp)
            }
        }
    }
    
    // Şifre giriş dialogu
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                password = ""
                passwordError = false
            },
            title = { Text("🔐 Şifre Gerekli", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("Tüm soruları silmek için yetkilendirme şifresini girin:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false
                        },
                        label = { Text("Şifre") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = passwordError,
                        supportingText = if (passwordError) {
                            { Text("Yanlış şifre!", color = Color.Red) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (password == correctPassword) {
                            showPasswordDialog = false
                            showConfirmDialog = true
                            password = ""
                        } else {
                            passwordError = true
                        }
                    }
                ) {
                    Text("Doğrula", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPasswordDialog = false 
                    password = ""
                    passwordError = false
                }) { 
                    Text("İptal") 
                }
            }
        )
    }
    
    // Son onay dialogu
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("⚠️ Son Onay", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) },
            text = { Text("Tüm sorular kalıcı olarak silinecek. Bu işlem geri alınamaz!") },
            confirmButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    onDelete() 
                }) {
                    Text("Evet, Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("İptal") }
            }
        )
    }
}

// ==================== SEVİYE BAZLI SİLME KARTI ====================
@Composable
private fun DeleteByLevelCard(
    isRunning: Boolean,
    onDeleteLevel: (EducationLevel) -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedLevel by remember { mutableStateOf<EducationLevel?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    
    val correctPassword = "636363"
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF263238) else Color(0xFFE8EAF6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Layers, null, tint = Color(0xFF5C6BC0))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Seviye Bazlı Silme", fontWeight = FontWeight.Bold, color = if(isDark) Color(0xFFC5CAE9) else Color(0xFF3F51B5))
                    Text("Sadece seçilen seviyenin soruları silinir", fontSize = 10.sp, color = if(isDark) Color(0xFF9FA8DA) else Color(0xFF7986CB))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Seviye butonları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // İlkokul butonu
                Button(
                    onClick = { 
                        selectedLevel = EducationLevel.ILKOKUL
                        showPasswordDialog = true
                        password = ""
                        passwordError = false
                    },
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📗", fontSize = 14.sp)
                        Text("İlkokul", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Ortaokul butonu
                Button(
                    onClick = { 
                        selectedLevel = EducationLevel.ORTAOKUL
                        showPasswordDialog = true
                        password = ""
                        passwordError = false
                    },
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📘", fontSize = 14.sp)
                        Text("Ortaokul", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Lise butonu
                Button(
                    onClick = { 
                        selectedLevel = EducationLevel.LISE
                        showPasswordDialog = true
                        password = ""
                        passwordError = false
                    },
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📕", fontSize = 14.sp)
                        Text("Lise", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            
            // 2. Satır: KPSS ve Diğerleri
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // KPSS butonu
                Button(
                    onClick = { 
                        selectedLevel = EducationLevel.KPSS
                        showPasswordDialog = true
                        password = ""
                        passwordError = false
                    },
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)), // Pembe
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(48.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎓", fontSize = 14.sp)
                        Text("KPSS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                // Boşluk doldurucu (simetri için)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.weight(1f))
            }
        }
    }
    
    // Şifre giriş dialogu
    if (showPasswordDialog && selectedLevel != null) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                password = ""
                passwordError = false
            },
            title = { Text("🔐 ${selectedLevel?.displayName} Silme", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("${selectedLevel?.displayName} sorularını silmek için şifre girin:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false
                        },
                        label = { Text("Şifre") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = passwordError,
                        supportingText = if (passwordError) {
                            { Text("Yanlış şifre!", color = Color.Red) }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (password == correctPassword) {
                            showPasswordDialog = false
                            showConfirmDialog = true
                            password = ""
                        } else {
                            passwordError = true
                        }
                    }
                ) {
                    Text("Doğrula", color = Color(0xFF3F51B5))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showPasswordDialog = false 
                    password = ""
                }) { Text("İptal") }
            }
        )
    }
    
    // Onay dialogu
    if (showConfirmDialog && selectedLevel != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("⚠️ Son Onay", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("${selectedLevel?.displayName} seviyesindeki TÜM sorular silinecek!")
                    Spacer(Modifier.height(8.dp))
                    Text("Bu işlem geri alınamaz.", fontWeight = FontWeight.Bold, color = Color.Red)
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    selectedLevel?.let { onDeleteLevel(it) }
                }) {
                    Text("Evet, Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("İptal") }
            }
        )
    }
}

@Composable
private fun LogCard(
    logs: List<LogEntry>
) {
    val isDark = isSystemInDarkTheme()
    
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1E1E1E) else Color(0xFF161B22))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFF5F56)))
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFBD2E)))
                Spacer(Modifier.width(6.dp))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF27C93F)))
                Spacer(Modifier.width(12.dp))
                Text("System Log", color = Color(0xFF8B949E), fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn {
                items(logs) { log ->
                    val color = when(log.type) {
                        LogType.SUCCESS -> Color(0xFF238636)
                        LogType.ERROR -> Color(0xFFDA3633)
                        LogType.WARNING -> Color(0xFF9E6A03)
                        LogType.INFO -> Color(0xFF8B949E)
                    }
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(log.time, color = Color.Gray, fontSize = 10.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, modifier = Modifier.width(50.dp))
                        Text(log.message, color = color, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

enum class LogType { INFO, SUCCESS, WARNING, ERROR }
data class LogEntry(val time: String, val message: String, val type: LogType)
class StarVisualTransformation : VisualTransformation {
    override fun filter(text: androidx.compose.ui.text.AnnotatedString): androidx.compose.ui.text.input.TransformedText {
        return androidx.compose.ui.text.input.TransformedText(
            androidx.compose.ui.text.AnnotatedString("★".repeat(text.text.length)),
            androidx.compose.ui.text.input.OffsetMapping.Identity
        )
    }
}

// ==================== AGS TARİH SORU EKLEME KARTI ====================
@Composable
private fun AgsTarihQuestionCard(
    isRunning: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit = {},
    questionCount: Int = 0,
    isDeleting: Boolean = false
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0xFF3E2723) else Color(0xFFFFF8E1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF795548).copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🏛️", fontSize = 24.sp)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "AGS Tarih Soru Üretici",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )
                    Text(
                        "14 Ünite • MEB Müfredatı",
                        fontSize = 12.sp,
                        color = Color(0xFF8D6E63)
                    )
                }
                // Soru sayısı badge
                Surface(
                    color = if (questionCount > 0) Color(0xFF4CAF50) else Color(0xFFBDBDBD),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$questionCount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Bilgi kartı
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF795548).copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📚", fontSize = 20.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Bulutta: $questionCount soru mevcut",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5D4037)
                        )
                        Text(
                            "Her ünite için 10 soru üretilir (5 şıklı A-E)",
                            fontSize = 11.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // Butonlar
            if (isRunning) {
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Stop, null)
                    Spacer(Modifier.width(8.dp))
                    Text("DURDUR", fontWeight = FontWeight.Bold)
                }
            } else if (isDeleting) {
                Button(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBDBDBD)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("SİLİNİYOR...", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sil butonu
                    Button(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        enabled = questionCount > 0
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("SİL", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    
                    // Üret butonu
                    Button(
                        onClick = onStart,
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🏛️", fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Text("SORU ÜRET", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }

}

// ==================== KPSS DENEME SİLME KARTI ====================
@Composable
private fun DeleteKpssDenemeCard(
    isRunning: Boolean,
    onDelete: () -> Unit
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    
    val correctPassword = "636363"
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if(isDark) Color(0xFF1B5E20) else Color(0xFFE0F2F1))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DeleteSweep, null, tint = Color(0xFF00897B))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("KPSS Deneme Paketlerini Sil", fontWeight = FontWeight.Bold, color = if(isDark) Color(0xFF80CBC4) else Color(0xFF00796B))
                    Text("Tüm deneme sınavı paketlerini ve sorularını siler", fontSize = 10.sp, color = if(isDark) Color(0xFF4DB6AC) else Color(0xFF00695C))
                }
                
                Button(
                    onClick = { 
                        showPasswordDialog = true
                        password = ""
                        passwordError = false
                    },
                    enabled = !isRunning,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("TEMİZLE", fontSize = 12.sp)
                }
            }
        }
    }
    
    // Şifre dialogu
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { 
                showPasswordDialog = false 
                password = ""
            },
            title = { Text("🔐 Deneme Paketlerini Sil", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("Tüm KPSS Deneme paketlerini (sorular dahil) silmek için şifre girin:")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            passwordError = false
                        },
                        label = { Text("Şifre") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        isError = passwordError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (passwordError) {
                         Text("Yanlış şifre!", color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        if (password == correctPassword) {
                            showPasswordDialog = false
                            showConfirmDialog = true
                            password = ""
                        } else {
                            passwordError = true
                        }
                    }
                ) {
                    Text("Doğrula", color = Color(0xFF00796B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) { Text("İptal") }
            }
        )
    }
    
    // Onay dialogu
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("⚠️ Kesin Onay", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { Text("Tüm deneme paketleri ve içerikleri kalıcı olarak silinecek. Emin misiniz?") },
            confirmButton = {
                TextButton(onClick = { 
                    showConfirmDialog = false
                    onDelete() 
                }) {
                    Text("Evet, Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("İptal") }
            }
        )
    }
}

// ==================== GİZLİ SİLME PANELİ KARTI ====================
@Composable
private fun SecretDeletePanelCard(
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF3E0) // Turuncu arka plan
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İkon
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFFFF6F00),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Metin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "🔒 Gizli Silme Paneli",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Text(
                    "Şifre korumalı silme işlemleri",
                    fontSize = 14.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Buton
            Button(
                onClick = onNavigate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6F00)
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Aç",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
