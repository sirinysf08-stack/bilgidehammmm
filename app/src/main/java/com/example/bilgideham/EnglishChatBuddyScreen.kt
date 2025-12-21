package com.example.bilgideham

import androidx.compose.foundation.BorderStroke // ✅ EKLENEN IMPORT (Hatayı çözen satır)
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Mesaj Modeli
data class EnglishMessage(
    val text: String,
    val isUser: Boolean,
    val correction: String? = null // Eğer hata varsa düzeltme buraya gelir
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishChatBuddyScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    // Sohbet Geçmişi
    var messages by remember { mutableStateOf(listOf(
        EnglishMessage("Hello! I am your English Buddy. 👋\nHow are you today?", false)
    )) }

    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    // Otomatik Kaydırma (Yeni mesaj gelince)
    LaunchedEffect(messages.size) {
        listState.animateScrollToItem(messages.size - 1)
    }

    fun sendMessage() {
        if (inputText.isBlank()) return
        val userMsg = inputText
        messages = messages + EnglishMessage(userMsg, true)
        inputText = ""
        isTyping = true
        focusManager.clearFocus() // Klavyeyi kapat (isteğe bağlı)

        scope.launch {
            // --- ÖZEL İNGİLİZCE ÖĞRETMENİ PROMPTU ---
            val prompt = """
                SEN: 5. Sınıf öğrencisi için eğlenceli bir İngilizce arkadaşısın (English Buddy).
                
                ÖĞRENCİ MESAJI: "$userMsg"
                
                GÖREVLERİN:
                1. Öğrencinin mesajına samimi, kısa ve basit bir İngilizce ile cevap ver (A1-A2 seviyesi).
                2. EĞER öğrenci gramer hatası yaptıysa, cevabının en altına [CORRECTION] etiketiyle düzeltilmiş halini yaz.
                3. Cevabın içinde mutlaka bir emoji kullan.
                4. Sohbeti devam ettirmek için basit bir soru sor.
                
                FORMAT ÖRNEĞİ:
                Great! I love football too! ⚽ Who is your favorite player?
                [CORRECTION] I love football too.
            """.trimIndent()

            val rawResponse = aiGenerateText(prompt)

            // Cevabı Ayrıştır (Düzeltme var mı?)
            val parts = rawResponse.split("[CORRECTION]")
            val aiReply = parts[0].trim()
            val aiCorrection = if (parts.size > 1) parts[1].trim() else null

            messages = messages + EnglishMessage(aiReply, false, aiCorrection)
            isTyping = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Profil Resmi (Avatar)
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE0F7FA),
                            modifier = Modifier.size(40.dp),
                            border = BorderStroke(1.dp, Color(0xFF00ACC1))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.SmartToy, null, tint = Color(0xFF006064))
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("English Buddy 🇬🇧", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Online • A1/A2 Level", fontSize = 12.sp, color = Color(0xFF00C853)) // Yeşil renk
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
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
                .background(Color(0xFFE0F2F1)) // Çok açık turkuaz arka plan (WhatsApp tarzı)
        ) {

            // --- MESAJ LİSTESİ ---
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    EnglishChatBubble(msg)
                }

                if (isTyping) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // --- MESAJ YAZMA ALANI ---
            Surface(
                color = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White, RoundedCornerShape(24.dp)),
                        placeholder = { Text("Type something in English...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF009688),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage() }),
                        maxLines = 3
                    )

                    Spacer(Modifier.width(8.dp))

                    // Gönder Butonu
                    FloatingActionButton(
                        onClick = { sendMessage() },
                        containerColor = Color(0xFF009688), // Teal rengi
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                }
            }
        }
    }
}

@Composable
fun EnglishChatBubble(msg: EnglishMessage) {
    val isUser = msg.isUser
    val align = if (isUser) Alignment.End else Alignment.Start

    // Renkler
    val bubbleColor = if (isUser) Color(0xFFB2DFDB) else Color.White // Kullanıcı: Açık Yeşil, AI: Beyaz
    val textColor = Color.Black
    val shape = if (isUser) RoundedCornerShape(18.dp, 18.dp, 2.dp, 18.dp)
    else RoundedCornerShape(18.dp, 18.dp, 18.dp, 2.dp)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        // İsim Etiketi (Opsiyonel)
        if (!isUser) {
            Text("Buddy", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
        }

        Surface(
            color = bubbleColor,
            shape = shape,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = msg.text,
                    fontSize = 16.sp,
                    color = textColor,
                    lineHeight = 22.sp
                )

                // Eğer düzeltme varsa (AI mesajında)
                if (msg.correction != null) {
                    Spacer(Modifier.height(8.dp))
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Doğrusu: ${msg.correction}",
                            fontSize = 12.sp,
                            color = Color(0xFFE65100), // Koyu Turuncu
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
        Text("Buddy yazıyor...", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    }
}