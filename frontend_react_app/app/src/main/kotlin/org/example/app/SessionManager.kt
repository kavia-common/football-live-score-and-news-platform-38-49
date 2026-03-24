package org.example.app

import android.content.Context

internal class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // PUBLIC_INTERFACE
    fun isLoggedIn(): Boolean {
        /** Returns true if an auth token is present. */
        return !getAuthToken().isNullOrBlank()
    }

    // PUBLIC_INTERFACE
    fun getAuthToken(): String? {
        /** Returns the stored auth token (if any). */
        return prefs.getString(KEY_TOKEN, null)
    }

    // PUBLIC_INTERFACE
    fun getEmail(): String? {
        /** Returns the stored user email (if any). */
        return prefs.getString(KEY_EMAIL, null)
    }

    // PUBLIC_INTERFACE
    fun saveSession(token: String, email: String) {
        /** Persists the current session. */
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    // PUBLIC_INTERFACE
    fun clear() {
        /** Clears stored session data. */
        prefs.edit().clear().apply()
    }

    private companion object {
        private const val PREFS_NAME = "football_live_session"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EMAIL = "email"
    }
}
