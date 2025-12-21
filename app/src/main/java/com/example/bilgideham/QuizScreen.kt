package com.example.bilgideham

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Locale

// Legacy underline char (harf harf alt çizgi yapan combining underline)
private const val LEGACY_UL = '\u0332'

private fun stripLegacyUnderlineAndMarkers(raw: String): String {
    if (raw.isEmpty()) return raw
    // 1) legacy combining underline karakterlerini sil
    var out = raw.replace(LEGACY_UL.toString(), "")
    // 2) varsa [[...]] markerlarını düz metne çevir (şıklar kesinlikle altı çizilmesin)
    out = out.replace(Regex("\\[\\[(.+?)]]"), "$1")
    return out
}

/**
 * Normal test standardı:
 * - Sadece soru kökünde "yanlıştır/değildir/söylenemez/hariç/..." gibi olumsuzluk ifadesini underline yap.
 * - Şıklarda underline ASLA uygulanmaz (şıklar düz metin).
 */
private fun buildQuestionAnnotatedString(rawQuestion: String): AnnotatedString {
    val q = stripLegacyUnderlineAndMarkers(rawQuestion)

    val cues = listOf(
        "yanlıştır", "yanlistir",
        "değildir", "degildir",
        "söylenemez", "soylenemez",
        "olamaz",
        "bulunmaz",
        "yoktur",
        "hariç", "haric",
        "hiçbir", "hicbir",
        "asla"
    )

    val lower = q.lowercase(Locale("tr", "TR"))
    val hit = cues.firstOrNull { lower.contains(it) } ?: return AnnotatedString(q)

    val idx = lower.indexOf(hit)
    if (idx < 0) return AnnotatedString(q)

    val before = q.substring(0, idx)
    val mid = q.substring(idx, idx + hit.length)
    val after = q.substring(idx + hit.length)

    return buildAnnotatedString {
        append(before)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(mid) }
        append(after)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavController,
    lessonTitle: String,
    questionCount: Int = 10,
    preLoadedQuestions: List<QuestionModel> = emptyList(),
    examDurationMinutes: Int = 0
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val TAG = "QUIZ_DEBUG"

    val cs = MaterialTheme.colorScheme

    // Dark mode otomatik olarak MaterialTheme.colorScheme'den gelir.
    // Bu ekranda hard-coded açık zemin renkleri kullanılmaz.

    // --- MOD KONTROLLERİ ---
    val isStructuredExam = remember(lessonTitle) {
        lessonTitle == "GENEL_DENEME" || lessonTitle == "MARATON"
    }

    val isRealExamMode = remember(examDurationMinutes, isStructuredExam) {
        examDurationMinutes > 0 || isStructuredExam || lessonTitle.contains("Deneme", ignoreCase = true)
    }

    val isParagraphMode = remember(lessonTitle) {
        lessonTitle.contains("Paragraf", ignoreCase = true)
    }

    val isInfiniteMode = remember(examDurationMinutes, preLoadedQuestions, isParagraphMode, isRealExamMode) {
        examDurationMinutes == 0 && preLoadedQuestions.isEmpty() && !isParagraphMode && !isRealExamMode
    }

    // --- STATE'LER ---
    var questions by remember { mutableStateOf<List<QuestionModel>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }

    var isLoading by remember { mutableStateOf(true) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var correctCount by remember { mutableIntStateOf(0) }
    var wrongCount by remember { mutableIntStateOf(0) }

    var showLessonTransitionDialog by remember { mutableStateOf(false) }
    var justFinishedLesson by remember { mutableStateOf("") }
    var nextLessonName by remember { mutableStateOf("") }

    var timeLeft by remember {
        mutableLongStateOf(if (examDurationMinutes > 0) examDurationMinutes * 60L else 0L)
    }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }

    // Aynı oturumda tekrar gelmesin diye sessionSeen
    val sessionSeen = remember { linkedSetOf<String>() }

    fun safeDocId(q: QuestionModel): String {
        // Tercihen repository docId (stabil). Yoksa soru metni bazlı fallback.
        return runCatching { QuestionRepository.computeDocIdForQuestion(q) }
            .getOrElse { (q.question ?: "").trim().lowercase(Locale.US) }
    }

    fun filterOutSeenAndSolved(
        list: List<QuestionModel>,
        solvedSet: Set<String>
    ): List<QuestionModel> {
        val out = ArrayList<QuestionModel>(list.size)
        for (q in list) {
            val id = safeDocId(q)
            if (id.isBlank()) continue
            if (id in solvedSet) continue
            if (id in sessionSeen) continue
            sessionSeen.add(id)
            out.add(q)
        }
        return out
    }

    // --- ŞIK KARIŞTIRMA ---
    fun shuffleOptions(q: QuestionModel): QuestionModel {
        // Not: QuestionModel option alanları bazı projelerde non-null (String) olarak tanımlı.
        // Bu nedenle burada null üretebilecek getOrNull vb. kullanmıyoruz; her şeyi String'e normalize ediyoruz.
        val baseOptions: List<String> = listOf(
            q.optionA ?: "",
            q.optionB ?: "",
            q.optionC ?: "",
            q.optionD ?: ""
        )

        val correctText: String = when (q.correctAnswer.uppercase(Locale.US)) {
            "A" -> baseOptions[0]
            "B" -> baseOptions[1]
            "C" -> baseOptions[2]
            "D" -> baseOptions[3]
            else -> baseOptions[0]
        }

        val allOptions: List<String> = baseOptions.shuffled()
        val newCorrectIndex = allOptions.indexOf(correctText)
        val newCorrectLetter = when (newCorrectIndex) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            else -> "A"
        }
        return q.copy(
            optionA = allOptions[0],
            optionB = allOptions[1],
            optionC = allOptions[2],
            optionD = allOptions[3],
            correctAnswer = newCorrectLetter
        )
    }

    suspend fun loadSolvedSet(): Set<String> {
        return try {
            HistoryRepository.getSolvedQuestionFps().toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    // --- VERİ YÜKLEME ---
    suspend fun loadInitialQuestions() {
        withContext(Dispatchers.IO) {
            try {
                withTimeout(45000L) {
                    runCatching {
                        LessonRepositoryLocal.init(context)
                        HistoryRepository.init(context)
                    }

                    val solved = loadSolvedSet()

                    if (preLoadedQuestions.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            val filtered = filterOutSeenAndSolved(preLoadedQuestions, solved)
                                .map { shuffleOptions(it) }
                            questions = filtered
                            isLoading = false
                            errorMessage = if (filtered.isEmpty()) "Bu cihazda çözülmemiş soru kalmadı." else null
                        }
                        return@withTimeout
                    }

                    if (isStructuredExam) {
                        Log.d(TAG, "Deneme sınavı oluşturuluyor: $lessonTitle")
                        val examType = if (lessonTitle == "MARATON") "MARATON" else "GENEL"
                        val structuredQ = QuestionRepository.createStructuredExam(examType)

                        val filtered = filterOutSeenAndSolved(structuredQ, solved)
                        val finalQs = if (filtered.isNotEmpty()) filtered else emptyList()

                        withContext(Dispatchers.Main) {
                            if (finalQs.isNotEmpty()) {
                                questions = finalQs.map { shuffleOptions(it) }
                                errorMessage = null
                            } else {
                                errorMessage = "Bu cihazda deneme için çözülmemiş soru kalmadı."
                            }
                            isLoading = false
                        }
                        return@withTimeout
                    }

                    val fetchLimit = if (isParagraphMode) questionCount else 12

                    var newQs = try {
                        QuestionRepository.getQuestionsFromFirestore(
                            lessonTitle = lessonTitle,
                            limit = fetchLimit
                        )
                    } catch (e: Exception) {
                        emptyList()
                    }

                    newQs = filterOutSeenAndSolved(newQs, solved)

                    // Eğer filtre sonrası boş kaldıysa AI ile top-up (tekrar göstermemek için)
                    if (newQs.isEmpty()) {
                        Log.d(TAG, "Veritabanında uygun soru yok (tekrar filtrelendi). AI top-up devrede...")
                        try {
                            val aiGenerator = AiQuestionGenerator()
                            val ai1 = aiGenerator.generateBatch(lessonTitle, 14)
                            val f1 = filterOutSeenAndSolved(ai1, solved)
                            newQs = f1

                            if (newQs.isEmpty()) {
                                val ai2 = aiGenerator.generateBatch(lessonTitle, 18)
                                val f2 = filterOutSeenAndSolved(ai2, solved)
                                newQs = f2
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "AI Üretim Hatası: ${e.message}")
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (newQs.isNotEmpty()) {
                            questions = newQs.map { shuffleOptions(it) }
                            errorMessage = null
                        } else {
                            errorMessage = "Bu cihazda çözülmemiş soru bulunamadı."
                        }
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Yükleme hatası: ${e.message}")
                withContext(Dispatchers.Main) {
                    errorMessage = "Bir hata oluştu: ${e.localizedMessage}"
                    isLoading = false
                }
            }
        }
    }

    fun loadMoreQuestions() {
        if (isLoadingMore) return
        scope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoadingMore = true }
            try {
                val solved = loadSolvedSet()

                var moreQs = try {
                    QuestionRepository.getQuestionsFromFirestore(
                        lessonTitle = lessonTitle,
                        limit = 12
                    )
                } catch (e: Exception) {
                    emptyList()
                }

                moreQs = filterOutSeenAndSolved(moreQs, solved)

                if (moreQs.isEmpty()) {
                    try {
                        val aiGenerator = AiQuestionGenerator()
                        val ai = aiGenerator.generateBatch(lessonTitle, 18)
                        moreQs = filterOutSeenAndSolved(ai, solved)
                    } catch (e: Exception) {
                        Log.e(TAG, "AI Ek Soru Hatası: ${e.message}")
                    }
                }

                val uniqueQs = moreQs
                    .filter { newQ -> questions.none { (it.question ?: "") == (newQ.question ?: "") } }
                    .map { shuffleOptions(it) }

                withContext(Dispatchers.Main) {
                    if (uniqueQs.isNotEmpty()) {
                        questions = questions + uniqueQs
                    }
                    isLoadingMore = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { isLoadingMore = false }
            }
        }
    }

    LaunchedEffect(Unit) { loadInitialQuestions() }

    LaunchedEffect(timeLeft, isLoading, isRealExamMode) {
        if (isRealExamMode && !isLoading && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    LaunchedEffect(currentQuestionIndex, isInfiniteMode) {
        if (isInfiniteMode && !isLoadingMore && questions.isNotEmpty()) {
            if (currentQuestionIndex >= questions.size - 2) loadMoreQuestions()
        }
    }

    // --- UI ---
    if (showLessonTransitionDialog) {
        ModernTransitionOverlay(
            finishedLesson = justFinishedLesson,
            nextLesson = nextLessonName,
            onContinue = {
                showLessonTransitionDialog = false
                currentQuestionIndex++
                selectedOption = null
                isAnswerChecked = false
            }
        )
    }

    val pageBg = cs.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBg)
            .statusBarsPadding()
    ) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = cs.primary)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (isStructuredExam) "Deneme Sınavı Hazırlanıyor..." else "Sorular Getiriliyor...",
                            color = cs.onBackground.copy(alpha = 0.70f),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            errorMessage != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Warning, null, tint = cs.error, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(errorMessage!!, color = cs.error, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                        ) {
                            Text("Geri Dön")
                        }
                    }
                }
            }

            questions.isNotEmpty() && currentQuestionIndex < questions.size -> {
                val currentQuestion = questions[currentQuestionIndex]

                val displayTitle = lessonTitle
                val countDisplay =
                    if (isInfiniteMode) "${currentQuestionIndex + 1}" else "${currentQuestionIndex + 1} / ${questions.size}"
                val progressVal = if (isInfiniteMode) 1f else (currentQuestionIndex + 1f) / questions.size

                // "Cevabı Kontrol Et" geri bildirimi:
                // - Paragraf ve Deneme/Maraton modlarında UYGULANMAYACAK.
                // - Sadece normal testte (real exam değil + paragraf değil) ve isAnswerChecked sonrası çalışacak.
                val feedbackEnabled = !isRealExamMode && !isParagraphMode && isAnswerChecked && selectedOption != null
                val isCorrectInPractice = feedbackEnabled && (selectedOption == currentQuestion.correctAnswer)
                val isWrongInPractice = feedbackEnabled && (selectedOption != currentQuestion.correctAnswer)

                ModernQuizHeader(
                    title = displayTitle,
                    countDisplay = countDisplay,
                    progress = progressVal,
                    timeLeftSeconds = timeLeft,
                    showTimer = isRealExamMode,
                    onBack = { navController.popBackStack() }
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = cs.surface),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(Modifier.padding(24.dp)) {
                                Text(
                                    text = buildQuestionAnnotatedString(currentQuestion.question ?: "..."),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cs.onSurface,
                                    lineHeight = 26.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    val options = listOf(
                        "A" to (currentQuestion.optionA ?: ""),
                        "B" to (currentQuestion.optionB ?: ""),
                        "C" to (currentQuestion.optionC ?: ""),
                        "D" to (currentQuestion.optionD ?: "")
                    ).filter { it.second.isNotEmpty() }

                    items(items = options, key = { it.first }) { opt ->
                        val isSelected = selectedOption == opt.first
                        val isCorrectOption = opt.first == currentQuestion.correctAnswer

                        // Geri bildirim kuralları:
                        // - Doğru cevap verildiyse: seçilen şık YEŞİL
                        // - Yanlış cevap verildiyse: doğru şık YEŞİL + yanlış seçilen KIRMIZI
                        val highlightCorrect =
                            (isCorrectInPractice && isSelected) ||
                                    (isWrongInPractice && isCorrectOption)

                        val highlightWrongSelected = isWrongInPractice && isSelected && !isCorrectOption

                        // Kurumsal UI: Feedback renkleri theme ile entegre (koyu/aydınlık uyumlu)
                        val correctBorder = Color(0xFF2E7D32)
                        val wrongBorder = Color(0xFFC62828)

                        val borderColor = when {
                            highlightCorrect -> correctBorder
                            highlightWrongSelected -> wrongBorder
                            isSelected -> cs.primary
                            else -> cs.outline.copy(alpha = 0.35f)
                        }

                        val bgColor = when {
                            highlightCorrect -> correctBorder.copy(alpha = 0.14f)
                            highlightWrongSelected -> wrongBorder.copy(alpha = 0.14f)
                            isSelected -> cs.primary.copy(alpha = 0.10f)
                            else -> cs.surface
                        }

                        val badgeBg = when {
                            highlightCorrect -> correctBorder
                            highlightWrongSelected -> wrongBorder
                            isSelected -> cs.primary
                            else -> cs.surfaceVariant
                        }

                        val badgeTextColor = when {
                            highlightCorrect || highlightWrongSelected || isSelected -> Color.White
                            else -> cs.onSurfaceVariant
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    if (isSelected || highlightCorrect || highlightWrongSelected) 2.dp else 1.dp,
                                    borderColor,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = !isAnswerChecked || isRealExamMode) {
                                    if (!isAnswerChecked || isRealExamMode) selectedOption = opt.first
                                },
                            colors = CardDefaults.cardColors(containerColor = bgColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(badgeBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        opt.first,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeTextColor
                                    )
                                }
                                Spacer(Modifier.width(16.dp))

                                // ŞIKLAR: underline KAPALI. Eski legacy underline/marker varsa sanitize edilip düz basılır.
                                Text(
                                    text = stripLegacyUnderlineAndMarkers(opt.second),
                                    color = cs.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item {
                        // KURAL: Normal testte, öğretmen notu sadece YANLIŞ cevapta görünecek.
                        // Paragraf ve Deneme alanlarında bu kural uygulanmaz.
                        if (isWrongInPractice) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = cs.surfaceVariant),
                                modifier = Modifier.padding(top = 16.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(
                                        "Öğretmen Notu:",
                                        fontWeight = FontWeight.Bold,
                                        color = cs.secondary
                                    )
                                    Text(
                                        (currentQuestion.explanation ?: "").trim(),
                                        fontSize = 14.sp,
                                        color = cs.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Doğru Cevap: ${currentQuestion.correctAnswer}",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(80.dp))
                    }
                }

                val isLastQuestion = currentQuestionIndex == questions.size - 1

                val buttonText = when {
                    isRealExamMode && isLastQuestion -> "Sınavı Bitir"
                    isRealExamMode -> "İşaretle ve Geç"
                    !isInfiniteMode && isLastQuestion && isAnswerChecked -> "Dersi Bitir"
                    !isAnswerChecked -> "Cevabı Kontrol Et"
                    else -> "Sonraki Soru"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(cs.surface)
                        .padding(20.dp)
                ) {
                    Button(
                        onClick = {
                            val isCorrect = selectedOption == currentQuestion.correctAnswer

                            if (selectedOption != null) {
                                scope.launch(Dispatchers.IO) {
                                    runCatching {
                                        HistoryRepository.saveAnswer(
                                            currentQuestion,
                                            selectedOption!!,
                                            currentQuestion.lesson ?: "Genel"
                                        )
                                        StatsManager(context).addResult(
                                            lessonRaw = currentQuestion.lesson ?: "Genel",
                                            correctDelta = if (isCorrect) 1 else 0,
                                            wrongDelta = if (isCorrect) 0 else 1
                                        )
                                    }
                                }
                                if (isRealExamMode && isCorrect) correctCount++
                                if (isRealExamMode && !isCorrect) wrongCount++
                            }

                            if (isRealExamMode) {
                                if (currentQuestionIndex + 1 < questions.size) {
                                    val nextQ = questions[currentQuestionIndex + 1]
                                    if (nextQ.lesson != currentQuestion.lesson) {
                                        justFinishedLesson = currentQuestion.lesson ?: ""
                                        nextLessonName = nextQ.lesson ?: ""
                                        showLessonTransitionDialog = true
                                    } else {
                                        currentQuestionIndex++
                                        selectedOption = null
                                    }
                                } else {
                                    val totalTime = (examDurationMinutes * 60) - timeLeft
                                    navController.navigate(
                                        "exam_result/$correctCount/$wrongCount/${questions.size}/${formatTime(totalTime)}"
                                    ) {
                                        popUpTo("practice_exam_screen") { inclusive = false }
                                    }
                                }
                            } else {
                                // Normal test akışı
                                if (!isAnswerChecked) {
                                    if (selectedOption != null) {
                                        isAnswerChecked = true
                                        if (isCorrect) correctCount++ else wrongCount++
                                    }
                                } else {
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                        selectedOption = null
                                        isAnswerChecked = false
                                    } else {
                                        navController.navigate("exam_result/$correctCount/$wrongCount/${questions.size}/00:00") {
                                            popUpTo("home") { inclusive = false }
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary),
                        enabled = selectedOption != null || (isAnswerChecked && !isRealExamMode)
                    ) {
                        if (isLoadingMore && isAnswerChecked && currentQuestionIndex == questions.size - 1) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(buttonText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernQuizHeader(
    title: String,
    countDisplay: String,
    progress: Float,
    timeLeftSeconds: Long,
    showTimer: Boolean,
    onBack: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Column(
        Modifier
            .background(cs.surface)
            .border(0.5.dp, cs.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = cs.onSurface)
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
                if (showTimer) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, modifier = Modifier.size(14.dp), tint = cs.onSurface.copy(alpha = 0.65f))
                        Spacer(Modifier.width(4.dp))
                        Text(formatTime(timeLeftSeconds), fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.65f))
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            Surface(color = cs.primary.copy(alpha = 0.14f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = countDisplay,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = cs.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (progress <= 1f) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = cs.primary,
                trackColor = cs.surfaceVariant
            )
        }
    }
}

@Composable
fun ModernTransitionOverlay(
    finishedLesson: String,
    nextLesson: String,
    onContinue: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Dialog(onDismissRequest = {}, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.70f)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier
                            .size(70.dp)
                            .background(cs.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DoneAll, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "$finishedLesson Tamamlandı!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = cs.onSurface
                    )
                    Spacer(Modifier.height(16.dp))
                    Divider(color = cs.outline.copy(alpha = 0.30f))
                    Spacer(Modifier.height(16.dp))
                    Text("Sıradaki Ders:", color = cs.onSurface.copy(alpha = 0.70f), fontSize = 14.sp)
                    Text(nextLesson, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = cs.primary)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = cs.primary)
                    ) {
                        Text("Devam Et", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
