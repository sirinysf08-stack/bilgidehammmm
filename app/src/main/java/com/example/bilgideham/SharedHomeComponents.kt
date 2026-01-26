package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

/**
 * Shared UI Components for HomeScreen Drawer and Headers
 */

// ==================== BRAND WORDMARK ====================

@Composable
fun AnimatedBrandWordmark(
    modifier: Modifier = Modifier,
    darkMode: Boolean,
    compact: Boolean,
    brandTitle: String,
    onSecretTap: () -> Unit = {}
) {
    val titleSize = if (compact) 30.sp else 38.sp
    val aiSize = if (compact) 20.sp else 26.sp
    val infiniteTransition = rememberInfiniteTransition(label = "shimmerSystem")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing), RepeatMode.Restart),
        label = "shimmer"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(0.7f), Color.White, Color.White.copy(0.7f)),
        start = Offset(shimmerOffset * 1800f - 900f, 0f),
        end = Offset(shimmerOffset * 1800f, 400f)
    )
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = brandTitle,
                fontSize = titleSize,
                fontWeight = FontWeight.Black,
                style = TextStyle(brush = shimmerBrush),
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.width(12.dp))
            // GİZLİ ADMİN GİRİŞİ - AI badge'ine 5 kez tıkla
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSecretTap() }
                    .background(Color.White.copy(0.18f))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
                    .border(1.2.dp, Color.White.copy(0.3f), CircleShape)
            ) {
                Text(
                    text = "AI",
                    fontSize = aiSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00E5FF),
                    letterSpacing = 1.5.sp
                )
            }
        }
        if (!compact) {
            Text(
                text = "Cebindeki Bilge Yapay Zeka.",
                fontSize = 17.sp,
                color = Color.White.copy(0.85f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 2.dp, top = 6.dp)
            )
        }
    }
}

// ==================== STAR DUST EFFECT ====================

@Composable
fun HomeStarDustEffect(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "atmosphereLayer")
    val moveY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -250f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Restart),
        label = "snowMove"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val randomGen = Random(9999)
        repeat(70) {
            val xPos = randomGen.nextFloat() * size.width
            val startY = randomGen.nextFloat() * size.height
            val currentY = (startY + moveY) % size.height
            val drawY = if (currentY < 0) size.height + currentY else currentY
            drawCircle(
                color = color,
                radius = randomGen.nextFloat() * 3.5.dp.toPx() + 0.8.dp.toPx(),
                center = Offset(xPos, drawY),
                alpha = randomGen.nextFloat() * 0.9f
            )
        }
    }
}

// ==================== DRAWER COMPONENTS ====================

@Composable
fun DrawerSectionTitle(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Black,
        color = color,
        modifier = Modifier.padding(start = 24.dp, top = 28.dp, bottom = 10.dp),
        letterSpacing = 2.sp
    )
}

@Composable
fun ColorfulDrawerItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        selected = false,
        icon = { Icon(icon, null, tint = color, modifier = Modifier.size(28.dp)) },
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
        colors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(horizontal = 30.dp, vertical = 28.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 3.5.sp,
        color = MaterialTheme.colorScheme.onBackground.copy(0.55f)
    )
}

// ==================== DRAWER THEME SELECTOR ====================

@Composable
fun DrawerThemeSelector(
    context: Context,
    onThemeChanged: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }
    // Mevcut tercihler
    var selectedInterface by remember { mutableStateOf(AppPrefs.getInterfaceStyle(context)) }
    var selectedColor by remember { mutableStateOf(AppPrefs.getThemeColor(context)) }
    var isDarkMode by remember { mutableStateOf(AppPrefs.getDarkMode(context)) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp)) {
        // Ana buton
        Surface(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(16.dp),
            color = if (expanded) cs.primary.copy(alpha = 0.1f) else Color.Transparent
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Palette, null, tint = Color(0xFF78909C), modifier = Modifier.size(28.dp))
                Spacer(Modifier.width(12.dp))
                Text("Tema & Arayüz", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = cs.onSurface)
                Spacer(Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    null,
                    tint = cs.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        // Genişletilmiş içerik
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp)) {
                // Karanlık Mod Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            isDarkMode = !isDarkMode
                            AppPrefs.setDarkMode(context, isDarkMode)
                            onThemeChanged()
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                        null,
                        tint = if (isDarkMode) Color(0xFF5C6BC0) else Color(0xFFFFA000),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (isDarkMode) "Karanlık Mod" else "Aydınlık Mod",
                        fontSize = 14.sp,
                        color = cs.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = {
                            isDarkMode = it
                            AppPrefs.setDarkMode(context, it)
                            onThemeChanged()
                        },
                        modifier = Modifier.height(24.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                // Arayüz Stili
                Text(
                    "Arayüz Stili",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.bilgideham.ui.theme.InterfaceStyle.entries.forEach { style ->
                        val isSelected = style == selectedInterface
                        Surface(
                            onClick = {
                                selectedInterface = style
                                AppPrefs.setInterfaceStyle(context, style)
                                onThemeChanged()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) cs.primary.copy(alpha = 0.15f) else cs.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isSelected) BorderStroke(2.dp, cs.primary) else null,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(style.icon, fontSize = 20.sp)
                                Text(
                                    style.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) cs.primary else cs.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                // Tema Rengi
                Text(
                    "Tema Rengi",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = cs.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    com.example.bilgideham.ui.theme.ThemeColor.entries.forEach { color ->
                        val isSelected = color == selectedColor
                        val themeColor = Color(color.primaryLight)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(CircleShape)
                                .background(themeColor)
                                .then(
                                    if (isSelected) Modifier.border(3.dp, cs.onSurface, CircleShape)
                                    else Modifier
                                )
                                .clickable {
                                    selectedColor = color
                                    AppPrefs.setThemeColor(context, color)
                                    onThemeChanged()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(color.icon, fontSize = 16.sp)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

// ==================== PLAYFUL DRAWER ITEMS ====================

@Composable
fun PlayfulDrawerItem(icon: String, label: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // İkon Arka Planı
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun PlayfulSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(start = 32.dp, top = 24.dp, bottom = 8.dp)
    )
}
