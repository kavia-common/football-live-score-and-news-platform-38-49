package org.example.app.realtime

import android.os.Handler
import android.os.Looper
import org.example.app.network.BackendApi
import java.util.Timer
import java.util.TimerTask

internal class RealtimeUpdatesManager(
    private val api: BackendApi,
    private val onLiveMatchesUpdate: (List<String>) -> Unit,
    private val onStatus: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var pollTimer: Timer? = null

    // PUBLIC_INTERFACE
    fun start(authToken: String?) {
        /**
         * Starts real-time updates.
         *
         * Current implementation: polling fallback every 15 seconds.
         * WebSocket can be added later once backend exposes a stable endpoint.
         */
        onStatus("Updating live scores…")
        startPolling(authToken)
    }

    // PUBLIC_INTERFACE
    fun stop() {
        /** Stops real-time updates. */
        pollTimer?.cancel()
        pollTimer = null
    }

    private fun startPolling(authToken: String?) {
        pollTimer?.cancel()
        pollTimer = Timer("live_poll", true)
        pollTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val res = api.getLiveMatches(authToken)
                mainHandler.post {
                    res.onSuccess { onLiveMatchesUpdate(it) }
                        .onFailure { onStatus("Live update failed: ${it.message ?: "unknown error"}") }
                }
            }
        }, 0L, 15_000L)
    }
}
