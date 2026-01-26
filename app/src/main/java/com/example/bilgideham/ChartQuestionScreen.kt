package com.example.bilgideham

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Modern Grafikli Soru Üretici
 * Toplu üretim (Batch Generation) desteğiyle
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChartQuestionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // States
    var selectedChartType by remember { mutableStateOf("bar") }
    var selectedGrade by remember { mutableIntStateOf(8) }
    var selectedSubject by remember { mutableStateOf("Matematik") }
    
    var isGenerating by remember { mutableStateOf(false) }
    var generationProgress by remember { mutableFloatStateOf(0f) }
    var generatedCount by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(5) }
    
    // Üretilen sorular listesi (En son üretilen en üstte)
    var generatedQuestions by remember { mutableStateOf<List<ChartQuestionModel>>(emptyList()) }
    var totalStats by remember { mutableIntStateOf(0) }
    
    // İstatistik Yükle
    LaunchedEffect(Unit) {
        totalStats = ChartQuestionRepository.getTotalCount()
    }
    
    val chartTypes = listOf(
        "bar" to "📊 Sütun",
        "line" to "📈 Çizgi",
        "pie" to "🥧 Pasta"
    )
    
    // Sınıf seviyesine göre dersler
    val subjects = when {
        selectedGrade <= 4 -> listOf("Matematik", "Hayat Bilgisi", "Türkçe")
        selectedGrade <= 8 -> listOf("Matematik", "Fen Bilgisi", "Sosyal Bilgiler", "Türkçe")
        else -> listOf("Matematik", "Fizik", "Kimya", "Biyoloji", "Coğrafya", "Tarih", "İstatistik")
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Grafik Soru Fabrikası", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("${totalStats} soru havuzda", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // KONFİGÜRASYON PANELİ
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // SEVİYE SEÇİMİ (Tabs)
                    Text("Okul Seviyesi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    
                    var selectedLevel by remember { mutableStateOf(if (selectedGrade <= 4) 0 else if (selectedGrade <= 8) 1 else 2) }
                    
                    PrimaryTabRow(
                        selectedTabIndex = selectedLevel,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                        indicator = {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(selectedLevel),
                                color = Color(0xFF1565C0)
                            )
                        }
                    ) {
                        listOf("İlkokul", "Ortaokul", "Lise").forEachIndexed { index, title ->
                            Tab(
                                selected = selectedLevel == index,
                                onClick = { 
                                    selectedLevel = index
                                    // Default grades when switching levels
                                    selectedGrade = when(index) {
                                        0 -> 4
                                        1 -> 8
                                        else -> 12
                                    }
                                },
                                text = { Text(title, fontSize = 13.sp, fontWeight = if(selectedLevel == index) FontWeight.Bold else FontWeight.Normal) },
                                unselectedContentColor = Color.Gray,
                                selectedContentColor = Color(0xFF1565C0)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // SINIF VE GRAFİK TİPİ SEÇİMİ
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Grade Selector (Available grades for level)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sınıf", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(8.dp))
                            
                            val availableGrades = when(selectedLevel) {
                                0 -> listOf(1, 2, 3, 4)
                                1 -> listOf(5, 6, 7, 8)
                                else -> listOf(9, 10, 11, 12)
                            }
                            
                            // Grade Chips in a Grid-like flow or just Row
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                availableGrades.forEach { grade ->
                                    FilterChip(
                                        selected = selectedGrade == grade,
                                        onClick = { selectedGrade = grade },
                                        label = { Text("$grade.") },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF1565C0),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                        
                        // Chart Type Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Grafik Tipi", fontSize = 12.sp, color = Color.Gray)
                            Spacer(Modifier.height(8.dp))
                            
                            val allChartTypes = chartTypes + ("random" to "🔀 Karma")
                            
                            OutlinedCard(
                                onClick = { 
                                    val currentIndex = allChartTypes.indexOfFirst { it.first == selectedChartType }
                                    val nextIndex = (currentIndex + 1) % allChartTypes.size
                                    selectedChartType = allChartTypes[nextIndex].first
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Box(modifier = Modifier.padding(12.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                     val label = allChartTypes.find { it.first == selectedChartType }?.second ?: "Bar"
                                     Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1565C0))
                                }
                            }
                            
                            Text(
                                if(selectedChartType == "random") "Her soru farklı tipte" else "Sabit grafik tipi", 
                                fontSize = 10.sp, 
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    
                    // Ders Seçimi (Horizontal Scroll)
                    Text("Ders", fontSize = 12.sp, color = Color.Gray)
                    Spacer(Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subjects) { subject ->
                            FilterChip(
                                selected = selectedSubject == subject,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject) },
                                leadingIcon = if (selectedSubject == subject) {
                                    { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE3F2FD),
                                    selectedLabelColor = Color(0xFF1565C0),
                                    selectedLeadingIconColor = Color(0xFF1565C0)
                                )
                            )
                        }
                    }
                }
            }
            
            // AKSİYON BUTONU VE PROGRESS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (isGenerating) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp, color = Color(0xFF1565C0))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Üretiliyor: $generatedCount / $targetCount",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { generationProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF43A047),
                                trackColor = Color(0xFFE0E0E0)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (!isGenerating) {
                                isGenerating = true
                                generatedCount = 0
                                generationProgress = 0f
                                
                                scope.launch {
                                    val newQuestions = ChartQuestionGenerator.generateBatchChartQuestions(
                                        context = context,
                                        chartType = selectedChartType,
                                        grade = selectedGrade,
                                        subject = selectedSubject,
                                        count = 5
                                    ) { current, total ->
                                        generatedCount = current
                                        generationProgress = current.toFloat() / total.toFloat()
                                    }
                                    
                                    newQuestions.forEach { q ->
                                        ChartQuestionRepository.saveQuestion(q)
                                    }
                                    
                                    generatedQuestions = newQuestions + generatedQuestions
                                    totalStats += newQuestions.size
                                    
                                    isGenerating = false
                                    Toast.makeText(context, "✅ ${newQuestions.size} soru üretildi ve kaydedildi!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        Icon(Icons.Default.AutoAwesome, null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "⚡ 5 ADET SORU ÜRET (AUTO-SAVE)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            // ÜRETİLEN SORULAR LİSTESİ
            if (generatedQuestions.isNotEmpty()) {
                Text(
                    "   Son Üretilenler (${generatedQuestions.size})",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(generatedQuestions) { question ->
                    GeneratedQuestionCard(question)
                }
                
                if (generatedQuestions.isEmpty() && !isGenerating) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Inventory2, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(Modifier.height(8.dp))
                            Text("Henüz yeni soru üretilmedi", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeneratedQuestionCard(question: ChartQuestionModel) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when(question.chartType) {
                            "pie" -> Icons.Default.PieChart
                            "line" -> Icons.Default.ShowChart
                            else -> Icons.Default.BarChart
                        },
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF2E7D32)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${question.grade}. Sınıf ${question.subject}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
                
                Surface(
                    color = Color(0xFF2E7D32),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "KAYDEDİLDİ",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    VegaLiteChartView(
                        vegaSpec = question.vegaSpec,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = question.question,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (expanded) {
                    Spacer(Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "A" to question.optionA,
                            "B" to question.optionB,
                            "C" to question.optionC,
                            "D" to question.optionD,
                            "E" to question.optionE
                        ).filter { it.second.isNotEmpty() }.forEach { (letter, text) ->
                            val isCorrect = letter == question.correctAnswer
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "$letter)", 
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color.Gray
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text,
                                    fontSize = 14.sp,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color.Black
                                )
                            }
                        }
                    }
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null,
                    tint = Color.Gray
                )
            }
        }
    }
}
