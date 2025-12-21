package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class GameMenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color1: Color,
    val color2: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme

    val games = listOf(
        // Ana Oyunlar
        GameMenuItem(
            "Robo-Kodlama",
            "Algoritma mantığını geliştir!",
            Icons.Default.SmartToy,
            "robo_logic",
            Color(0xFF43A047), Color(0xFF66BB6A)
        ),
        GameMenuItem(
            "Matematik Rallisi",
            "Zamana karşı yarış! 100. Seviyeye ulaş.",
            Icons.Default.SportsScore,
            "math_rally",
            Color(0xFFD32F2F), Color(0xFFEF5350)
        ),
        // Ders Oyunları
        GameMenuItem("Fen Bilimleri", "Madde, Kuvvet, Canlılar.", Icons.Default.Science, "game_science", Color(0xFF3949AB), Color(0xFF5C6BC0)),
        GameMenuItem("Sosyal Bilgiler", "Tarih, Kültür, Haklar.", Icons.Default.Public, "game_social", Color(0xFFD81B60), Color(0xFFEC407A)),
        GameMenuItem("Türkçe Oyunu", "Dil bilgisi ve Sözcükler.", Icons.Default.MenuBook, "game_turkish", Color(0xFF00897B), Color(0xFF26A69A)),
        GameMenuItem("İngilizce Oyunu", "Kelime (Vocabulary) Testi.", Icons.Default.Translate, "game_english", Color(0xFFFB8C00), Color(0xFFFFB74D))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oyun Parkı 🎡", fontWeight = FontWeight.Bold, color = cs.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = cs.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cs.primary)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(cs.background)
                .padding(16.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(games) { game ->
                    GameCard(game) { navController.navigate(game.route) }
                }
            }
        }
    }
}

@Composable
fun GameCard(game: GameMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(game.color1, game.color2)))
                .padding(16.dp)
        ) {
            Icon(
                imageVector = game.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(game.icon, null, tint = Color.White, modifier = Modifier.size(32.dp))
                Column {
                    Text(text = game.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = game.description, fontSize = 11.sp, lineHeight = 14.sp, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }
    }
}