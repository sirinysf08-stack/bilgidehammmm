package com.example.bilgideham

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleResultScreen(
    correct: Int,
    wrong: Int,
    onHome: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Sınav Bitti!",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Doğru: $correct",
                    fontSize = 22.sp,
                    color = Color(0xFF43A047) // yeşil
                )

                Text(
                    text = "Yanlış: $wrong",
                    fontSize = 22.sp,
                    color = Color(0xFFE53935) // kırmızı
                )

                val total = correct + wrong
                val percent = if (total > 0) (correct * 100) / total else 0

                Text(
                    text = "Başarı: $percent%",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Ana Sayfa",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
