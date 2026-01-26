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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccentCoachScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- DURUMLAR ---
    var targetSentence by remember { mutableStateOf("Loading new sentence...") }
    var isLoading by remember { mutableStateOf(true) }
    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog) {
        ReportContentDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { _, _ -> showReportDialog = false }
        )
    }

    var spokenText by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(-1) }
    var isListening by remember { mutableStateOf(false) }

    // Konu Listesi (Rastgelelik için)
    val topics = listOf("Family", "School", "Hobbies", "Animals", "Space", "Food", "Future", "Sports", "Holidays")

    // YENİ CÜMLE ÜRETİCİ (AI)
    fun generateSentence() {
        scope.launch {
            isLoading = true
            score = -1
            spokenText = ""

            val topic = topics.random()
            val prompt = """
                Task: Generate ONE simple English sentence for a 5th-grade student to practice pronunciation.
                Topic: $topic
                Level: A1-A2 (Simple grammar).
                Length: 5-10 words.
                Output: Just the sentence. Nothing else.
            """.trimIndent()

            targetSentence = aiGenerateText(prompt).replace("\"", "")
            isLoading = false
        }
    }

    // İlk açılışta cümle üret
    LaunchedEffect(Unit) {
        generateSentence()
    }

    // İzin
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    // Mikrofon Animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "mic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // Amerikan İngilizcesi
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
    }

    DisposableEffect(Unit) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() { isListening = false }
            override fun onError(error: Int) {
                isListening = false
                spokenText = "Anlaşılamadı (Try Again!)"
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    spokenText = matches[0]

                    // Puanlama Algoritması
                    val cleanTarget = targetSentence.lowercase().replace(Regex("[^a-z ]"), "")
                    val cleanSpoken = spokenText.lowercase().replace(Regex("[^a-z ]"), "")

                    val targetWords = cleanTarget.split(" ").filter { it.isNotBlank() }
                    val spokenWords = cleanSpoken.split(" ").filter { it.isNotBlank() }

                    if (targetWords.isNotEmpty()) {
                        val common = targetWords.intersect(spokenWords.toSet()).size
                        score = (common.toFloat() / targetWords.size.toFloat() * 100).toInt().coerceIn(0, 100)
                    } else {
                        score = 0
                    }
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        onDispose { speechRecognizer.destroy() }
    }

    // --- UI TASARIMI ---
    Scaffold(
        containerColor = Color(0xFFF0F4F8), // Header ile uyumlu açık gri/mavi ton
        topBar = {
            // Mavi Modern Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF42A5F5), Color(0xFF1976D2)) // Canlı Mavi Gradient
                        )
                    )
            ) {
                // Dekoratif Arka Plan Efektleri
                Box(modifier = Modifier.fillMaxSize()) {
                    // Sağ taraftaki büyük silik ikon
                    Icon(
                        imageVector = Icons.Default.GraphicEq, // Ses dalgası ikonu
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 20.dp, y = 10.dp)
                            .size(140.dp)
                            .rotate(-15f)
                    )
                    
                    // Rastgele noktalar
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        repeat(5) {
                            val radiusVal = (10..30).random().toFloat()
                            val xVal = size.width * (0.2f + kotlin.random.Random.nextFloat() * 0.8f)
                            val yVal = size.height * (0.1f + kotlin.random.Random.nextFloat() * 0.9f)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.1f),
                                radius = radiusVal,
                                center = androidx.compose.ui.geometry.Offset(xVal, yVal)
                            )
                        }
                    }
                }

                // Header İçeriği
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Geri Butonu
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }

                    // Başlıklar
                    Column {
                        Text(
                            text = "Aksan Koçu 🇬🇧",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 32.sp,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Telaffuzunu Mükemmelleştir",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    ) { p ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFF0F4F8), Color(0xFFE3F2FD))))
        ) {
            Column(
                modifier = Modifier
                    .padding(p)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // --- SLOGAN KARTI (Modernize) ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    border = BorderStroke(1.dp, Color(0xFFE3F2FD))
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F7FA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🗣️", fontSize = 24.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Sesini Kaydet, İngilizceyi Konuştur!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1565C0))
                            Spacer(Modifier.height(4.dp))
                            Text("Yapay zeka telaffuzunu anında puanlar.", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                // --- NATIVE SPEAKER MODE ---
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE3F2FD))
                        .border(1.dp, Color(0xFFBBDEFB), RoundedCornerShape(50))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Native Speaker Mode",
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "Aşağıdaki cümleyi sesli oku:",
                    color = Color(0xFF546E7A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                // --- CÜMLE KARTI (Büyük ve Gölgeli) ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Box {
                        ReportIconButton(
                            onClick = { showReportDialog = true },
                            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
                        )
                        Column(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color(0xFF1976D2))
                            Spacer(Modifier.height(16.dp))
                            Text("Öğretmen cümleyi hazırlıyor...", color = Color.Gray, fontSize = 13.sp)
                        } else {
                            Text(
                                text = targetSentence,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 32.sp,
                                color = Color(0xFF263238)
                            )
                        }

                        Spacer(Modifier.height(24.dp))

                        // Sıradaki Cümle Butonu (Outline yerine hafif mavi dolgu)
                        Button(
                            onClick = { generateSentence() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF5F9FF),
                                contentColor = Color(0xFF1976D2)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isLoading,
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sıradaki Cümle", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
             }

                // --- SONUÇ ALANI ---
                if (score != -1 && !isLoading) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (score >= 80) Color(0xFFE8F5E9) else if (score >= 50) Color(0xFFFFF3E0) else Color(0xFFFFEBEE)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, if (score >= 80) Color(0xFFA5D6A7) else if (score >= 50) Color(0xFFFFCC80) else Color(0xFFEF9A9A))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (score >= 80) "MÜKEMMEL! 🌟" else if (score >= 50) "GÜZEL ÇABA 👍" else "TEKRAR DENE 💪",
                                    fontWeight = FontWeight.Black,
                                    color = if (score >= 80) Color(0xFF2E7D32) else if (score >= 50) Color(0xFFEF6C00) else Color(0xFFC62828),
                                    fontSize = 18.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Score: $score",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black.copy(0.6f)
                                )
                            }
                            
                            Spacer(Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                color = if (score >= 80) Color(0xFF43A047) else if (score >= 50) Color(0xFFFF9800) else Color(0xFFE53935),
                                trackColor = Color.White
                            )
                            Spacer(Modifier.height(12.dp))

                            Text(
                                text = "Algılanan: \"$spokenText\"",
                                fontSize = 14.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.Black.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    Spacer(Modifier.height(20.dp))
                }

                // --- KAYIT BUTONU (Büyük ve Modern) ---
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .scale(pulseScale)
                            .shadow(16.dp, CircleShape, spotColor = if (isListening) Color.Red else Color.Blue)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isListening) listOf(Color(0xFFFF1744), Color(0xFFD50000))
                                    else listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                                )
                            )
                            .clickable {
                                if (isListening) {
                                    speechRecognizer.stopListening()
                                    isListening = false
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    speechRecognizer.startListening(intent)
                                    isListening = true
                                    score = -1
                                    spokenText = "Dinleniyor..."
                                }
                            }
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                            contentDescription = "Record",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (isListening) "Dinliyorum..." else "Bas ve Konuş",
                        color = if(isListening) Color(0xFFD32F2F) else Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                AiDisclaimerFooter(isDarkMode = false)
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}