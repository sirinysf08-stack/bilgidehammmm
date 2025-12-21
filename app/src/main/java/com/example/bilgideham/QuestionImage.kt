package com.example.bilgideham

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

@Composable
fun QuestionImageFromBase64(
    imageBase64: String?,
    modifier: Modifier = Modifier
) {
    if (imageBase64.isNullOrBlank()) return

    val bmp: Bitmap? = remember(imageBase64) {
        runCatching {
            val bytes = Base64.decode(imageBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    if (bmp != null) {
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = "Soru görseli",
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp, max = 320.dp)
                .padding(top = 8.dp, bottom = 8.dp)
        )
    } else {
        Text(
            text = "Görsel yüklenemedi.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(8.dp)
        )
    }
}
