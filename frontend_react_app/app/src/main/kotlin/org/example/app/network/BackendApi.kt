package org.example.app.network

import org.example.app.AppConfig

/**
 * Minimal backend integration layer.
 *
 * IMPORTANT:
 * The downloaded OpenAPI spec currently exposes only GET "/".
 * We implement that health check, and provide placeholder methods for login/signup/matches/news
 * which will gracefully fail and allow the UI to show mock data until backend endpoints are added.
 */
internal class BackendApi {

    // PUBLIC_INTERFACE
    fun healthCheck(): Result<Unit> {
        /** Calls backend health endpoint (GET "/"). */
        return try {
            val resp = HttpClient.get("${AppConfig.BASE_URL}/")
            if (resp.statusCode in 200..299) Result.success(Unit)
            else Result.failure(IllegalStateException("Health check failed (${resp.statusCode})"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // PUBLIC_INTERFACE
    fun login(email: String, password: String): Result<String> {
        /**
         * Attempts login. Returns auth token on success.
         * Placeholder until backend exposes an auth endpoint.
         */
        return Result.failure(UnsupportedOperationException("Login endpoint not available yet"))
    }

    // PUBLIC_INTERFACE
    fun signup(email: String, password: String): Result<String> {
        /**
         * Attempts signup. Returns auth token on success.
         * Placeholder until backend exposes an auth endpoint.
         */
        return Result.failure(UnsupportedOperationException("Signup endpoint not available yet"))
    }

    // PUBLIC_INTERFACE
    fun getLiveMatches(authToken: String?): Result<List<String>> {
        /**
         * Fetch live matches list.
         * Placeholder: returns mocked items until backend endpoint exists.
         */
        val mock = listOf(
            "Liverpool vs Chelsea • 1 - 0 • 67'",
            "Barcelona vs Sevilla • 2 - 2 • 79'",
            "Bayern vs Dortmund • 0 - 1 • HT"
        )
        return Result.success(mock)
    }

    // PUBLIC_INTERFACE
    fun getUpcomingMatches(authToken: String?): Result<List<String>> {
        /** Fetch upcoming matches list (mocked for now). */
        val mock = listOf(
            "Arsenal vs Spurs • Today 19:30",
            "Inter vs Milan • Tomorrow 21:00",
            "PSG vs Lyon • Sat 18:00"
        )
        return Result.success(mock)
    }

    // PUBLIC_INTERFACE
    fun getNews(authToken: String?): Result<List<String>> {
        /** Fetch latest news list (mocked for now). */
        val mock = listOf(
            "Transfer roundup: Big moves expected before deadline",
            "Injury update: Key striker ruled out for 3 weeks",
            "Tactical preview: Derby showdown this weekend"
        )
        return Result.success(mock)
    }
}
