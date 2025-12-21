package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TurkceParagrafGunlukScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxSize().background(cs.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        Text("Türkçe Paragraf", fontSize = 28.sp, color = cs.primary)
        Spacer(Modifier.height(10.dp))
        Text("Hedef: Günlük 20 Soru", fontSize = 16.sp, color = cs.onSurface)
        Spacer(Modifier.height(24.dp))

        Button(onClick = { navController.navigate("quiz_turkce_paragraf_gunluk") }) {
            Text("Başlat")
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = { navController.popBackStack() }) { Text("Geri") }
    }
}
