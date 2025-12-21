package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilgideham.ui.theme.AppThemeId
import com.example.bilgideham.ui.theme.EnergyPrimary
import com.example.bilgideham.ui.theme.ForestPrimary
import com.example.bilgideham.ui.theme.PrincessPrimary
import com.example.bilgideham.ui.theme.RoyalPrimary
import com.example.bilgideham.ui.theme.SpacePrimary
import com.example.bilgideham.ui.theme.SunsetPrimary

private data class ThemeItem(
    val id: AppThemeId,
    val title: String,
    val subtitle: String,
    val swatch: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(
    navController: NavController,
    currentTheme: AppThemeId,
    onSetTheme: (AppThemeId) -> Unit,
    darkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    val themes = listOf(
        ThemeItem(
            id = AppThemeId.ROYAL_ACADEMY,
            title = "Royal Academy",
            subtitle = "Asil, temiz ve kurumsal görünüm",
            swatch = RoyalPrimary
        ),
        ThemeItem(
            id = AppThemeId.CYBER_FUTURE,
            title = "Cyber Future",
            subtitle = "Neon, modern ve teknoloji odaklı",
            swatch = SpacePrimary
        ),
        ThemeItem(
            id = AppThemeId.HIDDEN_FOREST,
            title = "Hidden Forest",
            subtitle = "Doğal, huzurlu ve göz yormayan",
            swatch = ForestPrimary
        ),
        ThemeItem(
            id = AppThemeId.SUNSET_LOFI,
            title = "Sunset Lofi",
            subtitle = "Sıcak, yumuşak ve sanatsal",
            swatch = SunsetPrimary
        ),
        ThemeItem(
            id = AppThemeId.HIGH_ENERGY,
            title = "High Energy",
            subtitle = "Dinamik, hızlı ve canlı",
            swatch = EnergyPrimary
        ),
        ThemeItem(
            id = AppThemeId.FAIRY_TALE,
            title = "Fairy Tale",
            subtitle = "Masalsı, zarif ve renkli",
            swatch = PrincessPrimary
        )
    )

    val headerBrush = Brush.verticalGradient(
        colors = listOf(
            cs.primary,
            cs.tertiary.copy(alpha = 0.92f),
            cs.secondary.copy(alpha = 0.90f)
        )
    )

    Scaffold(
        containerColor = cs.background,
        topBar = {
            // Modern, HomeScreen ile hizalı header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(headerBrush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(4.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Görünüm Ayarları",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Tema ve gece modu yönetimi",
                            color = Color.White.copy(alpha = 0.90f),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Tema",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(14.dp))

            // Gece modu kartı (aktif çalışır: MaterialTheme darkColorScheme'e geçiş AppTheme katmanında)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cs.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (darkMode) cs.primary.copy(alpha = 0.18f) else cs.tertiary.copy(alpha = 0.18f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (darkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (darkMode) cs.primary else cs.tertiary
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Gece Modu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = cs.onSurface
                        )
                        Text(
                            "Uygulama arayüzünü soft dark moda alır",
                            fontSize = 12.sp,
                            color = cs.onSurface.copy(alpha = 0.65f)
                        )
                    }

                    Switch(
                        checked = darkMode,
                        onCheckedChange = { onToggleDarkMode() }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                "Tema Seçimi",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = cs.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Seçiminiz tüm ekranlarda tutarlı şekilde uygulanır.",
                fontSize = 12.sp,
                color = cs.onBackground.copy(alpha = 0.65f)
            )

            Spacer(Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 18.dp)
            ) {
                items(themes, key = { it.id.name }) { item ->
                    val selected = item.id == currentTheme

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(
                                width = if (selected) 2.dp else 1.dp,
                                color = if (selected) cs.primary else cs.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { onSetTheme(item.id) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) cs.primary.copy(alpha = 0.06f) else cs.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // swatch
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(item.swatch, CircleShape)
                                    .border(1.dp, Color.Black.copy(alpha = 0.10f), CircleShape)
                            )

                            Spacer(Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cs.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    item.subtitle,
                                    fontSize = 12.sp,
                                    color = cs.onSurface.copy(alpha = 0.65f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (selected) {
                                Surface(
                                    color = cs.primary,
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
