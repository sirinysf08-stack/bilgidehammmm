package com.example.bilgideham

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

// --- KONFİGÜRASYON ---
// Bu metin Google Politikaları gereği zorunludur.
private const val DISCLAIMER_TEXT = "Yapay zeka tarafından oluşturulan içerikler hatalı olabilir. Lütfen önemli bilgileri kontrol ediniz."

/**
 * Ekranın altına veya içerik sonuna eklenecek yasal uyarı şeridi.
 */
@Composable
fun AiDisclaimerFooter(
    modifier: Modifier = Modifier,
    isDarkMode: Boolean = false
) {
    val bgColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFF1F8E9)
    val contentColor = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF558B2F)
    val iconColor = if (isDarkMode) Color(0xFF64748B) else Color(0xFF7CB342)

    Surface(
        color = bgColor,
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = DISCLAIMER_TEXT,
                fontSize = 11.sp,
                color = contentColor,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * İçeriği raporlamak için kullanılan ikon butonu.
 * AI mesaj baloncuklarının yanına veya altına eklenebilir.
 */
@Composable
fun ReportIconButton(
    onClick: () -> Unit,
    tint: Color = Color.Gray,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = onClick, modifier = modifier.size(32.dp)) {
        Icon(
            imageVector = Icons.Default.Flag,
            contentDescription = "İçeriği Raporla",
            tint = tint.copy(alpha = 0.6f),
            modifier = Modifier.size(16.dp)
        )
    }
}

/**
 * Raporlama Diyaloğu.
 * Kullanıcı bir içeriği raporladığında açılır.
 */
@Composable
fun ReportContentDialog(
    onDismiss: () -> Unit,
    onSubmit: (reason: String, details: String) -> Unit
) {
    var selectedReason by remember { mutableStateOf("Zararlı/Uygunsuz İçerik") }
    var details by remember { mutableStateOf("") }
    val reasons = listOf("Zararlı/Uygunsuz İçerik", "Yanlış Bilgi", "Spam/Reklam", "Diğer")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "İçeriği Raporla",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bu içeriği neden raporluyorsunuz? Geri bildiriminiz AI modelini iyileştirmemize yardımcı olur.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Seçenekler
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedReason == reason),
                            onClick = { selectedReason = reason }
                        )
                        Text(
                            text = reason,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    placeholder = { Text("Ek açıklama (isteğe bağlı)...", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("İptal")
                    }
                    Button(
                        onClick = { onSubmit(selectedReason, details) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Raporla")
                    }
                }
            }
        }
    }
}
