package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordHuntScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // State
    var loading by remember { mutableStateOf(true) }
    var dailyWords by remember { mutableStateOf<List<DailyWord>>(emptyList()) }
    var historyVisible by remember { mutableStateOf(false) }
    var showQuizDialog by remember { mutableStateOf(false) }
    
    // Init Manager
    LaunchedEffect(Unit) {
        WordHuntManager.init(context)
        loading = true
        dailyWords = WordHuntManager.checkAndFetchDailyWords(context)
        loading = false
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // --- HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // Biraz daha yüksek, dropdown için
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                        )
                    )
            ) {
                // Background Decor
                Box(Modifier.align(Alignment.TopEnd).offset(x=20.dp, y=(-20).dp).size(100.dp).clip(CircleShape).background(Color.White.copy(0.1f)))
                Box(Modifier.align(Alignment.BottomStart).offset(x=(-20).dp, y=20.dp).size(80.dp).clip(CircleShape).background(Color.White.copy(0.1f)))

                // Geri Butonu
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(16.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(0.2f))
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                ) {
                    Text("Kelime Avı", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Kaç kelime bulabilirsin?", fontSize = 14.sp, color = Color.White.copy(0.9f))
                }
            }
        },
        floatingActionButton = {
            if(!historyVisible && !loading) {
                ExtendedFloatingActionButton(
                    onClick = { showQuizDialog = true },
                    containerColor = Color(0xFFFFCA28),
                    contentColor = Color(0xFF37474F),
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.SportsEsports, null)
                    Spacer(Modifier.width(8.dp))
                    Text("MİNİ TEST OL", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (loading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Main Content
                AnimatedVisibility(
                    visible = !historyVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            // Info Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("🔥", fontSize = 24.sp)
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text("Bugünün 5 Kelimesi", fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                                        Text(
                                            "${WordHuntManager.state.learnedCount} kelime ezberlendi!", 
                                            fontSize = 12.sp, 
                                            color = Color.Green.copy(0.6f) // Darker green needed ideally
                                        )
                                    }
                                }
                            }
                        }

                        items(dailyWords) { word ->
                            WordCard(word = word, onLearnedToggle = { 
                                scope.launch {
                                    WordHuntManager.markAsLearned(word.id, context)
                                    // Refresh logic usually requires observing state, 
                                    // but we modify object directly so recomposition might need trigger.
                                    // For simplicity in this structure:
                                    dailyWords = WordHuntManager.checkAndFetchDailyWords(context) 
                                }
                            })
                        }
                        
                        item {
                            Button(
                                onClick = { 
                                    loading = true
                                    scope.launch {
                                        val new = WordHuntManager.fetchMoreWords(context)
                                        dailyWords = WordHuntManager.state.allWords.takeLast(5) // Sadece yenileri göster veya hepsini? Kullanıcı +5 dedi, yenileri görsün.
                                        // Ama dailyWords state'i şu an sadece ekranda gösterilenleri tutuyor.
                                        // İstersek dailyWords'e ekleyebiliriz.
                                        // En iyisi dailyWords listesini güncellemek.
                                        // Fakat listede SADECE bugünün kelimeleri mi var?
                                        // checkAndFetchDailyWords sadece son 5'i döndürüyordu (ya da yenileri).
                                        // Biz burada sadece son 5'i (yeni gelenleri) gösterelim ki kullanıcı boğulmasın.
                                        if(new.isNotEmpty()) {
                                            dailyWords = new
                                        }
                                        loading = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(Modifier.width(8.dp))
                                Text("+5 Yeni Kelime", fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        item { Spacer(Modifier.height(80.dp)) } // FAB Space
                    }
                }

                // History Content
                AnimatedVisibility(
                    visible = historyVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val historyMap = remember(historyVisible) { WordHuntManager.getHistory() }
                    
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        historyMap.forEach { (date, words) ->
                            item {
                                Text(
                                    text = date,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF78909C),
                                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                                )
                            }
                            items(words) { word ->
                                WordCardMini(word)
                            }
                        }
                    }
                }
            }
        }

        // Quiz Dialog
        if (showQuizDialog) {
            QuizDialog(
                onDismiss = { showQuizDialog = false }
            )
        }
    }
}

@Composable
fun WordCard(word: DailyWord, onLearnedToggle: () -> Unit) {
    var flipped by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { flipped = !flipped },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
            // Checkbox for Learned
            IconButton(
                onClick = onLearnedToggle,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    if(word.isLearned) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    null,
                    tint = if(word.isLearned) Color(0xFF4CAF50) else Color(0xFFB0BEC5)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!flipped) {
                    Text(word.english, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    Text(word.pronunciation, fontSize = 14.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    Spacer(Modifier.height(8.dp))
                    Text("Türkçesi için dokun 👆", fontSize = 10.sp, color = Color.LightGray)
                } else {
                    Text(word.turkish, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                    Spacer(Modifier.height(8.dp))
                    Text("\"${word.exampleSentence}\"", fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun WordCardMini(word: DailyWord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.8f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(word.english, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                Text(word.turkish, fontSize = 12.sp, color = Color.Gray)
            }
            if(word.isLearned) {
                Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun QuizDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // 3 Soru Sor
    val questions = remember { WordHuntManager.getQuizWords(3) }
    var currentIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    
    // Cevap kontrolü için state
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }

    LaunchedEffect(currentIndex) {
        selectedOption = null
        showExplanation = false
    }
    
    // Özel Dialog Tasarımı
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- HEADER ---
                if (!isFinished) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kelime Testi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F) // Koyu Gri - Net Görünüm
                        )
                        
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE1F5FE), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${currentIndex + 1}/${questions.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0288D1)
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }

                // --- CONTENT ---
                if (questions.isEmpty()) {
                    Text("Test olacak kadar kelime birikmedi henüz! Biraz kelime avına çık.", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onDismiss) { Text("Tamam") }
                } 
                else if (isFinished) {
                    // SONUÇ EKRANI
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏆", fontSize = 40.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Test Tamamlandı!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Skorun: $score / ${questions.size}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF455A64)
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        Text("Harika!")
                    }
                } 
                else {
                    // SORU EKRANI
                    val currentWord = questions[currentIndex]
                    // Seçenekleri hatırla
                    val options = remember(currentWord) {
                        val allWords = WordHuntManager.state.allWords
                        val distractors = allWords.filter { it.id != currentWord.id }.shuffled().take(2).map { it.turkish }
                        (distractors + currentWord.turkish).shuffled()
                    }

                    Text(
                        text = "Bu kelimenin anlamı ne?",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = currentWord.english,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1565C0),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = currentWord.pronunciation,
                        fontSize = 16.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.Gray,
                        modifier = Modifier.alpha(0.8f)
                    )
                    
                    Spacer(Modifier.height(32.dp))

                    // SEÇENEKLER
                    options.forEach { option ->
                        val isSelected = (selectedOption == option)
                        val isCorrect = (option == currentWord.turkish)
                        
                        val btnColor = if (selectedOption != null) {
                            if (isCorrect) Color(0xFF4CAF50) // Yeşil
                            else if (isSelected) Color(0xFFEF5350) // Kırmızı
                            else Color(0xFFF5F5F5) // Gri
                        } else {
                            Color(0xFFF0F4F8) // Varsayılan açık gri/mavi
                        }
                        
                        val txtColor = if (selectedOption != null) {
                            if (isCorrect || isSelected) Color.White
                            else Color.Gray
                        } else {
                            Color(0xFF37474F) // Koyu Gri Yazı
                        }

                        Button(
                            onClick = {
                                if (selectedOption == null) {
                                    selectedOption = option
                                    if (isCorrect) {
                                        score++
                                        isAnswerCorrect = true
                                        // Öğrenildi işaretle
                                        scope.launch {
                                            WordHuntManager.markAsLearned(currentWord.id, context)
                                        }
                                    } else {
                                        isAnswerCorrect = false
                                    }
                                    showExplanation = true
                                    
                                    // 🛡️ P0: GlobalScope → scope (memory leak fix)
                                    scope.launch {
                                        kotlinx.coroutines.delay(1500)
                                        if (currentIndex < questions.size - 1) {
                                            currentIndex++
                                        } else {
                                            isFinished = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                            elevation = ButtonDefaults.buttonElevation(if(isSelected) 4.dp else 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option, 
                                    fontSize = 16.sp, 
                                    fontWeight = FontWeight.Bold,
                                    color = txtColor
                                )
                                if(selectedOption != null && isCorrect) {
                                    Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                                } else if(selectedOption != null && isSelected && !isCorrect) {
                                    Icon(Icons.Default.Cancel, null, tint = Color.White)
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    
                    if(selectedOption == null) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Vazgeç", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}