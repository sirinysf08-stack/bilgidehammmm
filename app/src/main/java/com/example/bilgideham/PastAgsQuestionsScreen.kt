package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * Geçmiş AGS Soruları Ekranı
 * AGS (Adalet Görevde Yükselme Sınavı)
 * 1. Oturum: Tek tip sınav
 * 2. Oturum ÖABT: Alan/Branş bazlı sınavlar
 */

// Oturum seçenekleri - sealed class dışarıda tanımlanmalı
sealed class AgsSession(val title: String, val subtitle: String, val icon: String) {
    data object MebAgs : AgsSession("MEB AGS", "1. Oturum - Genel Kültür & Mevzuat", "📋")
    data object Oturum2Oabt : AgsSession("2. Oturum (ÖABT)", "Alan Bilgisi Testleri", "📚")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastAgsQuestionsScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {
    val scope = rememberCoroutineScope()
    
    // ÖABT Branşları (Öğretmenlik Alan Bilgisi Testi) - Artık kullanılmıyor, yeni ekrana yönlendiriliyor
    val oabtBranches = listOf(
        "turkce" to "Türkçe",
        "ilkogretim_mat" to "İlköğretim Matematik",
        "fen_bilimleri" to "Fen Bilimleri",
        "sosyal_bilgiler" to "Sosyal Bilgiler",
        "turk_dili_edebiyat" to "Türk Dili ve Edebiyatı",
        "tarih" to "Tarih",
        "cografya" to "Coğrafya",
        "matematik" to "Matematik",
        "fizik" to "Fizik",
        "kimya" to "Kimya / Kimya Teknolojisi",
        "biyoloji" to "Biyoloji",
        "din_kulturu" to "Din Kültürü ve Ahlak Bilgisi / İHL Meslek Dersleri",
        "rehberlik" to "Rehberlik",
        "sinif_ogretmenligi" to "Sınıf Öğretmenliği",
        "okul_oncesi" to "Okul Öncesi",
        "beden_egitimi" to "Beden Eğitimi"
    )
    
    // State
    var selectedSession by remember { mutableStateOf<AgsSession?>(null) }
    var selectedBranch by remember { mutableStateOf<String?>(null) }
    var selectedYear by remember { mutableIntStateOf(2024) }
    var questions by remember { mutableStateOf<List<QuestionModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var showResults by remember { mutableStateOf(false) }
    var correctAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    var noQuestionsFound by remember { mutableStateOf(false) }
    var showComingSoonDialog by remember { mutableStateOf(false) }

    val years = (2020..2024).toList().reversed()

    // Soruları yükle
    fun loadQuestions() {
        scope.launch {
            isLoading = true
            noQuestionsFound = false
            currentQuestionIndex = 0
            correctAnswers = 0
            wrongAnswers = 0
            showResults = false
            selectedAnswer = null
            showExplanation = false
            
            // Önce Firestore'dan dene
            var fetchedQuestions = QuestionRepository.getPastAgsQuestions(
                session = when (selectedSession) {
                    AgsSession.MebAgs -> "oturum1"
                    AgsSession.Oturum2Oabt -> "oturum2_oatb"
                    else -> "oturum1"
                },
                branch = selectedBranch,
                year = selectedYear,
                limit = 20
            )
            
            // Firestore boşsa, AI ile soru üret
            if (fetchedQuestions.isEmpty()) {
                val generator = AiQuestionGenerator()
                val sessionName = selectedSession?.title ?: "1. Oturum"
                val branchName = if (selectedSession is AgsSession.Oturum2Oabt) {
                    oabtBranches.find { it.first == selectedBranch }?.second ?: ""
                } else ""
                
                val lessonTitle = if (branchName.isNotEmpty()) {
                    "AGS $sessionName - $branchName ($selectedYear)"
                } else {
                    "AGS $sessionName ($selectedYear)"
                }
                
                fetchedQuestions = generator.generateFastBatch(
                    lesson = lessonTitle,
                    count = 10,
                    level = EducationLevel.KPSS,
                    schoolType = SchoolType.KPSS_LISANS,
                    grade = null
                )
            }
            
            questions = fetchedQuestions
            noQuestionsFound = fetchedQuestions.isEmpty()
            isLoading = false
        }
    }

    fun checkAnswer(answer: String) {
        if (selectedAnswer != null) return
        selectedAnswer = answer
        
        val currentQuestion = questions.getOrNull(currentQuestionIndex)
        if (currentQuestion != null) {
            if (answer == currentQuestion.correctAnswer) {
                correctAnswers++
            } else {
                wrongAnswers++
            }
            showExplanation = true
        }
    }

    fun nextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            selectedAnswer = null
            showExplanation = false
        } else {
            showResults = true
        }
    }
    
    fun resetToSessionSelection() {
        selectedSession = null
        selectedBranch = null
        questions = emptyList()
        showResults = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        when {
                            selectedSession == null -> "AGS Hazırlık"
                            selectedSession is AgsSession.Oturum2Oabt && selectedBranch == null -> "2. Oturum - Branş Seçin"
                            else -> selectedSession?.title ?: "AGS"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        when {
                            questions.isNotEmpty() -> {
                                questions = emptyList()
                                showResults = false
                            }
                            selectedBranch != null -> selectedBranch = null
                            selectedSession != null -> selectedSession = null
                            else -> navController.popBackStack()
                        }
                    }) {
                        Icon(Icons.Rounded.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        // Yapım Aşamasında Dialog
        if (showComingSoonDialog) {
            AlertDialog(
                onDismissRequest = { showComingSoonDialog = false },
                containerColor = Color.White,
                shape = RoundedCornerShape(28.dp),
                icon = {
                    Text("🚧", fontSize = 48.sp)
                },
                title = {
                    Text(
                        text = "Yapım Aşamasında",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "MEB AGS (1. Oturum) bölümü şu anda yapım aşamasındadır.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "✅ Şu an sadece 2. Oturum - Tarih dersi aktif!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showComingSoonDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300))
                    ) {
                        Text("Anladım 👍", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Oturum Seçimi
            if (selectedSession == null && questions.isEmpty() && !isLoading) {
                Text(
                    text = "⚖️ AGS Oturumu Seçin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Akademi Giriş Sınavı",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 1. Oturum Kartı - YAPIM AŞAMASINDA
                SessionCard(
                    session = AgsSession.MebAgs,
                    color = Color(0xFF1565C0),
                    isLocked = true,
                    onClick = { showComingSoonDialog = true }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 2. Oturum (ÖATB) Kartı - Yeni Alan Dersleri ekranına yönlendir
                SessionCard(
                    session = AgsSession.Oturum2Oabt,
                    color = Color(0xFF7B1FA2),
                    isLocked = false,
                    onClick = { 
                        // Yeni Alan Dersleri ekranına git
                        navController.navigate("ags_alan_dersleri")
                    }
                )
            }
            
            // ÖATB Branş Seçimi
            if (selectedSession is AgsSession.Oturum2Oabt && selectedBranch == null && questions.isEmpty() && !isLoading) {
                Text(
                    text = "📚 Branş/Alan Seçin",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = WindowInsets.navigationBars.asPaddingValues()
                ) {
                    items(oabtBranches) { (id, name) ->
                        Card(
                            onClick = { selectedBranch = id },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    Icons.Rounded.ChevronRight,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            
            // 1. Oturum veya ÖATB branş seçildiyse - Yıl seçimi ve test başlatma
            val showYearSelection = (selectedSession is AgsSession.MebAgs || 
                (selectedSession is AgsSession.Oturum2Oabt && selectedBranch != null)) && 
                questions.isEmpty() && !isLoading
            
            if (showYearSelection) {
                Text(
                    text = "📅 Yıl Seçin",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    years.forEach { year ->
                        FilterChip(
                            selected = selectedYear == year,
                            onClick = { selectedYear = year },
                            label = { Text(year.toString()) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Soru bulunamadı uyarısı
                if (noQuestionsFound) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.Warning, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Soru bulunamadı. AI ile üretilecek.",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = { loadQuestions() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("AGS Testini Başlat", fontWeight = FontWeight.Bold)
                }
            }

            // Loading
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("AGS soruları hazırlanıyor...")
                        Text(
                            "AI ile soru üretiliyor olabilir",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Sonuç ekranı
            if (showResults) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚖️ AGS Testi Tamamlandı!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = correctAnswers.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Doğru", color = MaterialTheme.colorScheme.primary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = wrongAnswers.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Yanlış", color = MaterialTheme.colorScheme.error)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    val percentage = if (questions.isNotEmpty()) 
                        (correctAnswers * 100 / questions.size) else 0
                    Text(
                        text = "%$percentage Başarı",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = { resetToSessionSelection() }) {
                            Text("Yeni Test")
                        }
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Ana Sayfa")
                        }
                    }
                }
            }

            // Soru gösterimi
            if (questions.isNotEmpty() && !showResults && !isLoading) {
                val currentQuestion = questions[currentQuestionIndex]

                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
                
                Text(
                    text = "Soru ${currentQuestionIndex + 1} / ${questions.size}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    AgsHtmlText(
                        html = currentQuestion.question,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val options = listOf(
                    "A" to currentQuestion.optionA,
                    "B" to currentQuestion.optionB,
                    "C" to currentQuestion.optionC,
                    "D" to currentQuestion.optionD
                )

                options.forEach { (letter, text) ->
                    val isCorrect = letter == currentQuestion.correctAnswer
                    val isSelected = letter == selectedAnswer
                    
                    val containerColor = when {
                        selectedAnswer != null && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                        isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        onClick = { checkAnswer(letter) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$letter)",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(text = text)
                        }
                    }
                }

                AnimatedVisibility(visible = showExplanation) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "💡 Açıklama",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = currentQuestion.explanation)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { nextQuestion() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (currentQuestionIndex < questions.size - 1) 
                                    "Sonraki Soru" else "Sonuçları Gör"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: AgsSession,
    color: Color,
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isLocked) Color.Gray.copy(alpha = 0.1f) else color.copy(alpha = 0.1f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (isLocked) Color.Gray.copy(alpha = 0.3f) else color.copy(alpha = 0.3f)
                )
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.icon,
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isLocked) Color.Gray else color
                    )
                    Text(
                        text = session.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                if (isLocked) {
                    Icon(
                        Icons.Rounded.Lock,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        Icons.Rounded.ChevronRight,
                        null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
        
        // Yapım Aşamasında Badge
        if (isLocked) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = Color(0xFFFFB300),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🚧", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Yakında",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun AgsHtmlText(
    html: String,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val annotatedString = remember(html) {
        buildAnnotatedString {
            var i = 0
            while (i < html.length) {
                when {
                    html.substring(i).startsWith("<u><b>", ignoreCase = true) -> {
                        val closeTag = html.indexOf("</b></u>", i, ignoreCase = true)
                        if (closeTag != -1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                                append(html.substring(i + 6, closeTag))
                            }
                            i = closeTag + 8
                        } else { append(html[i]); i++ }
                    }
                    html.substring(i).startsWith("<b><u>", ignoreCase = true) -> {
                        val closeTag = html.indexOf("</u></b>", i, ignoreCase = true)
                        if (closeTag != -1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                                append(html.substring(i + 6, closeTag))
                            }
                            i = closeTag + 8
                        } else { append(html[i]); i++ }
                    }
                    html.substring(i).startsWith("<b>", ignoreCase = true) -> {
                        val closeTag = html.indexOf("</b>", i, ignoreCase = true)
                        if (closeTag != -1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(html.substring(i + 3, closeTag))
                            }
                            i = closeTag + 4
                        } else { append(html[i]); i++ }
                    }
                    html.substring(i).startsWith("<u>", ignoreCase = true) -> {
                        val closeTag = html.indexOf("</u>", i, ignoreCase = true)
                        if (closeTag != -1) {
                            withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                                append(html.substring(i + 3, closeTag))
                            }
                            i = closeTag + 4
                        } else { append(html[i]); i++ }
                    }
                    html.substring(i).startsWith("**_") -> {
                        val closeTag = html.indexOf("_**", i + 3)
                        if (closeTag != -1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)) {
                                append(html.substring(i + 3, closeTag))
                            }
                            i = closeTag + 3
                        } else { append(html[i]); i++ }
                    }
                    else -> { append(html[i]); i++ }
                }
            }
        }
    }
    Text(text = annotatedString, style = style, modifier = modifier)
}
