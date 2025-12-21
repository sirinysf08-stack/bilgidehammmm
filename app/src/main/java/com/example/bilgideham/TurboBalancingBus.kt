package com.example.bilgideham

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object TurboBalancingBus {

    data class State(
        val isRunning: Boolean = false,
        val task: String = "",
        val progress: Float = 0f,
        val logs: String = "Sistem hazır. Bekleniyor...",
        val lastCloudCounts: Map<String, Int>? = null,
        val lastTarget: String = "",
        val lastSaved: Int = 0
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    fun markStarted(initialLog: String = "🚀 Turbo dengeleme başlatıldı.") {
        _state.update {
            it.copy(
                isRunning = true,
                task = "Başlatılıyor...",
                progress = 0.1f,
                logs = appendLineSafe(it.logs, initialLog),
                lastSaved = 0
            )
        }
    }

    fun markStopped(stopLog: String = "⛔ Turbo dengeleme durduruldu.") {
        _state.update {
            it.copy(
                isRunning = false,
                task = "Durduruldu",
                progress = 0f,
                logs = appendLineSafe(it.logs, stopLog),
                lastSaved = 0
            )
        }
    }

    fun updateProgress(
        task: String,
        progress: Float,
        logLine: String? = null,
        cloudCounts: Map<String, Int>? = null,
        lastTarget: String? = null,
        lastSaved: Int? = null
    ) {
        _state.update { s ->
            s.copy(
                task = task,
                progress = progress.coerceIn(0f, 1f),
                logs = if (!logLine.isNullOrBlank()) appendLineSafe(s.logs, logLine) else s.logs,
                lastCloudCounts = cloudCounts ?: s.lastCloudCounts,
                lastTarget = lastTarget ?: s.lastTarget,
                lastSaved = lastSaved ?: s.lastSaved
            )
        }
    }

    fun error(err: String) {
        _state.update { s ->
            s.copy(
                task = "Hata",
                progress = 0f,
                logs = appendLineSafe(s.logs, "❌ Hata: $err")
            )
        }
    }

    private fun appendLineSafe(existing: String, line: String): String {
        // log şişmesini kontrol altına almak için limit
        val maxChars = 9000
        val combined = (line.trim() + "\n" + existing).trim()
        return if (combined.length <= maxChars) combined else combined.take(maxChars)
    }
}
