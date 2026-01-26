package com.example.bilgideham

import androidx.compose.foundation.BorderStroke 
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable // ✅ EKLENDI
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
import androidx.compose.ui.draw.rotate // ✅ EKLENDI
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Canvas
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

    // --- MODERN UI with Dark Mode Support ---
    val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()
    val bgColor = if (isDarkMode) Color(0xFF121212) else Color(0xFFF0F4F8)
    val inputSurfaceColor = if (isDarkMode) Color(0xFF1E1E1E) else Color.White
    val textFieldBgColor = if (isDarkMode) Color(0xFF2C2C2C) else Color(0xFFF5F5F5)
    val textColor = if (isDarkMode) Color.White else Color.Black
    val placeholderColor = if (isDarkMode) Color.Gray else Color.Gray

    Scaffold(
        containerColor = bgColor,
        topBar = {
            // Mavi Modern Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
                        )
                    )
            ) {
                // Dekoratif Efektler
                Box(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 20.dp, y = 10.dp)
                            .size(100.dp)
                            .rotate(-15f)
                    )

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        repeat(10) {
                            val radiusVal = (5..15).random().toFloat()
                            val xVal = size.width * (0.1f + kotlin.random.Random.nextFloat() * 0.9f)
                            val yVal = size.height * (0.1f + kotlin.random.Random.nextFloat() * 0.9f)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.15f),
                                radius = radiusVal,
                                center = androidx.compose.ui.geometry.Offset(xVal, yVal)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "English Buddy 🇬🇧",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Online",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(
                    if (isDarkMode) 
                        Brush.verticalGradient(listOf(Color(0xFF121212), Color(0xFF1E1E1E)))
                    else 
                        Brush.verticalGradient(listOf(Color(0xFFF0F4F8), Color(0xFFE1F5FE)))
                )
        ) {

            // --- MESAJ LİSTESİ ---
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(messages) { msg ->
                    EnglishChatBubble(msg, isDarkMode)
                }

                if (isTyping) {
                    item {
                        TypingIndicator(isDarkMode)
                    }
                }
            }

// --- MESAJ YAZMA ALANI ---
            Surface(
                color = inputSurfaceColor,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .background(textFieldBgColor, RoundedCornerShape(24.dp)),
                        placeholder = { Text("Bir şeyler yaz...", color = placeholderColor) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF42A5F5),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = textFieldBgColor,
                            unfocusedContainerColor = textFieldBgColor,
                            focusedTextColor = textColor, // FIX: Yazı rengi eklendi
                            unfocusedTextColor = textColor // FIX: Yazı rengi eklendi
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { sendMessage() }),
                        maxLines = 3
                    )

                    Spacer(Modifier.width(12.dp))

                    FloatingActionButton(
                        onClick = { sendMessage() },
                        containerColor = Color(0xFF1976D2),
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                }
                AiDisclaimerFooter(isDarkMode = isDarkMode)
                }
            }
        }
    }
}

@Composable
fun EnglishChatBubble(msg: EnglishMessage, isDarkMode: Boolean) {
    val isUser = msg.isUser
    val align = if (isUser) Alignment.End else Alignment.Start

    // Renkler - Modern Palette with Dark Mode
    val bubbleColor = if (isUser) {
        Color(0xFF1565C0) // User: Koyu Mavi
    } else {
        if (isDarkMode) Color(0xFF2C2C2C) else Color.White // Bot: Dark Gray or White
    }
    
    val textColor = if (isUser) {
        Color.White
    } else {
        if (isDarkMode) Color(0xFFE0E0E0) else Color(0xFF37474F) // Bot: Light Gray or Dark Gray
    }
    
    val shadowElevation = if (isUser) 2.dp else 4.dp // Bot mesajı biraz daha "kart" gibi dursun

    val shape = if (isUser) 
        RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    else 
        RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 20.dp)

    var showReportDialog by remember { mutableStateOf(false) }
    if (showReportDialog) {
        ReportContentDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { _, _ -> showReportDialog = false }
        )
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        // İsim Etiketi
        if (!isUser) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)) {
                Icon(Icons.Default.Face, null, tint = Color(0xFF1976D2), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Buddy", fontSize = 11.sp, color = if(isDarkMode) Color(0xFF90CAF9) else Color(0xFF546E7A), fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
             if (!isUser) {
                Surface(
                    color = bubbleColor,
                    shape = shape,
                    shadowElevation = shadowElevation,
                    modifier = Modifier.widthIn(max = 300.dp)
                ) {
                    Column(Modifier.padding(16.dp)) { // Padding arttırıldı
                        Text(
                            text = msg.text,
                            fontSize = 16.sp,
                            color = textColor,
                            lineHeight = 24.sp
                        )

                        // Eğer düzeltme varsa (AI mesajında)
                        if (msg.correction != null) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = if(isDarkMode) Color(0xFF424242) else Color(0xFFE0E0E0)) // Divider -> HorizontalDivider update in newer M3
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFFA000), modifier = Modifier.size(16.dp)) // Turuncu yıldız
                                Spacer(Modifier.width(6.dp))
                                Column {
                                     Text(
                                        text = "DÜZELTME:",
                                        fontSize = 10.sp,
                                        color = if(isDarkMode) Color(0xFFB0BEC5) else Color(0xFF78909C),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = msg.correction,
                                        fontSize = 14.sp,
                                        color = Color(0xFFD81B60), // Pembe vurgu
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.width(4.dp))
                ReportIconButton(onClick = { showReportDialog = true })
            } else {
                 Surface(
                    color = bubbleColor,
                    shape = shape,
                    shadowElevation = shadowElevation,
                    modifier = Modifier.widthIn(max = 300.dp)
                ) {
                    Column(Modifier.padding(16.dp)) { // Padding arttırıldı
                        Text(
                            text = msg.text,
                            fontSize = 16.sp,
                            color = textColor,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator(isDarkMode: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
        Text("Buddy yazıyor...", fontSize = 12.sp, color = if(isDarkMode) Color.Gray else Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    }
}