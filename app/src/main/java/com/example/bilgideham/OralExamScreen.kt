package com.example.bilgideham

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.border

// --- 5. SINIF MÜFREDAT KONULARI (DATABASE) ---
val grade5Curriculum = mapOf(
    "Fen Bilimleri" to listOf(
        "Güneş, Dünya ve Ay'ın hareketleri", "Ay'ın evreleri", "Canlıları tanıyalım (Mikroskobik canlılar, Mantarlar)",
        "Kuvvetin ölçülmesi ve sürtünme", "Maddenin hal değişimi", "Isı ve Sıcaklık",
        "Işığın yayılması", "Işığın yansıması", "İnsan ve Çevre ilişkisi", "Elektrik devre elemanları"
    ),
    "Sosyal Bilgiler" to listOf(
        "Haklarımız ve sorumluluklarımız", "Çocuk hakları", "Anadolu ve Mezopotamya uygarlıkları",
        "Doğal varlıklar ve tarihi mekanlar", "Kültürel zenginliklerimiz", "Harita okuryazarlığı (İklim, Yeryüzü şekilleri)",
        "Teknoloji ve toplum", "Üretim, Dağıtım, Tüketim", "Bilinçli tüketici", "Etkin vatandaşlık"
    ),
    "Türkçe" to listOf(
        "Deyimler ve Atasözleri", "Gerçek ve Mecaz anlam", "Eş ve Zıt anlamlı kelimeler",
        "Noktalama işaretleri", "Cümlede anlam (Neden-Sonuç, Amaç-Sonuç)", "Metnin ana fikri",
        "Söz sanatları (Benzetme, Kişileştirme)", "Yazım kuralları"
    ),
    "Matematik" to listOf(
        "Milyonlu sayılar", "Örüntüler", "Doğal sayılarla işlemler", "Kesirler (Birim kesir, Tam sayılı kesir)",
        "Ondalık gösterim", "Yüzdeler", "Temel geometrik kavramlar (Doğru, Işın, Açı)",
        "Üçgen ve Dörtgenler", "Veri toplama ve değerlendirme", "Uzunluk ve Zaman ölçme"
    ),
    "Din Kültürü" to listOf(
        "Allah inancı (Tevhid)", "Ramazan ve Oruç", "Adap ve Nezaket kuralları",
        "Peygamberimizin hayatı (Aile hayatı)", "Camiyi tanıyalım", "Dua ve sureler"
    ),
    "İngilizce" to listOf(
        "Hello (Greetings)", "My Town (Locations, Directions)", "Games and Hobbies",
        "My Daily Routine", "Health (Illnesses)", "Movies", "Party Time", "Fitness", "The Animal Shelter", "Festivals"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OralExamScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- DURUMLAR ---
    var selectedLesson by remember { mutableStateOf("Karma") }
    var currentTopic by remember { mutableStateOf("") }

    var question by remember { mutableStateOf("Merhaba! Hangi dersten sözlü yapalım?") }
    var userSpeech by remember { mutableStateOf("") }
    var aiFeedback by remember { mutableStateOf("") }
    var aiScore by remember { mutableStateOf(-1) } // -1: Yok, 0-100: Puan

    var isListening by remember { mutableStateOf(false) }
    var processingState by remember { mutableStateOf(0) } // 0:Idle, 1:Listening, 2:Thinking, 3:Done

    // İzin
    var hasPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    // Mikrofon Animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    // Yeni Soru Üretme Fonksiyonu
    fun generateNewQuestion() {
        scope.launch {
            processingState = 2 // Düşünüyor
            question = "Soru hazırlanıyor..."
            userSpeech = ""
            aiFeedback = ""
            aiScore = -1
            currentTopic = ""

            try {
                // Konu Seçimi
                val lessonKey = if (selectedLesson == "Karma") grade5Curriculum.keys.random() else selectedLesson
                val topics = grade5Curriculum[lessonKey] ?: listOf("Genel Kültür")
                currentTopic = topics.random()

                // Basit prompt formatı (diğer çalışan fonksiyonlar gibi)
                val prompt = "Sen 5. Sınıf öğretmenisin. Ders: $lessonKey, Konu: $currentTopic. GÖREV: Bu konuyla ilgili kısa bir sözlü sınav sorusu sor. Sadece soruyu yaz."

                question = aiGenerateText(prompt)
            } catch (e: Exception) {
                question = "❌ Hata: ${e.localizedMessage ?: e.message ?: "Bilinmeyen hata"}"
            }
            processingState = 0
        }
    }

    LaunchedEffect(Unit) {
        // İsteğe bağlı otomatik başlangıç
    }

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
        }
    }

    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isListening = false
                processingState = 2
            }
            override fun onError(error: Int) {
                isListening = false
                processingState = 0
                userSpeech = "Sesini duyamadım, tekrar dener misin? (Hata: $error)"
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    userSpeech = matches[0]
                    scope.launch {
                        val prompt = """
                            SORU: $question
                            ÖĞRENCİ CEVABI: "$userSpeech"
                            
                            GÖREV:
                            1. Cevabı değerlendir. Doğru mu, yanlış mı, eksik mi?
                            2. 100 üzerinden bir puan ver. (Format: PUAN: 85)
                            3. Öğrenciye hitaben motive edici kısa bir geri bildirim yaz.
                        """.trimIndent()

                        val response = aiGenerateText(prompt)

                        // Puanı ayıklamaya çalış (Basit Regex)
                        val scoreRegex = Regex("PUAN:\\s*(\\d+)")
                        val match = scoreRegex.find(response)
                        aiScore = match?.groupValues?.get(1)?.toIntOrNull() ?: 0

                        aiFeedback = response.replace(Regex("PUAN:\\s*\\d+"), "").trim()
                        processingState = 3
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        onDispose { speechRecognizer.destroy() }
    }

    Scaffold(
        containerColor = Color(0xFF0F172A), // Slate-900 (Dark Background)
        topBar = {
            // Modern Gradient TopBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1E3A8A), // Blue-900
                                Color(0xFF7C3AED)  // Violet-600
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Geri Butonu
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            null, 
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(Modifier.width(12.dp))
                    
                    // Başlık
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Yapay Zeka Sözlüsü", 
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Text(
                            "AI Destekli Sözlü Sınav 🎓",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    
                    // Yenile Butonu - Glassmorphism
                    IconButton(
                        onClick = { generateNewQuestion() },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                ),
                                CircleShape
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Refresh, 
                            "Yeni Soru", 
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    ) { p ->
        Box(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFF0F172A)) // Slate-900
        ) {
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- DERS SEÇİMİ (YATAY KAYDIRMA) ---
                val lessons = listOf("Karma") + grade5Curriculum.keys.toList()
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(lessons) { lesson ->
                        val isSelected = selectedLesson == lesson
                        val backgroundColor = if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B) // Blue-500 or Slate-800
                        val contentColor = if (isSelected) Color.White else Color(0xFF94A3B8) // Slate-400
                        val borderColor = if (isSelected) Color.Transparent else Color(0xFF334155) // Slate-700

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = backgroundColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .clickable { selectedLesson = lesson; generateNewQuestion() }
                                .height(36.dp),
                            shadowElevation = if (isSelected) 8.dp else 0.dp
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(
                                    text = lesson,
                                    color = contentColor,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // --- SORU KARTI ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)), // Slate-800
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF3B82F6).copy(alpha = 0.1f), // Blue-500 with alpha
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome, 
                                    null, 
                                    tint = Color(0xFF3B82F6), // Blue-500
                                    modifier = Modifier.padding(6.dp).size(20.dp)
                                )
                            }
                            
                            Text(
                                if (currentTopic.isNotEmpty()) currentTopic.uppercase() else "HAZIR MISIN?",
                                fontWeight = FontWeight.Bold, 
                                color = Color(0xFF3B82F6), // Blue-500
                                fontSize = 12.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text(
                            text = question,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White, // White text for visibility
                            lineHeight = 28.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- İÇERİK ve DURUM ALANI (Scrollable) ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Durum Animasyonları
                    if (processingState == 1) {
                         Text(
                             "Dinliyorum...", 
                             color = Color(0xFFEF4444), // Red-500
                             fontWeight = FontWeight.Bold,
                             modifier = Modifier.padding(bottom = 8.dp)
                         )
                    } else if (processingState == 2) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(150.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF3B82F6), // Blue-500
                            trackColor = Color(0xFF334155) // Slate-700
                        )
                        Text(
                            "Öğretmen değerlendiriyor...", 
                            modifier = Modifier.padding(top = 12.dp), 
                            color = Color(0xFF94A3B8), // Slate-400
                            fontSize = 13.sp
                        )
                    }

                    // Cevap Kartı
                    if (userSpeech.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF334155)), // Slate-700
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    "Senin Cevabın:", 
                                    fontSize = 12.sp, 
                                    fontWeight = FontWeight.Bold, 
                                    color = Color(0xFF94A3B8) // Slate-400
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    userSpeech, 
                                    fontSize = 15.sp, 
                                    color = Color(0xFFE2E8F0), // Slate-200
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }

                    // AI Geri Bildirimi ve Puan
                    if (aiFeedback.isNotEmpty()) {
                        Spacer(Modifier.height(24.dp))

                        // Puan Rozeti
                        if (aiScore > -1) {
                            val scoreColor = when {
                                aiScore >= 85 -> Color(0xFF10B981) // Emerald-500
                                aiScore >= 70 -> Color(0xFFF59E0B) // Amber-500
                                else -> Color(0xFFEF4444) // Red-500
                            }
                            
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(90.dp)
                                    .background(scoreColor.copy(alpha = 0.1f), CircleShape)
                                    .border(4.dp, scoreColor, CircleShape)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "$aiScore", 
                                        color = scoreColor, 
                                        fontWeight = FontWeight.Black, 
                                        fontSize = 28.sp
                                    )
                                    Text(
                                        "PUAN", 
                                        color = scoreColor, 
                                        fontSize = 10.sp, 
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                        }

                        // Geri Bildirim Kartı
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)), // Emerald-900 (Dark)
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF059669)) // Emerald-600
                        ) {
                            Column(Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp)) // Emerald-400
                                    Spacer(Modifier.width(8.dp))
                                    Text("Öğretmen Notu", fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    aiFeedback, 
                                    fontSize = 15.sp, 
                                    lineHeight = 24.sp, 
                                    color = Color(0xFFD1FAE5) // Emerald-100
                                )
                            }
                        }

                        // Devam Butonu
                        Button(
                            onClick = { generateNewQuestion() },
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), // Blue-500
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Text(
                                "Sıradaki Soruya Geç", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.scale(-1f, 1f), tint = Color.White) 
                        }
                    }
                    
                    // Alt boşluk (Mikrofon butonu için yer)
                    Spacer(Modifier.height(100.dp))
                }
            }

            // --- MİKROFON BUTONU (MODERN - FLOATING) ---
            if (processingState != 2 && processingState != 3) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Dış Halka Animasyonu
                    if (isListening) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(pulseScale)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFEF4444).copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                        )
                        // İkinci Halka
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .scale(pulseScale * 0.9f)
                                .background(
                                    Color(0xFFEF4444).copy(alpha = 0.2f),
                                    CircleShape
                                )
                        )
                    }
                    
                    // Ana Mikrofon Butonu - Gradient
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(16.dp, CircleShape)
                            .background(
                                brush = if (isListening) 
                                    Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
                                else 
                                    Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))),
                                shape = CircleShape
                            )
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.5f),
                                        Color.White.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .clickable {
                                if (isListening) {
                                    speechRecognizer.stopListening()
                                    isListening = false
                                    processingState = 2
                                } else {
                                    if (!hasPermission) {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    } else {
                                        if (question.contains("Soru hazırlanıyor") || question.contains("Hata")) {
                                            generateNewQuestion()
                                        } else {
                                            speechRecognizer.startListening(speechIntent)
                                            isListening = true
                                            processingState = 1
                                            userSpeech = ""
                                            aiFeedback = ""
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Mikrofon",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    
                    // Etiket - Glassmorphism Kapsül
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 50.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E293B).copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text(
                            if (isListening) "🎙️ Dinleniyor..." else "🎤 Konuşmak için dokun",
                            color = if (isListening) Color(0xFFFCA5A5) else Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}