package com.example.bilgideham

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

/**
 * Google Play In-App Updates yöneticisi
 * Play Store'da güncelleme olduğunda kullanıcıya bildirim gönderir
 */
object UpdateManager {
    
    private const val TAG = "UpdateManager"
    private const val UPDATE_REQUEST_CODE = 1234
    
    private var appUpdateManager: AppUpdateManager? = null
    
    /**
     * Güncelleme kontrolü yap
     */
    suspend fun checkForUpdate(context: Context): UpdateResult {
        return try {
            val manager = AppUpdateManagerFactory.create(context)
            appUpdateManager = manager
            
            val appUpdateInfo = manager.appUpdateInfo.await()
            
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    DebugLog.d(TAG, "Güncelleme mevcut: ${appUpdateInfo.availableVersionCode()}")
                    
                    // Bildirim gönder
                    AppNotificationManager.sendUpdateNotification(context)
                    
                    UpdateResult.Available(appUpdateInfo)
                }
                UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                    DebugLog.d(TAG, "Güncelleme yok")
                    UpdateResult.NotAvailable
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    DebugLog.d(TAG, "Güncelleme devam ediyor")
                    UpdateResult.InProgress
                }
                else -> UpdateResult.NotAvailable
            }
        } catch (e: Exception) {
            Log.e(TAG, "Güncelleme kontrolü hatası", e)
            UpdateResult.Error(e.message ?: "Bilinmeyen hata")
        }
    }
    
    /**
     * Esnek güncelleme başlat (arka planda indir, kullanıcı istediğinde yükle)
     */
    fun startFlexibleUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager?.let { manager ->
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    manager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        UPDATE_REQUEST_CODE
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Güncelleme başlatma hatası", e)
        }
    }
    
    /**
     * Zorunlu güncelleme başlat (hemen yükle)
     */
    fun startImmediateUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager?.let { manager ->
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    manager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        UPDATE_REQUEST_CODE
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Zorunlu güncelleme hatası", e)
        }
    }
    
    /**
     * İndirilen güncellemeyi yükle
     */
    fun completeUpdate() {
        appUpdateManager?.completeUpdate()
    }
    
    /**
     * Güncelleme durumu dinleyicisi ekle
     */
    fun registerListener(listener: InstallStateUpdatedListener) {
        appUpdateManager?.registerListener(listener)
    }
    
    /**
     * Dinleyiciyi kaldır
     */
    fun unregisterListener(listener: InstallStateUpdatedListener) {
        appUpdateManager?.unregisterListener(listener)
    }
}

sealed class UpdateResult {
    data class Available(val info: AppUpdateInfo) : UpdateResult()
    data object NotAvailable : UpdateResult()
    data object InProgress : UpdateResult()
    data class Error(val message: String) : UpdateResult()
}
