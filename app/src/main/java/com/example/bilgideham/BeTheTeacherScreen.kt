package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeTheTeacherScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var questionText by remember { mutableStateOf("") }
    var answerText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    fun generateTeacherSolution() {
        if (questionText.isBlank()) return
        isLoading = true
        focusManager.clearFocus()

        scope.launch {
            val prompt = """
                Rolün: 5. Sınıf öğrencisi olan bir "Öğretmen".
                Görev: Aşağıdaki soruyu sınıftaki diğer arkadaşlarına anlatıyormuş gibi, adım adım ve öğretici bir dille çöz.
                
                Soru: "$questionText"
                
                Kurallar:
                1. "Evet arkadaşlar..." gibi bir giriş yap.
                2. Cevabı doğrudan verme, nasıl bulunduğunu anlat.
                3. Türkçe karakterleri (ğ, ü, ş, i, ö, ç) düzgün kullan.
                4. Cesaretlendirici ve net bir dil kullan.
            """.trimIndent()

            answerText = aiGenerateText(prompt)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Öğretmen Sensin! 👩‍🏫", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cs.primaryContainer,
                    titleContentColor = cs.onPrimaryContainer
                )
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .background(cs.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. BAŞLIK KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cs.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.School, null, tint = cs.onTertiaryContainer, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Kürsü Senin!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = cs.onTertiaryContainer)
                        Text("Sorusunu hazırla, yapay zeka asistanın senin için çözüm anahtarını yazsın.", fontSize = 13.sp, color = cs.onTertiaryContainer.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- 2. MODERN SORU YAZMA ALANI ---
            Text(
                "Soru Hazırlama Paneli",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = cs.primary,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = cs.surface)
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Sorunu buraya yaz öğretmenim...") },
                    leadingIcon = { Icon(Icons.Default.Create, null, tint = cs.primary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .heightIn(min = 150.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cs.primary,
                        unfocusedBorderColor = cs.outlineVariant
                    ),
                    // ✅ DÜZELTME: Türkçe karakter ve çok satır desteği için ayarlar
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default // Enter ile alt satıra geçebilsin
                    ),
                    maxLines = 10
                )
            }

            Spacer(Modifier.height(24.dp))

            // --- 3. MODERN GRADIENT BUTON (Yapay Zeka Asistanı) ---
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF6200EA), Color(0xFFC51162)) // Mor -> Pembe Geçiş
            )

            Button(
                onClick = { generateTeacherSolution() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Biraz daha büyük ve iddialı
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Gradient için şeffaf
                contentPadding = PaddingValues(), // İç boşluğu sıfırla ki gradient taşsın
                enabled = questionText.isNotBlank() && !isLoading
            ) {
                // Gradient Arka Plan Kutusu
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "YAPAY ZEKA ASİSTANINA ÇÖZDÜR ✨",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- 4. ÇÖZÜM ALANI (Profesyonel Not) ---
            if (answerText.isNotBlank() && !isLoading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF59D)) // Not kağıdı sarısı
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, null, tint = Color(0xFFF57F17))
                            Spacer(Modifier.width(8.dp))
                            Text("ÇÖZÜM ANAHTARI", fontWeight = FontWeight.Black, color = Color(0xFFE65100), fontSize = 16.sp)
                        }

                        Divider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF9A825))

                        Text(
                            text = answerText,
                            fontSize = 16.sp,
                            color = Color(0xFF3E2723),
                            lineHeight = 24.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.align(Alignment.End)) {
                            Text("İmza: Öğretmen Furkan", fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                    }
                }
            }
        }
    }
}