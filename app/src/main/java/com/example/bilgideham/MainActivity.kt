package com.example.bilgideham

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.bilgideham.ui.theme.BilgidehamTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            var darkMode by remember { mutableStateOf(AppPrefs.getDarkMode(context)) }
            var themeId by remember { mutableStateOf(AppPrefs.getTheme(context)) }
            var readingLevel by remember { mutableIntStateOf(AppPrefs.getReadingLevel(context)) }
            val activity = LocalContext.current as? Activity

            // Startup sync (non-blocking)
            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.IO) {
                    runCatching { GameRepositoryNew.init(context.applicationContext) }
                    runCatching { GameRepositoryNew.syncFromCloudToDevice() }
                }
            }

            // Apply brightness
            LaunchedEffect(readingLevel) {
                activity?.let { BrightnessController.apply(it, readingLevel) }
            }

            BilgidehamTheme(
                darkTheme = darkMode,
                themeId = themeId
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        darkMode = darkMode,
                        onToggleTheme = {
                            darkMode = !darkMode
                            AppPrefs.setDarkMode(context, darkMode)
                        },
                        themeId = themeId,
                        onSetTheme = { newTheme ->
                            themeId = newTheme
                            AppPrefs.setTheme(context, newTheme)
                        },
                        readingLevel = readingLevel,
                        onToggleBrightness = {
                            val next = (readingLevel + 1) % 4
                            readingLevel = next
                            AppPrefs.setReadingLevel(context, next)
                        }
                    )
                }
            }
        }
    }
}
