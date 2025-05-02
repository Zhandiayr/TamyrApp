package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.auth.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val etUsername = findViewById<EditText>(R.id.user_username)
        val etEmail = findViewById<EditText>(R.id.user_email)
        val etPassword = findViewById<EditText>(R.id.user_password)
        val btnRegister = findViewById<Button>(R.id.button_register)
        val tvResult = findViewById<TextView>(R.id.tvErrorRegister)
        val tvLoginHere = findViewById<TextView>(R.id.tv_login_here) // ✨ Новый код

        btnRegister.setOnClickListener {
            viewModel.registerUser(
                etUsername.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString(),
                "none",
                "none"
            )
        }

        viewModel.success.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                saveUserInfo(etUsername.text.toString(), etEmail.text.toString())

                Toast.makeText(this, "Registration Successful! Please log in.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })

        viewModel.error.observe(this, Observer { error ->
            tvResult.text = error
        })

        // ✨ Обработка нажатия на "Login"
        tvLoginHere.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun saveUserInfo(username: String, email: String) {
        sharedPreferences.edit().apply {
            putString("user_username", username)
            putString("user_email", email)
            apply()
        }
    }
}