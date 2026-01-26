package com.example.bilgideham

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Vega-Lite grafik render eden Compose bileşeni
 * WebView ile client-side render yapar
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VegaLiteChartView(
    vegaSpec: String,
    modifier: Modifier = Modifier,
    onRendered: (Boolean) -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        setSupportZoom(false)
                        builtInZoomControls = false
                    }
                    
                    setBackgroundColor(Color.TRANSPARENT)
                    
                    // Android-JavaScript bridge
                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onChartRendered(success: Boolean) {
                            isLoading = false
                            hasError = !success
                            onRendered(success)
                        }
                    }, "Android")
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Sayfa yüklendiğinde chart'ı render et
                            val escapedSpec = vegaSpec
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                            evaluateJavascript("renderChart(\"$escapedSpec\")", null)
                        }
                    }
                    
                    loadUrl("file:///android_asset/vega_chart.html")
                }
            },
            update = { webView ->
                // Spec değişirse yeniden render et
                val escapedSpec = vegaSpec
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                webView.evaluateJavascript("renderChart(\"$escapedSpec\")", null)
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Error message
        if (hasError) {
            Text(
                text = "Grafik yüklenemedi",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Örnek kullanım için preview
 */
@Composable
fun ChartPreview() {
    val testSpec = """
    {
      "${"$"}schema": "https://vega.github.io/schema/vega-lite/v5.json",
      "title": "Aylık Satışlar",
      "data": {
        "values": [
          {"ay": "Ocak", "satis": 150},
          {"ay": "Şubat", "satis": 230},
          {"ay": "Mart", "satis": 180},
          {"ay": "Nisan", "satis": 320}
        ]
      },
      "mark": "bar",
      "encoding": {
        "x": {"field": "ay", "type": "nominal", "title": "Ay"},
        "y": {"field": "satis", "type": "quantitative", "title": "Satış"}
      }
    }
    """.trimIndent()
    
    VegaLiteChartView(
        vegaSpec = testSpec,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
