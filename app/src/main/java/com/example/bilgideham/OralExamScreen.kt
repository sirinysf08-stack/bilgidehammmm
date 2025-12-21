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
        "Hz. Muhammed'in hayatı (Aile hayatı)", "Camiyi tanıyalım", "Dua ve sureler"
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

            // Konu Seçimi
            val lessonKey = if (selectedLesson == "Karma") grade5Curriculum.keys.random() else selectedLesson
            val topics = grade5Curriculum[lessonKey] ?: listOf("Genel Kültür")
            currentTopic = topics.random()

            val prompt = """
                Sen 5. Sınıf öğretmenisin. 
                DERS: $lessonKey
                KONU: $currentTopic
                
                GÖREV:
                Bu konuyla ilgili öğrencinin bilgisini ölçecek, düşündürücü TEK BİR sözlü sorusu sor.
                Soru çok uzun olmasın. Çocukların anlayacağı dilde olsun.
                Sadece soruyu yaz.
            """.trimIndent()

            question = aiGenerateText(prompt)
            processingState = 0
        }
    }

    // İlk açılışta değil, kullanıcı ders seçince başlasın diye bekletiyoruz.
    // Ancak varsayılan bir başlangıç için:
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
        topBar = {
            TopAppBar(
                title = { Text("Yapay Zeka Sözlüsü 🎓", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = { generateNewQuestion() }) {
                        Icon(Icons.Default.Refresh, "Yeni Soru")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFFAFAFA))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- DERS SEÇİMİ (YATAY KAYDIRMA) ---
            val lessons = listOf("Karma") + grade5Curriculum.keys.toList()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                items(lessons) { lesson ->
                    val isSelected = selectedLesson == lesson
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedLesson = lesson; generateNewQuestion() },
                        label = { Text(lesson) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // --- SORU KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (currentTopic.isNotEmpty()) "Konu: $currentTopic" else "Hazır mısın?",
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        lineHeight = 26.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- İÇERİK ALANI ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Durumlar
                if (processingState == 1) {
                    Text("Dinliyorum... 👂", color = Color(0xFFE91E63), fontWeight = FontWeight.Bold)
                } else if (processingState == 2) {
                    LinearProgressIndicator(modifier = Modifier.width(150.dp), color = MaterialTheme.colorScheme.tertiary)
                    Text("Öğretmen değerlendiriyor...", modifier = Modifier.padding(top = 8.dp), color = Color.Gray, fontSize = 12.sp)
                }

                // Cevap
                if (userSpeech.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Sen:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text(userSpeech, fontSize = 15.sp, fontStyle = FontStyle.Italic)
                        }
                    }
                }

                // AI Geri Bildirimi ve Puan
                if (aiFeedback.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))

                    // Puan Rozeti
                    if (aiScore > -1) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    if (aiScore >= 85) Color(0xFF43A047) // Yeşil (Pekiyi)
                                    else if (aiScore >= 70) Color(0xFFFFA000) // Turuncu (İyi)
                                    else Color(0xFFD32F2F) // Kırmızı (Gelişmeli)
                                )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$aiScore", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)
                                Text("PUAN", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Öğretmen Notu:", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Divider(Modifier.padding(vertical = 8.dp), color = Color.Black.copy(0.1f))
                            Text(aiFeedback, fontSize = 15.sp, lineHeight = 22.sp, color = Color(0xFF1B5E20))
                        }
                    }

                    // Devam Butonu
                    Button(
                        onClick = { generateNewQuestion() },
                        modifier = Modifier.padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Sıradaki Soruya Geç ->")
                    }
                }
            }

            // --- MİKROFON BUTONU ---
            if (processingState != 2 && processingState != 3) { // Düşünürken veya bitince gizle/değiştir
                Spacer(Modifier.height(16.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                if (isListening) listOf(Color(0xFFD32F2F), Color(0xFFFF5252))
                                else listOf(Color(0xFF1976D2), Color(0xFF42A5F5))
                            )
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
                                    if (question.contains("Soru hazırlanıyor")) {
                                        // Soru yoksa önce soru üret
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
                        }
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = "Mikrofon",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(if(isListening) "Dinliyorum..." else "Cevapla", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}