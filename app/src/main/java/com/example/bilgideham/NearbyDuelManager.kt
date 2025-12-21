package com.example.bilgideham

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import org.json.JSONObject
import java.nio.charset.StandardCharsets

data class DiscoveredEndpoint(
    val id: String,
    val name: String
)

class NearbyDuelManager(
    context: Context,
    private val myEndpointName: String
) {
    private val appContext = context.applicationContext
    private val connectionsClient: ConnectionsClient = Nearby.getConnectionsClient(appContext)

    // ServiceId: Aynı uygulamayı kullananlar birbirini bulsun
    private val serviceId: String = appContext.packageName

    // İstikrarlı topoloji: 1 host, çoklu client modeline uygun
    private val strategy: Strategy = Strategy.P2P_STAR

    // UI tarafından okunacak state’ler
    val discoveredEndpoints = mutableStateListOf<DiscoveredEndpoint>()

    var statusText by mutableStateOf("Hazır")
        private set

    var isConnected by mutableStateOf(false)
        private set

    private var connectedEndpointId: String? = null

    // Callbacks (DuelScreen bunları set ediyor)
    var onConnected: ((endpointId: String, endpointName: String) -> Unit)? = null
    var onDisconnected: (() -> Unit)? = null
    var onMessage: ((json: JSONObject) -> Unit)? = null
    var onError: ((msg: String) -> Unit)? = null

    private val discoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            val name = info.endpointName
            if (discoveredEndpoints.none { it.id == endpointId }) {
                discoveredEndpoints.add(DiscoveredEndpoint(endpointId, name))
            }
            statusText = "Cihaz bulundu: $name"
        }

        override fun onEndpointLost(endpointId: String) {
            discoveredEndpoints.removeAll { it.id == endpointId }
            statusText = "Cihaz kayboldu"
        }
    }

    private val lifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            statusText = "Bağlantı isteği: ${connectionInfo.endpointName}"
            connectionsClient.acceptConnection(endpointId, payloadCallback)
                .addOnFailureListener { e ->
                    onError?.invoke("Bağlantı kabul edilemedi: ${e.message}")
                }
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                connectedEndpointId = endpointId
                isConnected = true

                // Bağlantı kurulunca discovery/advertising’i kapat
                try { connectionsClient.stopDiscovery() } catch (_: Exception) {}
                try { connectionsClient.stopAdvertising() } catch (_: Exception) {}

                val epName = discoveredEndpoints.firstOrNull { it.id == endpointId }?.name
                    ?: "Rakip"
                statusText = "Bağlandı: $epName"
                onConnected?.invoke(endpointId, epName)
            } else {
                onError?.invoke("Bağlantı başarısız: ${result.status.statusMessage ?: result.status.statusCode}")
                clearConnection()
            }
        }

        override fun onDisconnected(endpointId: String) {
            onError?.invoke("Bağlantı koptu.")
            clearConnection()
            onDisconnected?.invoke()
        }
    }

    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val bytes = payload.asBytes() ?: return
            val text = String(bytes, StandardCharsets.UTF_8)
            try {
                val json = JSONObject(text)
                onMessage?.invoke(json)
            } catch (e: Exception) {
                onError?.invoke("Mesaj parse edilemedi: ${e.message}")
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Şu an için gerekli değil; büyük payload’larda progress izlemek istersen burası kullanılır.
        }
    }

    fun startHosting() {
        statusText = "Oda kuruluyor (advertising)..."
        discoveredEndpoints.clear()
        clearConnection()

        val options = AdvertisingOptions.Builder()
            .setStrategy(strategy)
            .build()

        connectionsClient.startAdvertising(
            myEndpointName,
            serviceId,
            lifecycleCallback,
            options
        ).addOnSuccessListener {
            statusText = "Oda kuruldu. Rakip bekleniyor..."
        }.addOnFailureListener { e ->
            onError?.invoke("Oda kurulamadı: ${e.message}")
            statusText = "Oda kurulamadı"
        }
    }

    fun startDiscovery() {
        statusText = "Cihaz aranıyor (discovery)..."
        discoveredEndpoints.clear()
        clearConnection()

        val options = DiscoveryOptions.Builder()
            .setStrategy(strategy)
            .build()

        connectionsClient.startDiscovery(
            serviceId,
            discoveryCallback,
            options
        ).addOnSuccessListener {
            statusText = "Arama başladı. Yakındaki cihazlar listelenecek."
        }.addOnFailureListener { e ->
            onError?.invoke("Cihaz aranamadı: ${e.message}")
            statusText = "Arama başlatılamadı"
        }
    }

    fun requestConnection(endpointId: String, endpointName: String) {
        statusText = "Bağlanılıyor: $endpointName"
        connectionsClient.requestConnection(
            myEndpointName,
            endpointId,
            lifecycleCallback
        ).addOnFailureListener { e ->
            onError?.invoke("Bağlantı isteği başarısız: ${e.message}")
            statusText = "Bağlantı isteği başarısız"
        }
    }

    fun send(json: JSONObject) {
        val endpointId = connectedEndpointId
        if (endpointId.isNullOrBlank()) {
            onError?.invoke("Gönderim yapılamadı: bağlı endpoint yok.")
            return
        }
        val bytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        val payload = Payload.fromBytes(bytes)
        connectionsClient.sendPayload(endpointId, payload)
            .addOnFailureListener { e ->
                onError?.invoke("Mesaj gönderilemedi: ${e.message}")
            }
    }

    fun stopAll() {
        try { connectionsClient.stopDiscovery() } catch (_: Exception) {}
        try { connectionsClient.stopAdvertising() } catch (_: Exception) {}
        try {
            connectedEndpointId?.let { connectionsClient.disconnectFromEndpoint(it) }
        } catch (_: Exception) {}
        try { connectionsClient.stopAllEndpoints() } catch (_: Exception) {}

        discoveredEndpoints.clear()
        clearConnection()
        statusText = "Durduruldu"
    }

    private fun clearConnection() {
        isConnected = false
        connectedEndpointId = null
    }
}
