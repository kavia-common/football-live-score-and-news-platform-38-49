package org.example.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.example.app.network.BackendApi

class SignupActivity : Activity() {
    private lateinit var sessionManager: SessionManager
    private val api = BackendApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_signup)

        val emailEt = findViewById<EditText>(R.id.emailEditText)
        val passwordEt = findViewById<EditText>(R.id.passwordEditText)
        val confirmEt = findViewById<EditText>(R.id.confirmPasswordEditText)
        val signupBtn = findViewById<Button>(R.id.signupButton)
        val goToLoginBtn = findViewById<Button>(R.id.goToLoginButton)
        val errorTv = findViewById<TextView>(R.id.errorText)

        signupBtn.setOnClickListener {
            errorTv.visibility = View.GONE

            val email = emailEt.text?.toString()?.trim().orEmpty()
            val password = passwordEt.text?.toString().orEmpty()
            val confirm = confirmEt.text?.toString().orEmpty()

            if (email.isBlank() || !email.contains("@")) {
                showError(errorTv, "Please enter a valid email.")
                return@setOnClickListener
            }
            if (password.length < 4) {
                showError(errorTv, "Password must be at least 4 characters.")
                return@setOnClickListener
            }
            if (password != confirm) {
                showError(errorTv, "Passwords do not match.")
                return@setOnClickListener
            }

            val tokenResult = api.signup(email, password)
            val token = tokenResult.getOrNull() ?: "dev-token"

            sessionManager.saveSession(token, email)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        goToLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showError(tv: TextView, message: String) {
        tv.text = message
        tv.visibility = View.VISIBLE
    }
}
