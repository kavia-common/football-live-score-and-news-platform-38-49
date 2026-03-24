package org.example.app.network

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

internal object HttpClient {

    // PUBLIC_INTERFACE
    fun get(url: String, headers: Map<String, String> = emptyMap()): HttpResponse {
        /** Performs a GET request and returns a response wrapper. */
        return request("GET", url, headers, null)
    }

    // PUBLIC_INTERFACE
    fun postJson(url: String, jsonBody: String, headers: Map<String, String> = emptyMap()): HttpResponse {
        /** Performs a POST with application/json body and returns a response wrapper. */
        val merged = headers.toMutableMap()
        merged["Content-Type"] = "application/json; charset=utf-8"
        return request("POST", url, merged, jsonBody)
    }

    private fun request(
        method: String,
        url: String,
        headers: Map<String, String>,
        body: String?
    ): HttpResponse {
        val conn = (URL(url).openConnection() as HttpURLConnection)
        conn.requestMethod = method
        conn.connectTimeout = 10_000
        conn.readTimeout = 10_000

        for ((k, v) in headers) {
            conn.setRequestProperty(k, v)
        }

        if (body != null) {
            conn.doOutput = true
            conn.outputStream.use { os ->
                os.write(body.toByteArray(Charsets.UTF_8))
            }
        }

        val code = conn.responseCode
        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
        val responseBody = stream?.use { input ->
            BufferedReader(InputStreamReader(input)).readText()
        } ?: ""

        return HttpResponse(code, responseBody)
    }
}

internal data class HttpResponse(val statusCode: Int, val body: String)
