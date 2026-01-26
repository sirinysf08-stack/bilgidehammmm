package com.example.bilgideham

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.bilgideham.ui.theme.BilgidehamTheme
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val TAG = "MainActivity"
    
    // İzin isteme launcher'ları
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // İzin sonuçları - loglama yapılabilir
        permissions.entries.forEach { (permission, granted) ->
            DebugLog.d("Permissions", "$permission: $granted")
        }
    }
    
    // In-App Update launcher
    private lateinit var updateLauncher: ActivityResultLauncher<IntentSenderRequest>
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 15+ için Edge-to-Edge desteği
        enableEdgeToEdge()
        
        super.onCreate(savedInstanceState)
        
        // Update launcher'ı kaydet
        updateLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.w(TAG, "Güncelleme iptal edildi veya başarısız: ${result.resultCode}")
            }
        }
        
        // StateFlow'ları SharedPreferences'tan yükle (bir kez)
        AppPrefs.initialize(this)
        
        // Bildirim kanallarını oluştur
        AppNotificationManager.createNotificationChannels(this)
        
        // Google Play güncelleme kontrolü
        checkForAppUpdate()
        
        // Uygulama açılış sayısını artır
        AppPrefs.incrementAppOpenCount(this)
        
        // İlk açılışta izinleri iste
        requestPermissionsIfNeeded()
        
        // Günlük motivasyon bildirimlerini planla
        AppNotificationManager.scheduleDailyMotivation(this)

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            // StateFlow'lardan reaktif olarak oku - POLLING YOK!
            val darkMode by AppPrefs.darkModeFlow.collectAsState()
            val themeId by AppPrefs.themeIdFlow.collectAsState()
            val themeColor by AppPrefs.themeColorFlow.collectAsState()
            val interfaceStyle by AppPrefs.interfaceStyleFlow.collectAsState()
            val readingLevel by AppPrefs.readingLevelFlow.collectAsState()

            val activity = LocalContext.current as? Activity

            // Startup sync (non-blocking)
            LaunchedEffect(Unit) {
                scope.launch(Dispatchers.IO) {
                    // Önceki oturumlardan kalan arka plan worker'larını iptal et
                    runCatching { QuestionSyncWorker.stopSync(context.applicationContext) }
                    
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
                themeId = themeId,
                themeColor = themeColor,
                interfaceStyle = interfaceStyle
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        darkMode = darkMode,
                        onToggleTheme = {
                            AppPrefs.setDarkMode(context, !darkMode)
                        },
                        themeId = themeId,
                        onSetTheme = { newTheme ->
                            AppPrefs.setTheme(context, newTheme)
                        },
                        readingLevel = readingLevel,
                        onToggleBrightness = {
                            val next = (readingLevel + 1) % 4
                            AppPrefs.setReadingLevel(context, next)
                        }
                    )
                }
            }
        }
    }
    
    /**
     * Gerekli izinleri iste (Kamera, Mikrofon, Bildirim)
     */
    private fun requestPermissionsIfNeeded() {
        // Daha önce istendiyse tekrar isteme
        if (AppPrefs.arePermissionsRequested(this)) return
        
        val permissionsToRequest = mutableListOf<String>()
        
        // Kamera izni
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        
        // Mikrofon izni
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
        
        // Bildirim izni (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
        
        // İzinler istendi olarak işaretle
        AppPrefs.markPermissionsRequested(this)
    }
    
    /**
     * Google Play In-App Update kontrolü
     * Güncelleme varsa kullanıcıya dialog gösterir
     */
    private fun checkForAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                DebugLog.d(TAG, "✅ Güncelleme mevcut! Versiyon: ${appUpdateInfo.availableVersionCode()}")
                
                // Esnek güncelleme destekleniyor mu?
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                        )
                        DebugLog.d(TAG, "Esnek güncelleme başlatıldı")
                    } catch (e: Exception) {
                        Log.e(TAG, "Güncelleme başlatma hatası: ${e.message}")
                    }
                }
                // Zorunlu güncelleme destekleniyor mu?
                else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            updateLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                        )
                        DebugLog.d(TAG, "Zorunlu güncelleme başlatıldı")
                    } catch (e: Exception) {
                        Log.e(TAG, "Zorunlu güncelleme hatası: ${e.message}")
                    }
                }
            } else {
                DebugLog.d(TAG, "Güncelleme yok veya zaten güncel")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Güncelleme kontrolü başarısız: ${e.message}")
        }
    }
}
