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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccentCoachScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- DURUMLAR ---
    var targetSentence by remember { mutableStateOf("Loading new sentence...") }
    var isLoading by remember { mutableStateOf(true) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aksan Koçu 🇬🇧", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFAFAFA))
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- YENİ EKLENEN SLOGAN KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA)) // Açık Turkuaz
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, null, tint = Color(0xFF00838F), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Slogan: Sesini Kaydet, İngilizceyi Konuştur!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF00838F))
                        Text("Yapay zeka, telaffuzunu anında puanlar ve seni ana diline yaklaştırır.", fontSize = 12.sp, color = Color(0xFF00838F).copy(alpha = 0.8f))
                    }
                }
            }

            // --- 1. ÜST BİLGİ ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Native Speaker Mode",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF1565C0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Aşağıdaki cümleyi sesli oku:",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // --- 2. CÜMLE KARTI ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color(0xFF2979FF))
                        Spacer(Modifier.height(16.dp))
                        Text("Yeni cümle yazılıyor...", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        Text(
                            text = targetSentence,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 34.sp,
                            color = Color(0xFF263238)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    // Cümle Değiştir Butonu
                    OutlinedButton(
                        onClick = { generateSentence() },
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(50),
                        enabled = !isLoading
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sıradaki Cümle", color = Color.Gray)
                    }
                }
            }

            // --- 3. SONUÇ ALANI ---
            if (score != -1 && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (score >= 80) Color(0xFFE8F5E9) else if (score >= 50) Color(0xFFFFF3E0) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (score >= 80) "MÜKEMMEL! 🌟" else if (score >= 50) "GÜZEL ÇABA 👍" else "TEKRAR DENE 💪",
                            fontWeight = FontWeight.Black,
                            color = if (score >= 80) Color(0xFF2E7D32) else if (score >= 50) Color(0xFFEF6C00) else Color(0xFFC62828),
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { score / 100f },
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                            color = if (score >= 80) Color(0xFF43A047) else Color(0xFFFF9800),
                            trackColor = Color.White
                        )
                        Spacer(Modifier.height(8.dp))

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
                Spacer(Modifier.height(80.dp))
            }

            // --- 4. KAYIT BUTONU ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (isListening) listOf(Color(0xFFFF1744), Color(0xFFFF5252))
                            else listOf(Color(0xFF2979FF), Color(0xFF448AFF))
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
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = if (isListening) "Dinliyorum..." else "Bas ve Konuş",
                modifier = Modifier.padding(top = 16.dp),
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}