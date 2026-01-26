package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * Geçmiş LGS Soruları Ekranı
 * Yıl ve ders bazlı filtreleme ile geçmiş LGS sorularını çözme
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastLgsQuestionsScreen(
    navController: NavController,
    context: Context = LocalContext.current
) {
    val scope = rememberCoroutineScope()
    
    var selectedYear by remember { mutableIntStateOf(2024) }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var questions by remember { mutableStateOf<List<QuestionModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var showResults by remember { mutableStateOf(false) }
    var correctAnswers by remember { mutableIntStateOf(0) }
    var wrongAnswers by remember { mutableIntStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    var noQuestionsFound by remember { mutableStateOf(false) }

    val years = (2018..2024).toList().reversed()
    val subjects = listOf(
        "turkce" to "Türkçe",
        "matematik" to "Matematik",
        "fen" to "Fen Bilimleri",
        "sosyal" to "Sosyal Bilgiler",
        "ingilizce" to "İngilizce",
        "dkab" to "Din Kültürü"
    )

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
            var fetchedQuestions = QuestionRepository.getPastLgsQuestions(
                year = selectedYear,
                subject = selectedSubject,
                limit = 20
            )
            
            // Firestore boşsa, AI ile soru üret
            if (fetchedQuestions.isEmpty()) {
                val generator = AiQuestionGenerator()
                val subjectName = subjects.find { it.first == (selectedSubject ?: "turkce") }?.second ?: "Türkçe"
                
                fetchedQuestions = generator.generateFastBatch(
                    lesson = "$subjectName (LGS $selectedYear)",
                    count = 10,
                    level = EducationLevel.ORTAOKUL,
                    schoolType = SchoolType.ORTAOKUL_STANDARD,
                    grade = 8
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Geçmiş LGS Soruları") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Filtreler
            if (questions.isEmpty() && !isLoading) {
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "📚 Ders Seçin",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = WindowInsets.navigationBars.asPaddingValues()
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubject == null,
                            onClick = { selectedSubject = null },
                            label = { Text("Tüm Dersler") },
                            leadingIcon = {
                                Icon(Icons.Rounded.SelectAll, null, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    items(subjects) { (id, name) ->
                        FilterChip(
                            selected = selectedSubject == id,
                            onClick = { selectedSubject = id },
                            label = { Text(name) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Soru bulunamadı uyarısı
                if (noQuestionsFound) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                                "Soru bulunamadı. Lütfen farklı filtreler deneyin.",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { loadQuestions() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LGS Testini Başlat", fontWeight = FontWeight.Bold)
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
                        Text("LGS soruları hazırlanıyor...")
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
                        text = "🎓 LGS Testi Tamamlandı!",
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
                        OutlinedButton(
                            onClick = { 
                                questions = emptyList()
                                showResults = false
                            }
                        ) {
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
                    LgsHtmlText(
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
private fun LgsHtmlText(
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
