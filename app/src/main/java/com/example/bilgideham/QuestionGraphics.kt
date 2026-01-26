package com.example.bilgideham

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * ÖSYM/MEB kalitesinde görsel soru grafikleri
 * 
 * Desteklenen tipler:
 * - numberLine: Sayı doğrusu
 * - pieChart: Pasta grafiği
 * - table: Veri tablosu
 * - grid: Kare/dikdörtgen grid
 * - coordinate: Koordinat sistemi
 * - barChart: Çubuk grafik
 */

@Composable
fun QuestionGraphicRenderer(
    graphicType: String,
    graphicData: String,
    modifier: Modifier = Modifier
) {
    if (graphicType.isBlank() || graphicData.isBlank()) return
    
    val data = try { JSONObject(graphicData) } catch (e: Exception) { return }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        when (graphicType.lowercase()) {
            "numberline" -> NumberLineChart(data)
            "piechart" -> PieChartGraphic(data)
            "table" -> DataTableGraphic(data)
            "grid" -> GridChartGraphic(data)
            "coordinate" -> CoordinateSystemGraphic(data)
            "barchart" -> BarChartGraphic(data)
        }
    }
}

// ==================== SAYI DOĞRUSU ====================

@Composable
fun NumberLineChart(data: JSONObject) {
    val min = data.optInt("min", -5)
    val max = data.optInt("max", 5)
    val points = data.optJSONObject("points") ?: JSONObject()
    
    val cs = MaterialTheme.colorScheme
    
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp)
    ) {
        val lineY = size.height / 2
        val padding = 40f
        val lineWidth = size.width - (padding * 2)
        val range = max - min
        val stepWidth = lineWidth / range
        
        // Ana çizgi
        drawLine(
            color = Color.Black,
            start = Offset(padding, lineY),
            end = Offset(size.width - padding, lineY),
            strokeWidth = 3f
        )
        
        // Ok başları
        val arrowSize = 12f
        drawLine(Color.Black, Offset(size.width - padding, lineY), 
                 Offset(size.width - padding - arrowSize, lineY - arrowSize), strokeWidth = 3f)
        drawLine(Color.Black, Offset(size.width - padding, lineY),
                 Offset(size.width - padding - arrowSize, lineY + arrowSize), strokeWidth = 3f)
        
        // Sayı işaretleri
        for (i in min..max) {
            val x = padding + ((i - min) * stepWidth)
            drawLine(
                color = Color.Black,
                start = Offset(x, lineY - 10f),
                end = Offset(x, lineY + 10f),
                strokeWidth = 2f
            )
            
            // Sayı etiketi
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    textSize = 28f
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                canvas.nativeCanvas.drawText(i.toString(), x, lineY + 35f, paint)
            }
        }
        
        // İşaretli noktalar (A, B, C, vb.)
        val colors = listOf(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFF9800))
        var colorIndex = 0
        
        points.keys().forEach { label ->
            val value = points.optDouble(label, 0.0)
            val x = padding + ((value - min) * stepWidth).toFloat()
            val color = colors[colorIndex % colors.size]
            
            // Nokta
            drawCircle(color, 12f, Offset(x, lineY))
            drawCircle(Color.White, 6f, Offset(x, lineY))
            
            // Etiket
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    textSize = 32f
                    this.color = color.hashCode()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
                canvas.nativeCanvas.drawText(label, x, lineY - 25f, paint)
            }
            
            colorIndex++
        }
    }
}

// ==================== PASTA GRAFİĞİ ====================

@Composable
fun PieChartGraphic(data: JSONObject) {
    val slices = data.optJSONArray("slices") ?: JSONArray()
    val labels = data.optJSONArray("labels") ?: JSONArray()
    val colors = listOf(
        Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047),
        Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4)
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pasta grafiği
        Canvas(
            modifier = Modifier.size(150.dp)
        ) {
            val total = (0 until slices.length()).sumOf { slices.optDouble(it, 0.0) }
            var startAngle = -90f
            
            for (i in 0 until slices.length()) {
                val value = slices.optDouble(i, 0.0)
                val sweepAngle = ((value / total) * 360).toFloat()
                
                drawArc(
                    color = colors[i % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
                
                // Çerçeve
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    style = Stroke(width = 3f),
                    size = Size(size.width, size.height)
                )
                
                startAngle += sweepAngle
            }
        }
        
        // Legend
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            for (i in 0 until slices.length()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(colors[i % colors.size], RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (i < labels.length()) labels.optString(i, "") else "${slices.optInt(i)}",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// ==================== VERİ TABLOSU ====================

@Composable
fun DataTableGraphic(data: JSONObject) {
    val rows = data.optJSONArray("rows") ?: JSONArray()
    val headerBg = Color(0xFF1E88E5)
    val cs = MaterialTheme.colorScheme
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
    ) {
        for (rowIndex in 0 until rows.length()) {
            val row = rows.optJSONArray(rowIndex) ?: continue
            val isHeader = rowIndex == 0
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isHeader) headerBg else if (rowIndex % 2 == 0) cs.surface else cs.surfaceVariant,
                        if (rowIndex == 0) RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        else if (rowIndex == rows.length() - 1) RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                        else RoundedCornerShape(0.dp)
                    )
            ) {
                for (colIndex in 0 until row.length()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(0.5.dp, Color.Gray.copy(alpha = 0.3f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = row.optString(colIndex, ""),
                            fontSize = 14.sp,
                            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                            color = if (isHeader) Color.White else cs.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// ==================== GRİD / KARE ÇİZİMİ ====================

@Composable
fun GridChartGraphic(data: JSONObject) {
    val cols = data.optInt("cols", 5)
    val rows = data.optInt("rows", 5)
    val filled = data.optJSONArray("filled") ?: JSONArray()
    val colors = data.optJSONArray("colors") ?: JSONArray()
    
    val defaultColor = Color(0xFF1E88E5)
    val redColor = Color(0xFFE53935)
    val greenColor = Color(0xFF43A047)
    
    val filledSet = mutableMapOf<Pair<Int, Int>, Color>()
    for (i in 0 until filled.length()) {
        val cell = filled.optJSONArray(i)
        if (cell != null && cell.length() >= 2) {
            val r = cell.optInt(0)
            val c = cell.optInt(1)
            val colorName = if (cell.length() > 2) cell.optString(2, "blue") else 
                           if (i < colors.length()) colors.optString(i, "blue") else "blue"
            
            val color = when (colorName.lowercase()) {
                "red" -> redColor
                "green" -> greenColor
                "yellow" -> Color(0xFFFFEB3B)
                "orange" -> Color(0xFFFF9800)
                else -> defaultColor
            }
            filledSet[Pair(r, c)] = color
        }
    }
    
    val cellSize = 32.dp
    
    Column(
        modifier = Modifier
            .border(2.dp, Color.Black)
            .background(Color.White)
    ) {
        for (r in 0 until rows) {
            Row {
                for (c in 0 until cols) {
                    val cellColor = filledSet[Pair(r, c)]
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .border(0.5.dp, Color.Gray)
                            .background(cellColor ?: Color.Transparent)
                    )
                }
            }
        }
    }
}

// ==================== KOORDİNAT SİSTEMİ ====================

@Composable
fun CoordinateSystemGraphic(data: JSONObject) {
    val minX = data.optInt("minX", -5)
    val maxX = data.optInt("maxX", 5)
    val minY = data.optInt("minY", -5)
    val maxY = data.optInt("maxY", 5)
    val points = data.optJSONArray("points") ?: JSONArray()
    
    val colors = listOf(
        Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFFFF9800)
    )
    
    Canvas(
        modifier = Modifier
            .size(200.dp)
            .background(Color.White)
    ) {
        val padding = 30f
        val graphWidth = size.width - (padding * 2)
        val graphHeight = size.height - (padding * 2)
        
        val rangeX = maxX - minX
        val rangeY = maxY - minY
        
        val originX = padding + ((-minX.toFloat() / rangeX) * graphWidth)
        val originY = padding + ((maxY.toFloat() / rangeY) * graphHeight)
        
        // Grid çizgileri
        for (x in minX..maxX) {
            val px = padding + (((x - minX).toFloat() / rangeX) * graphWidth)
            drawLine(Color.LightGray, Offset(px, padding), Offset(px, size.height - padding), 1f)
        }
        for (y in minY..maxY) {
            val py = padding + (((maxY - y).toFloat() / rangeY) * graphHeight)
            drawLine(Color.LightGray, Offset(padding, py), Offset(size.width - padding, py), 1f)
        }
        
        // X ekseni
        drawLine(Color.Black, Offset(padding, originY), Offset(size.width - padding, originY), 2f)
        // Y ekseni
        drawLine(Color.Black, Offset(originX, padding), Offset(originX, size.height - padding), 2f)
        
        // Ok başları
        drawLine(Color.Black, Offset(size.width - padding, originY), 
                 Offset(size.width - padding - 10f, originY - 5f), 2f)
        drawLine(Color.Black, Offset(size.width - padding, originY),
                 Offset(size.width - padding - 10f, originY + 5f), 2f)
        drawLine(Color.Black, Offset(originX, padding), 
                 Offset(originX - 5f, padding + 10f), 2f)
        drawLine(Color.Black, Offset(originX, padding),
                 Offset(originX + 5f, padding + 10f), 2f)
        
        // Noktalar
        for (i in 0 until points.length()) {
            val point = points.optJSONObject(i) ?: continue
            val label = point.optString("label", "")
            val x = point.optDouble("x", 0.0)
            val y = point.optDouble("y", 0.0)
            
            val px = padding + (((x - minX) / rangeX) * graphWidth).toFloat()
            val py = padding + (((maxY - y) / rangeY) * graphHeight).toFloat()
            
            val color = colors[i % colors.size]
            
            drawCircle(color, 10f, Offset(px, py))
            drawCircle(Color.White, 5f, Offset(px, py))
            
            drawIntoCanvas { canvas ->
                val paint = android.graphics.Paint().apply {
                    textSize = 28f
                    this.color = color.hashCode()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
                canvas.nativeCanvas.drawText(label, px, py - 18f, paint)
            }
        }
    }
}

// ==================== ÇUBUK GRAFİK ====================

@Composable
fun BarChartGraphic(data: JSONObject) {
    val bars = data.optJSONArray("bars") ?: JSONArray()
    val labels = data.optJSONArray("labels") ?: JSONArray()
    val colors = listOf(
        Color(0xFF1E88E5), Color(0xFFE53935), Color(0xFF43A047),
        Color(0xFFFF9800), Color(0xFF9C27B0)
    )
    
    val maxValue = (0 until bars.length()).maxOfOrNull { bars.optDouble(it, 0.0) } ?: 1.0
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until bars.length()) {
            val value = bars.optDouble(i, 0.0)
            val heightFraction = (value / maxValue).toFloat()
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value.toInt().toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .fillMaxHeight(heightFraction)
                        .background(colors[i % colors.size], RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                
                if (i < labels.length()) {
                    Text(
                        text = labels.optString(i, ""),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                }
            }
    }
}

// ==================== BASE64 GÖRSEL GÖSTERİMİ ====================

@Composable
fun QuestionImageDisplay(
    base64: String,
    mimeType: String,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    
    // PNG/JPEG için Bitmap decode et
    val bitmap = remember(base64) {
        try {
            val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }
    
    if (bitmap != null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, cs.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Soru görseli",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }
    } else {
        // Görsel yüklenemedi
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(cs.surfaceVariant, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "📷 Görsel yüklenemedi",
                color = cs.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
