package com.example.bilgideham

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
// ✅ DÜZELTME: Standart pakette KESİN olan ikonları kullanıyoruz:
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GlobalQuickControls(
    darkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onToggleBrightness: () -> Unit
) {
    val cs = MaterialTheme.colorScheme

    // ✅ Sağ üstte 2 ikon
    Surface(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 8.dp, end = 10.dp),
        color = cs.surface.copy(alpha = 0.92f),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 6.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.End
        ) {
            // 1) Karanlık / Aydınlık
            // Eğer karanlık moddaysak "Yüz" (Gündüze dön), aydınlıktaysak "Yıldız" (Geceye dön) göster.
            IconButton(onClick = onToggleDarkMode) {
                Icon(
                    imageVector = if (darkMode) Icons.Default.Face else Icons.Default.Star,
                    contentDescription = "tema",
                    tint = cs.primary
                )
            }

            // 2) Parlaklık azaltma (Göz konforu modu)
            IconButton(onClick = onToggleBrightness) {
                Icon(
                    // "Settings" (Çark) ikonu standart pakette kesinlikle vardır.
                    imageVector = Icons.Default.Settings,
                    contentDescription = "parlaklık ayarı",
                    tint = cs.primary
                )
            }
        }
    }
}