package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.auth.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    private var accessToken: String? = null
    private var userId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val etUsername = findViewById<EditText>(R.id.user_login)
        val etPassword = findViewById<EditText>(R.id.user_pass)
        val btnSubmit = findViewById<Button>(R.id.button_submit)
        val tvError = findViewById<TextView>(R.id.tvError)
        val tvSignUp = findViewById<TextView>(R.id.tv_sign_up)

        btnSubmit.setOnClickListener {
            viewModel.loginUser(etUsername.text.toString(), etPassword.text.toString())
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        viewModel.accessToken.observe(this) { token ->
            if (token != null) {
                accessToken = token
                sharedPreferences.edit().putString("access_token", token).apply()
                tryNavigate()
            }
        }

        viewModel.userId.observe(this) { id ->
            if (id != null && id != -1L) {
                userId = id
                sharedPreferences.edit().putLong("user_id", id).apply()
                tryNavigate()
            }
        }

        viewModel.refreshToken.observe(this) {
            it?.let { sharedPreferences.edit().putString("refresh_token", it).apply() }
        }

        viewModel.userFirstName.observe(this) {
            it?.let { sharedPreferences.edit().putString("user_firstname", it).apply() }
        }

        viewModel.userEmail.observe(this) {
            it?.let { sharedPreferences.edit().putString("user_email", it).apply() }
        }

        viewModel.error.observe(this) { error ->
            tvError.text = error
        }
    }

    private fun tryNavigate() {
        if (accessToken != null && userId != null) {
            val profileKey = "profile_filled_user_${userId!!}"
            val isProfileFilled = sharedPreferences.getBoolean(profileKey, false)

            Log.d("LOGIN_FLOW", "userId: $userId")
            Log.d("LOGIN_FLOW", "Checking flag $profileKey = $isProfileFilled")

            val nextActivity = if (isProfileFilled) {
                Log.d("LOGIN_FLOW", "Navigating to HomeActivity")
                HomeActivity::class.java
            } else {
                Log.d("LOGIN_FLOW", "Navigating to PersonalInfoActivity")
                PersonalInfoActivity::class.java
            }

            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, nextActivity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}

