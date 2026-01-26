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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// --- MODERN RENK PALETİ ---
val IslamicGreen = Color(0xFF00695C) // Koyu Zümrüt
val IslamicGold = Color(0xFFFFD700)  // Altın
val SandBg = Color(0xFFFFF8E1)       // Kum Rengi (Arkaplan)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArabicCoachScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- DURUMLAR ---
    var arabicText by remember { mutableStateOf("...") }
    var turkishText by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var spokenText by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(-1) }
    var isListening by remember { mutableStateOf(false) }

    // 5. Sınıf Müfredat Konuları
    val topics = listOf("Selamlaşma", "Sayılar (1-10)", "Aile Üyeleri", "Okul Eşyaları", "Renkler", "Nezaket İfadeleri")

    // YENİ ARAPÇA İÇERİK ÜRETİCİ
    fun generateContent() {
        scope.launch {
            isLoading = true
            score = -1
            spokenText = ""
            arabicText = ""
            turkishText = ""

            val topic = topics.random()
            // PROMPT GÜNCELLENDİ: Sadece Arapça ve Anlam (Okunuş Yok)
            val prompt = """
                GÖREV: 5. Sınıf öğrencisi için basit bir Arapça kelime veya kısa cümle üret.
                KONU: $topic
                FORMAT: Arapça Metin | Türkçe Anlamı
                ÖRNEK ÇIKTI: السلام عليكم | Allah'ın selamı üzerine olsun
                
                Sadece yukarıdaki formatta tek bir satır ver. Latin harfleriyle okunuşunu YAZMA.
            """.trimIndent()

            try {
                val response = aiGenerateText(prompt)
                val parts = response.split("|")
                if (parts.size >= 2) {
                    arabicText = parts[0].trim()
                    turkishText = parts[1].trim()
                } else {
                    arabicText = "مرحبا"
                    turkishText = "Merhaba"
                }
            } catch (e: Exception) {
                arabicText = "Hata"
                turkishText = "Bağlantı Sorunu"
            }
            isLoading = false
        }
    }

    // İlk açılış
    LaunchedEffect(Unit) {
        generateContent()
    }

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
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar-SA") // Arapça
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
                spokenText = "Tekrar dener misin?"
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    spokenText = matches[0]
                    // Basit Puanlama Simülasyonu
                    // (Gerçek Arapça NLP mobilde zor olduğu için basit uzunluk/benzerlik kontrolü)
                    val targetLen = arabicText.length
                    val spokenLen = spokenText.length
                    val diff = kotlin.math.abs(targetLen - spokenLen)
                    score = if (diff < 4) (85..100).random() else (50..80).random()
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
                title = { Text("Arapça Hafız 🕌", fontWeight = FontWeight.Bold, color = IslamicGreen) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = IslamicGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SandBg)
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(SandBg, Color.White)))
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- 1. MOTİVASYON BAŞLIĞI ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Star, null, tint = IslamicGold, modifier = Modifier.size(32.dp))
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Lisan-ı Kuran",
                    color = IslamicGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // --- 2. ANA KART (MODERN) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .shadow(16.dp, RoundedCornerShape(32.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(2.dp, IslamicGold.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = IslamicGreen)
                        Spacer(Modifier.height(16.dp))
                        Text("Yeni kelime seçiliyor...", color = Color.Gray, fontSize = 12.sp)
                    } else {
                        // ARAPÇA METİN (Çok Büyük)
                        Text(
                            text = arabicText,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 60.sp,
                            color = IslamicGreen
                        )

                        Spacer(Modifier.height(24.dp))
                        Divider(color = IslamicGold.copy(alpha = 0.3f), thickness = 1.dp)
                        Spacer(Modifier.height(16.dp))

                        // TÜRKÇE ANLAM
                        Surface(
                            color = IslamicGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = turkishText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGreen,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // DEĞİŞTİR BUTONU
                    Button(
                        onClick = { generateContent() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                        shape = RoundedCornerShape(50),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Sıradaki Kelime", color = Color.Gray)
                    }
                }
            }

            // --- 3. SONUÇ ALANI ---
            if (score != -1 && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (score >= 80) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (score >= 80) "MAŞALLAH! ✨" else "GAYRET ET 💪",
                                fontWeight = FontWeight.Black,
                                color = if (score >= 80) IslamicGreen else Color(0xFFC62828),
                                fontSize = 22.sp
                            )
                        }

                        if (spokenText.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Algılanan yok ama çaban harika!", // Arapça STT zor olduğu için motive edici standart mesaj
                                fontSize = 12.sp,
                                color = Color.Gray.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            } else {
                Spacer(Modifier.height(80.dp))
            }

            // --- 4. KAYIT BUTONU (MODERN) ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .scale(pulseScale)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            if (isListening) listOf(Color(0xFFD32F2F), Color(0xFFEF5350))
                            else listOf(IslamicGreen, Color(0xFF26A69A))
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

            Text(
                text = if (isListening) "Dinliyorum..." else "Bas ve Oku",
                modifier = Modifier.padding(top = 16.dp),
                color = IslamicGreen,
                fontWeight = FontWeight.Medium
            )
        }
    }
}