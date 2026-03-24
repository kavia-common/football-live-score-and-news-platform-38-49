package org.example.app

/**
 * Central place for app configuration.
 *
 * Note: in a real app, you'd prefer BuildConfig fields or remote config.
 */
internal object AppConfig {
    /**
     * Backend base URL for REST calls.
     *
     * This environment uses a hosted backend; if you run locally, change to your reachable host.
     */
    const val BASE_URL: String = "https://vscode-internal-19673-beta.beta01.cloud.kavia.ai:3001"

    /**
     * Backend WebSocket endpoint.
     *
     * The backend OpenAPI currently only exposes a "/" health check, so this WebSocket
     * may not exist yet. The app will automatically fall back to polling.
     */
    const val WEBSOCKET_URL: String = "wss://vscode-internal-19673-beta.beta01.cloud.kavia.ai:3001/ws"
}
