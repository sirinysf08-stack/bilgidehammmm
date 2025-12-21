package com.example.bilgideham

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// --- RENK PALETİ ---
val SuccessGreen = Color(0xFF43A047)
val ErrorRed = Color(0xFFE53935)
val WarningOrange = Color(0xFFFB8C00)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    // Silme onayı için Dialog durumu
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 1. Verileri Çekme
    val historyFlow = HistoryRepository.getAll() ?: flowOf(emptyList())
    val historyList by historyFlow.collectAsState(initial = emptyList())

    // 2. Kategoriler
    val categories = listOf("Tümü", "Matematik", "Fen", "Sosyal", "Türkçe", "İngilizce", "Din", "Paragraf")
    var selectedCategory by remember { mutableStateOf("Tümü") }

    // 3. Filtreleme
    val filteredList = remember(historyList, selectedCategory) {
        if (selectedCategory == "Tümü") historyList
        else historyList.filter {
            it.lesson.contains(selectedCategory, ignoreCase = true) ||
                    it.questionText.contains(selectedCategory, ignoreCase = true)
        }
    }

    // 4. İstatistik Hesaplama
    val totalCount = filteredList.size
    val correctCount = filteredList.count { it.isCorrect }
    val wrongCount = totalCount - correctCount
    val successRate = if (totalCount > 0) (correctCount * 100 / totalCount) else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Çözüm Geçmişi", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("${historyList.size} Toplam Kayıt", fontSize = 12.sp, color = cs.onPrimary.copy(0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = cs.onPrimary)
                    }
                },
                actions = {
                    if (historyList.isNotEmpty()) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, null, tint = cs.onPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.primary,
                    titleContentColor = cs.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA)) // Hafif gri modern zemin
        ) {
            // --- KATEGORİ SEÇİMİ ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cs.surface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = (category == selectedCategory)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = cs.primaryContainer,
                            selectedLabelColor = cs.onPrimaryContainer
                        ),
                        // DÜZELTME BURADA: selected parametresi eklendi
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent
                        )
                    )
                }
            }

            if (filteredList.isEmpty()) {
                // BOŞ EKRAN TASARIMI
                EmptyStateView(selectedCategory)
            } else {
                // --- İSTATİSTİK KARTI ---
                StatsHeader(totalCount, correctCount, wrongCount, successRate)

                // --- LİSTE ---
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList.reversed()) { item -> // En yeni en üstte
                        ModernHistoryCard(item)
                    }
                }
            }
        }
    }

    // SİLME ONAY DİALOGU
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Geçmişi Temizle") },
            text = { Text("Çözülen tüm sorular silinecek. Bu işlem geri alınamaz. Emin misin?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch { HistoryRepository.clearAll() }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = ErrorRed)
                ) { Text("Evet, Sil") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("İptal") }
            }
        )
    }
}

// --- BİLEŞENLER ---

@Composable
fun StatsHeader(total: Int, correct: Int, wrong: Int, rate: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Başarı Oranı", fontSize = 12.sp, color = Color.Gray)
                Text("%$rate", fontSize = 28.sp, fontWeight = FontWeight.Black, color = if(rate>=50) SuccessGreen else WarningOrange)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatItem(count = total, label = "Soru", color = Color.Black)
                StatItem(count = correct, label = "Doğru", color = SuccessGreen)
                StatItem(count = wrong, label = "Yanlış", color = ErrorRed)
            }
        }
    }
}

@Composable
fun StatItem(count: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun ModernHistoryCard(item: SolvedQuestionEntity) {
    val isCorrect = item.isCorrect
    val borderColor = if (isCorrect) SuccessGreen else ErrorRed
    val icon = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Ders ve Tarih
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(item.lesson, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if(isCorrect) SuccessGreen.copy(0.1f) else ErrorRed.copy(0.1f),
                        labelColor = if(isCorrect) SuccessGreen else ErrorRed
                    ),
                    border = null,
                    modifier = Modifier.height(26.dp)
                )

                Text(item.dateParams, fontSize = 11.sp, color = Color.Gray)
            }

            Spacer(Modifier.height(8.dp))

            // Soru Metni
            Text(
                text = item.questionText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF37474F),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(Modifier.height(12.dp))

            // Cevap Alanı
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = borderColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))

                Column {
                    if (isCorrect) {
                        Text("Tebrikler! Doğru Cevap.", fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    } else {
                        Row {
                            Text("Senin Cevabın: ", fontSize = 12.sp, color = Color.Gray)
                            Text(item.userAnswer, fontSize = 12.sp, color = ErrorRed, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            Text("Doğru Cevap: ", fontSize = 12.sp, color = Color.Gray)
                            Text(item.correctAnswer, fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(category: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = if(category == "Tümü") "Henüz çözülen soru yok." else "$category alanında kayıt bulunamadı.",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}