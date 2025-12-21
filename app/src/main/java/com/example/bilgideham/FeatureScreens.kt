package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtlasScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    // Durumlar
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }

    // Scaffold: Geri tuşunu ve başlığı sayfanın tepesine sabitler
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Coğrafya Atlası 🌍", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFE0F2F1)) // Arkaplan: Çok açık Turkuaz/Yeşil
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- 1. SLOGAN KARTI ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFB2DFDB)), // Kart Rengi: Açık Teal
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color(0xFF00695C), // İkon Rengi: Koyu Yeşil
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dünyayı Keşfet! 🗺️",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C),
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Merak ettiğin ülke veya şehri yaz, senin için rehber olayım.",
                            fontSize = 12.sp,
                            color = Color(0xFF00695C).copy(0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- 2. MODERN ARAMA ALANI ---
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nereyi merak ediyorsun?") },
                placeholder = { Text("Örn: Japonya, Paris, Nil Nehri...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF00695C)) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00695C),
                    cursorColor = Color(0xFF00695C),
                    focusedLabelColor = Color(0xFF00695C)
                )
            )

            Spacer(Modifier.height(16.dp))

            // --- 3. AKSİYON BUTONU ---
            Button(
                onClick = {
                    if (loading) return@Button
                    val q = input.trim()
                    if (q.isBlank()) {
                        result = "Lütfen geçerli bir yer ismi yazınız."
                        return@Button
                    }
                    focusManager.clearFocus() // Klavyeyi kapat
                    loading = true
                    result = null

                    scope.launch {
                        // Mevcut yapındaki fonksiyonu çağırıyoruz
                        val text = try {
                            atlasLookupText(q)
                        } catch (e: Exception) {
                            "Bağlantı hatası oluştu. Lütfen tekrar dene."
                        }
                        result = text
                        loading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)) // Koyu Yeşil Buton
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Haritalar Açılıyor...")
                } else {
                    Icon(Icons.Default.Public, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Bilgileri Getir", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // --- 4. SONUÇ ALANI ---
            if (result != null) {
                Spacer(Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Public, null, tint = Color(0xFF00695C), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Keşif Raporu:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00695C),
                                fontSize = 14.sp
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(0.3f))
                        Text(
                            text = result!!,
                            color = Color(0xFF263238),
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}