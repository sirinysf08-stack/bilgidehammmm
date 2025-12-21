package com.example.bilgideham

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.UUID
import kotlin.math.roundToInt

// --- DİNAMİK HEDEF AYARLARI ---
private const val BOOK_PAGE_COUNT = 64 // Baz alınan ortalama kitap sayfası
private const val DAILY_WEEKDAY_READ = 50 // Hafta içi günlük sayfa hedefi
private const val DAILY_WEEKEND_READ = 60 // Hafta sonu günlük sayfa hedefi

// --- VERİ MODELİ VE KAYIT İŞLEMLERİ ---
private const val BW_PREFS = "book_worm_prefs"
private const val BW_KEY = "books_json"

data class BookItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val totalPages: Int,
    val readPages: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

private fun loadBooks(context: Context): MutableList<BookItem> {
    val sp = context.getSharedPreferences(BW_PREFS, Context.MODE_PRIVATE)
    val json = sp.getString(BW_KEY, null) ?: return mutableListOf()
    return runCatching {
        val type = object : TypeToken<List<BookItem>>() {}.type
        Gson().fromJson<List<BookItem>>(json, type).toMutableList()
    }.getOrElse { mutableListOf() }
}

private fun saveBooks(context: Context, books: List<BookItem>) {
    val sp = context.getSharedPreferences(BW_PREFS, Context.MODE_PRIVATE)
    sp.edit().putString(BW_KEY, Gson().toJson(books)).apply()
}

// --- DİNAMİK HEDEF HESAPLAMA MANTIĞI ---
fun calculateAnnualTargetBooks(): Int {
    // 1 yıl = 52 hafta
    // Hafta içi gün sayısı: 52 * 5 = 260 gün
    // Hafta sonu gün sayısı: 52 * 2 = 104 gün

    val totalAnnualPages = (260 * DAILY_WEEKDAY_READ) + (104 * DAILY_WEEKEND_READ)

    // Yıllık Okuma Hedefi (Kitap Sayısı)
    return (totalAnnualPages.toFloat() / BOOK_PAGE_COUNT.toFloat()).roundToInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookWormScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current

    val books = remember { mutableStateListOf<BookItem>() }

    var showAddDialog by remember { mutableStateOf(false) }
    var editBookId by remember { mutableStateOf<String?>(null) }
    var deleteBookId by remember { mutableStateOf<String?>(null) }

    val annualTarget = remember { calculateAnnualTargetBooks() }

    LaunchedEffect(Unit) {
        books.clear()
        books.addAll(loadBooks(context))
    }

    fun persist() = saveBooks(context, books.toList())

    // --- ANALİZ HESAPLAMALARI ---
    val totalBooks = books.size
    val totalRead = books.sumOf { it.readPages.coerceAtLeast(0) }
    val totalPages = books.sumOf { it.totalPages.coerceAtLeast(0) }.coerceAtLeast(1)

    // Genel Sayfa İlerlemesi
    val progress = (totalRead.toFloat() / totalPages.toFloat()).coerceIn(0f, 1f)
    val percent = (progress * 100f).roundToInt().coerceIn(0, 100)

    // Kitap Hedefi İlerlemesi
    val booksProgress = (totalBooks.toFloat() / annualTarget.toFloat()).coerceIn(0f, 1f)
    val booksPercent = (booksProgress * 100).roundToInt().coerceIn(0, 100)


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kitap Kurdu 🦉", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Kitap Ekle", tint = Color(0xFFE65100))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBE9E7))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFBE9E7))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // --- 1. YILLIK HEDEF KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE65100)) // Koyu Turuncu
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "DİNAMİK YILLIK HEDEF",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$booksPercent%",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { booksProgress },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Tahmini Okuma: $totalBooks / $annualTarget kitap (64 sayfa baz alındı)",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // --- 2. SAYFA İSTATİSTİKLERİ (Row) ---
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Toplam Okunan",
                    value = totalRead.toString() + " sayfa",
                    icon = Icons.Default.Article,
                    color = Color(0xFF795548),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Genel İlerleme",
                    value = "$percent%",
                    icon = Icons.Default.StackedBarChart,
                    color = Color(0xFF4DB6AC),
                    modifier = Modifier.weight(1f)
                )
            }

            // --- 3. KİTAPLAR LİSTESİ BAŞLIĞI ---
            Text(
                "Okuma Listesi",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = cs.onBackground,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // --- 4. KİTAP LİSTESİ ---
            if (books.isEmpty()) {
                EmptyStateCard("Hadi ilk kitabını ekle! Okuma yolculuğun başlasın.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    books.forEach { b ->
                        BookItemCard(
                            book = b,
                            onEdit = { editBookId = b.id },
                            onDelete = { deleteBookId = b.id }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // --- EKLEME VE DÜZENLEME DİYALOĞLARI ---

    if (showAddDialog) {
        AddBookDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, totalPages, readPages ->
                val safeTotal = totalPages.coerceAtLeast(1)
                val safeRead = readPages.coerceIn(0, safeTotal)
                books.add(
                    BookItem(
                        title = title.trim(),
                        totalPages = safeTotal,
                        readPages = safeRead
                    )
                )
                persist()
                showAddDialog = false
            }
        )
    }

    editBookId?.let { id ->
        val book = books.firstOrNull { it.id == id }
        if (book != null) {
            EditBookDialog(
                book = book,
                onDismiss = { editBookId = null },
                onSave = { newTitle, newTotal, newRead ->
                    val idx = books.indexOfFirst { it.id == id }
                    if (idx >= 0) {
                        val safeTotal = newTotal.coerceAtLeast(1)
                        val safeRead = newRead.coerceIn(0, safeTotal)
                        books[idx] = book.copy(
                            title = newTitle.trim(),
                            totalPages = safeTotal,
                            readPages = safeRead,
                            updatedAt = System.currentTimeMillis()
                        )
                        persist()
                    }
                    editBookId = null
                }
            )
        } else {
            editBookId = null
        }
    }

    deleteBookId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteBookId = null },
            title = { Text("Kitap Silinsin mi?") },
            text = { Text("Bu kitabı listenizden tamamen kaldırmak istiyor musunuz?") },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = books.indexOfFirst { it.id == id }
                        if (idx >= 0) {
                            books.removeAt(idx)
                            persist()
                        }
                        deleteBookId = null
                    }
                ) { Text("Sil", color = Color.Red) }
            },
            dismissButton = {
                OutlinedButton(onClick = { deleteBookId = null }) { Text("Vazgeç") }
            }
        )
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, color = color)
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color.Black)
        }
    }
}

@Composable
fun BookItemCard(book: BookItem, onEdit: () -> Unit, onDelete: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val safeTotal = book.totalPages.coerceAtLeast(1)
    val safeRead = book.readPages.coerceIn(0, safeTotal)
    val progress = (safeRead.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
    val percent = (progress * 100f).roundToInt().coerceIn(0, 100)

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Kitap Adı
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF8D6E63)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Book, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(book.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
                }

                // Aksiyonlar
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, null, tint = cs.primary.copy(alpha = 0.7f))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.6f))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // İlerleme Çubuğu ve Yüzde
            Text(
                "$safeRead / $safeTotal sayfa (%$percent)",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
                color = if (progress == 1f) Color(0xFF4CAF50) else cs.primary,
                trackColor = cs.primary.copy(alpha = 0.18f)
            )
        }
    }
}

@Composable
fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(150.dp).padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("📖", fontSize = 48.sp)
            Text(message, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

// --- KİTAP EKLEME DİYALOĞU (MODERN ŞABLON) ---
@Composable
private fun AddBookDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, totalPages: Int, readPages: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var read by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Kitap Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Kitabın Adı") },
                    leadingIcon = { Icon(Icons.Default.Edit, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // Sayfa Alanları (Yan Yana)
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = total,
                        onValueChange = { total = it.filter { ch -> ch.isDigit() }; error = null },
                        label = { Text("Toplam Sayfa") },
                        leadingIcon = { Icon(Icons.Default.Article, null) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = read,
                        onValueChange = { read = it.filter { ch -> ch.isDigit() }; error = null },
                        label = { Text("Okunan (Ops.)") },
                        leadingIcon = { Icon(Icons.Default.ChevronRight, null) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val t = title.trim()
                    val tot = total.toIntOrNull()
                    val rd = read.toIntOrNull() ?: 0

                    if (t.isBlank()) { error = "Kitap adı yaz."; return@Button }
                    if (tot == null || tot <= 0) { error = "Toplam sayfa doğru olmalı."; return@Button }

                    onAdd(t, tot, rd)
                }
            ) { Text("Kaydet") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}

// --- KİTAP DÜZENLEME DİYALOĞU ---
@Composable
private fun EditBookDialog(
    book: BookItem,
    onDismiss: () -> Unit,
    onSave: (title: String, totalPages: Int, readPages: Int) -> Unit
) {
    var title by remember { mutableStateOf(book.title) }
    var total by remember { mutableStateOf(book.totalPages.toString()) }
    var read by remember { mutableStateOf(book.readPages.toString()) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kitabı Düzenle", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Kitap adı") },
                    leadingIcon = { Icon(Icons.Default.Edit, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it.filter { ch -> ch.isDigit() }; error = null },
                    label = { Text("Toplam sayfa") },
                    leadingIcon = { Icon(Icons.Default.Article, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = read,
                    onValueChange = { read = it.filter { ch -> ch.isDigit() }; error = null },
                    label = { Text("Okunan sayfa") },
                    leadingIcon = { Icon(Icons.Default.ChevronRight, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val t = title.trim()
                    val tot = total.toIntOrNull()
                    val rd = read.toIntOrNull()

                    if (t.isBlank()) { error = "Kitap adı yaz."; return@Button }
                    if (tot == null || tot <= 0) { error = "Toplam sayfa doğru olmalı."; return@Button }
                    if (rd == null || rd < 0) { error = "Okunan sayfa doğru olmalı."; return@Button }

                    onSave(t, tot, rd)
                }
            ) { Text("Kaydet") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Kapat") }
        }
    )
}