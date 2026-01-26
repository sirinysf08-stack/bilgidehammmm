// SettingsScreen.kt
package com.example.bilgideham

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bilgideham.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val cs = MaterialTheme.colorScheme
    val context = LocalContext.current

    // Prefs
    val prefs = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // State - Kişiselleştirme
    var studentName by remember { mutableStateOf(prefs.getString(KEY_STUDENT_NAME, "") ?: "") }
    var styleWord by remember { mutableStateOf(prefs.getString(KEY_BRAND_STYLE, DEFAULT_STYLE) ?: DEFAULT_STYLE) }

    // State - Tema
    var selectedInterface by remember { mutableStateOf(AppPrefs.getInterfaceStyle(context)) }
    var selectedColor by remember { mutableStateOf(AppPrefs.getThemeColor(context)) }
    var isDarkMode by remember { mutableStateOf(AppPrefs.getDarkMode(context)) }

    // UI helpers
    var styleMenuExpanded by remember { mutableStateOf(false) }
    var savedToast by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf("Kaydedildi!") }

    val styleOptions = remember {
        listOf("Küpü", "Dünyası", "Kulübü", "Atölyesi", "Akademisi")
    }

    val previewTitle = remember(studentName, styleWord) {
        buildBrandTitle(studentName, styleWord, fallback = "Akıl Küpü")
    }

    // Kozmik tema kaldırıldı - her zaman false
    val isCosmic = false
    val bgColor = cs.background

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Ayarlar",
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isCosmic) Color.White else cs.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Rounded.ArrowBack, 
                            contentDescription = "Geri",
                            tint = if (isCosmic) Color.White else cs.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isCosmic) Color(0xFF0A0E17) else cs.surface
                )
            )
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ==================== ARAYÜZ STİLİ ====================
            SettingsSectionHeader(
                icon = Icons.Rounded.Palette,
                title = "Arayüz Stili",
                subtitle = "Uygulamanın görünümünü seçin",
                isCosmic = isCosmic
            )
            
            InterfaceStyleSelector(
                selectedStyle = selectedInterface,
                onStyleSelected = { style ->
                    selectedInterface = style
                    AppPrefs.setInterfaceStyle(context, style)
                },
                isCosmic = isCosmic
            )
            
            // ==================== TEMA RENGİ ====================
            SettingsSectionHeader(
                icon = Icons.Rounded.ColorLens,
                title = "Tema Rengi",
                subtitle = "Ana renk paletini seçin",
                isCosmic = isCosmic
            )
            
            ThemeColorSelector(
                selectedColor = selectedColor,
                onColorSelected = { color ->
                    selectedColor = color
                    AppPrefs.setThemeColor(context, color)
                },
                isCosmic = isCosmic
            )
            
            // ==================== KARANLIK MOD ====================
            DarkModeToggle(
                isDarkMode = isDarkMode,
                onToggle = { dark ->
                    isDarkMode = dark
                    AppPrefs.setDarkMode(context, dark)
                },
                isCosmic = isCosmic
            )
            
            // ==================== SEVİYE DEĞİŞTİR ====================
            LevelChangeCard(
                context = context,
                onLevelChange = { navController.navigate("level_selection") },
                isCosmic = isCosmic
            )
            
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ==================== UI BİLEŞENLERİ ====================

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isCosmic: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val textColor = if (isCosmic) Color.White else cs.onSurface
    val subtitleColor = if (isCosmic) Color.White.copy(alpha = 0.6f) else cs.onSurface.copy(alpha = 0.6f)
    val iconBgColor = if (isCosmic) Color(0xFF00F5FF).copy(alpha = 0.15f) else cs.primary.copy(alpha = 0.12f)
    val iconTint = if (isCosmic) Color(0xFF00F5FF) else cs.primary
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = iconBgColor
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(10.dp).size(24.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textColor)
            Text(subtitle, color = subtitleColor, fontSize = 12.sp)
        }
    }
}

@Composable
private fun InterfaceStyleSelector(
    selectedStyle: InterfaceStyle,
    onStyleSelected: (InterfaceStyle) -> Unit,
    isCosmic: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val cardBg = if (isCosmic) Color.White.copy(alpha = 0.08f) else cs.surface
    val textColor = if (isCosmic) Color.White else cs.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (isCosmic) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 2x2 Grid for styles
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InterfaceStyle.entries.take(2).forEach { style ->
                        StyleCard(
                            style = style,
                            isSelected = style == selectedStyle,
                            onClick = { onStyleSelected(style) },
                            isCosmic = isCosmic,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InterfaceStyle.entries.drop(2).forEach { style ->
                        StyleCard(
                            style = style,
                            isSelected = style == selectedStyle,
                            onClick = { onStyleSelected(style) },
                            isCosmic = isCosmic,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Seçili stilin özellikleri
            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isCosmic) Color(0xFF00F5FF).copy(alpha = 0.1f) else cs.primary.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StyleFeatureChip("Köşe: ${InterfaceParams.getCornerRadius(selectedStyle)}dp", isCosmic)
                    StyleFeatureChip("Gölge: ${InterfaceParams.getCardElevation(selectedStyle)}dp", isCosmic)
                    StyleFeatureChip(if (InterfaceParams.useAnimations(selectedStyle)) "Animasyonlu" else "Statik", isCosmic)
                }
            }
        }
    }
}

@Composable
private fun StyleCard(
    style: InterfaceStyle,
    isSelected: Boolean,
    onClick: () -> Unit,
    isCosmic: Boolean,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val cornerRadius = InterfaceParams.getCornerRadius(style).dp
    
    val bgColor = when {
        isSelected && isCosmic -> Color(0xFF00F5FF).copy(alpha = 0.15f)
        isSelected -> cs.primary.copy(alpha = 0.1f)
        isCosmic -> Color.White.copy(alpha = 0.05f)
        else -> cs.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val borderColor = when {
        isSelected && isCosmic -> Color(0xFF00F5FF)
        isSelected -> cs.primary
        else -> Color.Transparent
    }
    
    val textColor = when {
        isSelected && isCosmic -> Color(0xFF00F5FF)
        isSelected -> cs.primary
        isCosmic -> Color.White
        else -> cs.onSurface
    }

    Card(
        modifier = modifier
            .clickable { onClick() }
            .then(
                if (isSelected) Modifier.border(2.dp, borderColor, RoundedCornerShape(cornerRadius))
                else Modifier
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(style.icon, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                style.displayName,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
            Text(
                style.description,
                fontSize = 10.sp,
                color = textColor.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun StyleFeatureChip(text: String, isCosmic: Boolean = false) {
    val cs = MaterialTheme.colorScheme
    val bgColor = if (isCosmic) Color.White.copy(alpha = 0.1f) else cs.surface
    val textColor = if (isCosmic) Color.White.copy(alpha = 0.7f) else cs.onSurface.copy(alpha = 0.7f)
    
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = textColor
        )
    }
}

@Composable
private fun ThemeColorSelector(
    selectedColor: ThemeColor,
    onColorSelected: (ThemeColor) -> Unit,
    isCosmic: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val cardBg = if (isCosmic) Color.White.copy(alpha = 0.08f) else cs.surface
    val textColor = if (isCosmic) Color.White else cs.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (isCosmic) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ThemeColor.entries) { color ->
                    val isSelected = color == selectedColor
                    val themeColor = Color(color.primaryLight)

                    Column(
                        modifier = Modifier
                            .clickable { onColorSelected(color) }
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(themeColor)
                                .then(
                                    if (isSelected) Modifier.border(
                                        3.dp, if (isCosmic) Color.White else cs.onSurface, CircleShape
                                    ) else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(color.icon, fontSize = 24.sp)
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        null,
                                        tint = themeColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            color.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                if (isCosmic) Color(0xFF00F5FF) else cs.primary
                            } else textColor.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DarkModeToggle(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    isCosmic: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val cardBg = if (isCosmic) Color.White.copy(alpha = 0.08f) else cs.surface
    val textColor = if (isCosmic) Color.White else cs.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (isCosmic) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isDarkMode) {
                    if (isCosmic) Color(0xFFBB86FC).copy(alpha = 0.2f) else Color(0xFF1A237E).copy(alpha = 0.2f)
                } else {
                    Color(0xFFFFC107).copy(alpha = 0.2f)
                }
            ) {
                Icon(
                    if (isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    null,
                    tint = if (isDarkMode) {
                        if (isCosmic) Color(0xFFBB86FC) else Color(0xFF3F51B5)
                    } else Color(0xFFFFA000),
                    modifier = Modifier.padding(10.dp).size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Karanlık Mod", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = textColor)
                Text(
                    if (isDarkMode) "Gece modu aktif" else "Gündüz modu aktif",
                    fontSize = 12.sp,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = if (isCosmic) Color(0xFF00F5FF) else Color(0xFF3F51B5)
                )
            )
        }
    }
}

@Composable
private fun PreviewCard(previewTitle: String) {
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = cs.secondary.copy(alpha = 0.12f)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Badge, null, tint = cs.secondary)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Önizleme", fontSize = 12.sp, color = cs.onSurface.copy(alpha = 0.6f))
                Text(
                    previewTitle,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun NameInputCard(
    studentName: String,
    onNameChange: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = cs.tertiary.copy(alpha = 0.12f)
                ) {
                    Icon(
                        Icons.Rounded.Edit,
                        null,
                        tint = cs.tertiary,
                        modifier = Modifier.padding(10.dp).size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Öğrenci Adı", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Örnek: Ela, Ahmet, Zeynep", color = cs.onSurface.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = studentName,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Ad") },
                placeholder = { Text("Örn: Ela") },
                shape = RoundedCornerShape(14.dp)
            )
        }
    }
}

@Composable
private fun StyleSelectorCard(
    styleWord: String,
    styleOptions: List<String>,
    styleMenuExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onStyleSelected: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cs.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Başlık Stili", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(
                "Örnek: \"Ela'nın Küpü\", \"Ahmet'in Dünyası\"",
                color = cs.onSurface.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(12.dp))

            Box {
                OutlinedButton(
                    onClick = { onExpandChange(true) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(styleWord, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Rounded.ExpandMore, null)
                }

                DropdownMenu(
                    expanded = styleMenuExpanded,
                    onDismissRequest = { onExpandChange(false) }
                ) {
                    styleOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onStyleSelected(option)
                                onExpandChange(false)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelChangeCard(
    context: Context,
    onLevelChange: () -> Unit,
    isCosmic: Boolean = false
) {
    val cs = MaterialTheme.colorScheme
    val educationPrefs = remember { AppPrefs.getEducationPrefs(context) }
    val cardBg = if (isCosmic) Color.White.copy(alpha = 0.08f) else cs.surface
    val textColor = if (isCosmic) Color.White else cs.onSurface

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(if (isCosmic) 0.dp else 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(educationPrefs.level.colorHex).copy(alpha = 0.15f)
                ) {
                    Text(
                        educationPrefs.level.icon,
                        modifier = Modifier.padding(10.dp),
                        fontSize = 24.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mevcut Seviye", fontSize = 12.sp, color = textColor.copy(alpha = 0.6f))
                    Text(
                        "${educationPrefs.schoolType.displayName}${educationPrefs.grade?.let { " - $it. Sınıf" } ?: ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onLevelChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCosmic) Color(0xFF00F5FF) else Color(educationPrefs.level.colorHex)
                )
            ) {
                Icon(Icons.Rounded.SwapHoriz, null, tint = if (isCosmic) Color(0xFF0A0E17) else Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Seviye Değiştir", fontWeight = FontWeight.Bold, color = if (isCosmic) Color(0xFF0A0E17) else Color.White)
            }
        }
    }
}

/**
 * "Ela'nın Küpü", "Ahmet'in Dünyası" gibi başlık üretimi.
 */
private fun buildBrandTitle(rawName: String, styleWord: String, fallback: String): String {
    val name = rawName.trim()
    if (name.isEmpty()) return fallback

    val lastChar = name.last()
    val vowels = setOf('a','e','ı','i','o','ö','u','ü','A','E','I','İ','O','Ö','U','Ü')
    val suffix = if (vowels.contains(lastChar)) "'nın" else "'in"

    return "$name$suffix $styleWord"
}

private const val PREFS_NAME = "bilgideham_prefs"
private const val KEY_STUDENT_NAME = "student_name"
private const val KEY_BRAND_STYLE = "brand_style"
private const val DEFAULT_STYLE = "Küpü"
