package com.example.bilgideham

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate // ✅ EKLENDI
import androidx.compose.ui.draw.scale  // ✅ EKLENDI
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush // ✅ EKLENDI
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// --- TARİHİ KARAKTERLER LİSTESİ (GÜNCELLENDİ: DAHA ÖĞRETİCİ) ---
data class HistoryCharacter(
    val name: String,
    val title: String,
    val color: Color,
    val intro: String,
    val style: String // AI Konuşma Tarzı
)

val characters = listOf(
    HistoryCharacter(
        "Fatih Sultan Mehmet", "Bilge Hükümdar", Color(0xFFD32F2F),
        "Merhaba küçük bilgin! İstanbul'u fethederken bilimi ve sanatı rehber edindim. Senin de öğrenme azgini görüyorum. Neyi merak ediyorsun?",
        "Çok bilgili, vizyoner ve nazik bir öğretmen gibi konuş. Tarihi olayları, bilimsel gerçekleri neden-sonuç ilişkisiyle detaylıca anlat. Öğrenciye 'evladım', 'küçük dostum' diye hitap et."
    ),
    HistoryCharacter(
        "Nasreddin Hoca", "Gülümseten Bilge", Color(0xFFFFA000),
        "Hoş geldin! Dünyanın maya tutması zor ama senin bilgi dağarcığın kolayca dolar. Gel seninle hem gülelim hem öğrenelim.",
        "Tatlı dilli, nüktedan ama çok öğretici konuş. Konuyu anlatırken araya mutlaka düşündürücü küçük hikayeler veya fıkra tadında örnekler sıkıştır. Detaylı bilgi ver ama sıkıcı olma."
    ),
    HistoryCharacter(
        "Mimar Sinan", "Büyük Usta", Color(0xFF795548),
        "Hoş geldin çırağım. Bir binanın sağlam olması için temeli, bir insanın başarılı olması için bilgisi sağlam olmalı. Sor bakalım, temeli atalım.",
        "Sabırlı, detaycı ve şefkatli bir usta gibi konuş. Konuları tane tane, mimari bir düzen içinde (maddeler halinde veya adım adım) detaylıca açıkla."
    ),
    HistoryCharacter(
        "Piri Reis", "Dünya Kâşifi", Color(0xFF1976D2),
        "Rüzgarımız kolayına olsun! Bu haritada keşfedilmemiş yer kalmadı ama senin zihninde keşfedilecek çok şey var. Nereye yelken açalım?",
        "Coğrafya, doğa ve keşif konularında heyecanlı ve detaylı bilgiler ver. Bir rehber gibi, öğrencinin gözünde canlanacak betimlemelerle anlat."
    ),
    HistoryCharacter(
        "Tomris Hatun", "Cesur Ana", Color(0xFFC2185B),
        "Hoş geldin. Güç sadece kılıçta değil, akıldadır. Bir lider gibi düşünmeyi öğrenmek ister misin? Seni dinliyorum.",
        "Güçlü ama bir o kadar da anaç bir tonla konuş. Tarihi ve stratejik bilgileri, hayat dersi verir gibi detaylandırarak anlat."
    ),
    HistoryCharacter(
        "Mevlana", "Sevgi Öğretmeni", Color(0xFF689F38),
        "Gel, gönül kapımız sana hep açık. Bilgi bir ışıktır, paylaştıkça çoğalır. Neyi aydınlatmamı istersin?",
        "Çok yumuşak, şiirsel ve sevgi dolu bir dille konuş. Konuları derinlemesine, insanlık ve erdem boyutuyla harmanlayarak, uzun ve açıklayıcı şekilde anlat."
    )
)

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryChatScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Kullanıcı seviyesini al
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userLevel = prefs.getString("education_level", "ORTAOKUL") ?: "ORTAOKUL"
    val userGrade = prefs.getInt("grade", 5)
    
    // Seviye açıklaması ve ton
    val (levelDescription, toneStyle) = when (userLevel) {
        "ILKOKUL" -> "4. sınıf öğrencisi" to "Çok tatlı, sevecen ve basit bir dil kullan. Kısa cümleler, anlaşılır kelimeler."
        "ORTAOKUL" -> when (userGrade) {
            5, 6 -> "${userGrade}. sınıf öğrencisi" to "Samimi ve arkadaşça bir dil kullan. Anlaşılır ama bilgilendirici ol."
            7, 8 -> "${userGrade}. sınıf öğrencisi" to "Daha olgun bir dil kullan. Detaylı ve düşündürücü cevaplar ver."
            else -> "5. sınıf öğrencisi" to "Samimi ve arkadaşça bir dil kullan."
        }
        "LISE" -> "${userGrade}. sınıf lise öğrencisi" to "Olgun ve akademik bir dil kullan. Derin analizler ve tarihsel bağlantılar kur."
        "KPSS" -> "KPSS adayı" to "Profesyonel ve akademik bir dil kullan. Detaylı tarihsel bilgiler ve analizler sun."
        "AGS" -> "Üniversite öğrencisi" to "Akademik ve profesyonel bir dil kullan. Kapsamlı tarihsel perspektifler sun."
        else -> "5. sınıf öğrencisi" to "Samimi ve arkadaşça bir dil kullan."
    }

    // Varsayılan Karakter: Nasreddin Hoca
    var selectedChar by remember { mutableStateOf(characters[1]) }

    var messages = remember { mutableStateListOf(ChatMessage(selectedChar.intro, false)) }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    fun changeCharacter(char: HistoryCharacter) {
        selectedChar = char
        messages.clear()
        messages.add(ChatMessage(char.intro, false))
    }

    // --- MODERN UI ---
    Scaffold(
        containerColor = Color(0xFFF0F4F8), // Açık Mavi-Gri Arka Plan
        topBar = {
            // Mavi Modern Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp) // Karakter isimleri için daha fazla alan
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF42A5F5), Color(0xFF1976D2)) // Canlı Mavi Gradient (Tüm app ile uyumlu)
                        )
                    )
            ) {
                // Dekoratif Efektler
                Box(modifier = Modifier.fillMaxSize()) {
                    // Sağ üstte silik ikon (Seçili karakterin tarzına göre değişebilir ama şimdilik standart History)
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 20.dp, y = -20.dp)
                            .size(120.dp)
                            .rotate(-15f)
                    )

                    // Stardust
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        repeat(10) {
                            val radiusVal = (5..15).random().toFloat()
                            val xVal = size.width * (0.1f + kotlin.random.Random.nextFloat() * 0.9f)
                            val yVal = size.height * (0.1f + kotlin.random.Random.nextFloat() * 0.9f)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.15f),
                                radius = radiusVal,
                                center = Offset(xVal, yVal)
                            )
                        }
                    }
                }

                // Header İçeriği
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    // Üst Bar (Geri ve Başlık)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                                text = selectedChar.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedChar.title,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // KARAKTER SEÇİMİ (Header'ın içinde, alt kısımda)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(characters) { char ->
                            val isSelected = selectedChar == char
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(80.dp)
                                    .clickable { changeCharacter(char) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 70.dp else 64.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.9f))
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .padding(3.dp)
                                        .clip(CircleShape)
                                        .background(char.color),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char.name.first().toString(),
                                        color = Color.White,
                                        fontSize = if (isSelected) 28.sp else 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = char.name.split(" ").first(),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                if (char.name.split(" ").size > 1) {
                                    Text(
                                        text = char.name.split(" ").drop(1).joinToString(" "),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White.copy(alpha = 0.9f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFF0F4F8), Color(0xFFE1F5FE))))
        ) {
            
            // --- SOHBET ALANI ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg, selectedChar.color)
                }
                if (isTyping) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                "${selectedChar.name} yazıyor...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }
            }

// --- MESAJ YAZMA ---
            Surface(
                color = Color.White,
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
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(24.dp)),
                        placeholder = { Text("Merak ettiğin şeyi sor...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedChar.color,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        maxLines = 3
                    )

                    Spacer(Modifier.width(12.dp))

                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val userMsg = inputText
                                messages.add(ChatMessage(userMsg, true))
                                inputText = ""
                                isTyping = true

                                scope.launch {
                                    val prompt = """
                                        SEN: ${selectedChar.name}. (${selectedChar.title})
                                        MUHATAP: $levelDescription
                                        
                                        GÖREVİN:
                                        Öğrencinin şu sorusuna cevap ver: "$userMsg"
                                        
                                        TON VE ÜSLUP KURALLARI:
                                        1. ${selectedChar.style}
                                        2. $toneStyle
                                        3. Cevabın detaylı ve doyurucu olsun.
                                        4. Öğrenciyle samimi bir bağ kur.
                                    """.trimIndent()

                                    val response = aiGenerateText(prompt)
                                    messages.add(ChatMessage(response, false))
                                    isTyping = false
                                }
                            }
                        },
                        containerColor = selectedChar.color,
                        contentColor = Color.White,
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(4.dp),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                }
                AiDisclaimerFooter(isDarkMode = false)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, themeColor: Color) {
    val align = if (msg.isUser) Alignment.End else Alignment.Start
    
    // Modern Renkler
    val bubbleColor = if (msg.isUser) themeColor else Color.White
    val textColor = if (msg.isUser) Color.White else Color(0xFF37474F)
    val shadowElevation = if (msg.isUser) 2.dp else 4.dp

    val shape = if (msg.isUser) 
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
        Row(verticalAlignment = Alignment.Bottom) {
            if (!msg.isUser) {
                Surface(
                    color = bubbleColor,
                    shape = shape,
                    shadowElevation = shadowElevation,
                    modifier = Modifier.widthIn(max = 300.dp)
                ) {
                    Text(
                        text = msg.text,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp,
                        color = textColor,
                        lineHeight = 22.sp
                    )
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
                    Text(
                        text = msg.text,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 15.sp,
                        color = textColor,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}