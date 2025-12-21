package com.example.bilgideham

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val scope = rememberCoroutineScope()

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedChar.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp)) {
                            Text("● Tarih Öğretmeni", color = Color(0xFF2E7D32), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(Color(0xFFF2EFE9))
        ) {

            // --- 1. KARAKTER SEÇİMİ ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(characters) { char ->
                    val isSelected = selectedChar == char
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { changeCharacter(char) }
                            .alpha(if (isSelected) 1f else 0.6f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(char.color)
                                .border(2.dp, if (isSelected) char.color else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char.name.first().toString(),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = char.name.split(" ").first(),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) char.color else Color.Gray
                        )
                    }
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.3f))

            // --- 2. SOHBET ALANI ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg, selectedChar.color)
                }
                if (isTyping) {
                    item {
                        Text(
                            "${selectedChar.name} yazıyor...",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            // --- 3. MESAJ YAZMA ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Merak ettiğin şeyi sor...") },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = selectedChar.color,
                        unfocusedBorderColor = Color.LightGray
                    )
                )

                FloatingActionButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMsg = inputText
                            messages.add(ChatMessage(userMsg, true))
                            inputText = ""
                            isTyping = true

                            scope.launch {
                                // --- GÜNCELLENEN DETAYLI ÖĞRETMEN PROMPTU ---
                                val prompt = """
                                    SEN: ${selectedChar.name}. (${selectedChar.title})
                                    MUHATAP: 5. Sınıf öğrencisi.
                                    
                                    GÖREVİN:
                                    Öğrencinin şu sorusuna cevap ver: "$userMsg"
                                    
                                    TON VE ÜSLUP KURALLARI:
                                    1. ${selectedChar.style}
                                    2. ÇOK ÖNEMLİ: Cevabın "Kısa" OLMASIN. Konuyu detaylıca, doyurucu bir şekilde anlat.
                                    3. Bilgileri maddeler halinde veya paragraflara bölerek düzenli ver.
                                    4. Öğrencinin konuyu tam anlaması için bol bol örnek ver.
                                    5. Üslubun tatlı, şefkatli, cesaretlendirici ve nazik olsun.
                                    6. Asla sıkıcı bir ansiklopedi gibi olma; hikayeleştirerek, sohbet eder gibi öğret.
                                """.trimIndent()

                                val response = aiGenerateText(prompt)
                                messages.add(ChatMessage(response, false))
                                isTyping = false
                            }
                        }
                    },
                    containerColor = selectedChar.color,
                    shape = CircleShape
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, themeColor: Color) {
    val align = if (msg.isUser) Alignment.End else Alignment.Start
    val bgColor = if (msg.isUser) themeColor.copy(alpha = 0.1f) else Color.White
    // Mesaj kutusunun şekli (Köşeler)
    val shape = if (msg.isUser) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = 1.dp,
            border = if (!msg.isUser) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)) else null,
            modifier = Modifier.widthIn(max = 300.dp) // Genişliği biraz artırdım ki uzun metinler rahat okunsun
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = 22.sp // Satır arasını biraz açtım, okuma kolaylığı için
            )
        }
    }
}