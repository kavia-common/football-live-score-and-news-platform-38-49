package org.example.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.example.app.network.BackendApi

class LoginActivity : Activity() {
    private lateinit var sessionManager: SessionManager
    private val api = BackendApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        // If already logged in, go to main.
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val emailEt = findViewById<EditText>(R.id.emailEditText)
        val passwordEt = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val goToSignupBtn = findViewById<Button>(R.id.goToSignupButton)
        val errorTv = findViewById<TextView>(R.id.errorText)

        loginBtn.setOnClickListener {
            errorTv.visibility = View.GONE

            val email = emailEt.text?.toString()?.trim().orEmpty()
            val password = passwordEt.text?.toString().orEmpty()

            if (email.isBlank() || !email.contains("@")) {
                showError(errorTv, "Please enter a valid email.")
                return@setOnClickListener
            }
            if (password.length < 4) {
                showError(errorTv, "Password must be at least 4 characters.")
                return@setOnClickListener
            }

            // Try backend auth; fallback to dev token until backend is implemented.
            val tokenResult = api.login(email, password)
            val token = tokenResult.getOrNull() ?: "dev-token"

            sessionManager.saveSession(token, email)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        goToSignupBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun showError(tv: TextView, message: String) {
        tv.text = message
        tv.visibility = View.VISIBLE
    }
}
