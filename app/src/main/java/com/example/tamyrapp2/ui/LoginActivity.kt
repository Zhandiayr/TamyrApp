package com.example.tamyrapp2.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tamyrapp2.R
import com.example.tamyrapp2.retrofit.auth.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val etUsername = findViewById<EditText>(R.id.user_login)
        val etPassword = findViewById<EditText>(R.id.user_pass)
        val btnSubmit = findViewById<Button>(R.id.button_submit)
        val tvError = findViewById<TextView>(R.id.tvError)

        btnSubmit.setOnClickListener {
            viewModel.loginUser(etUsername.text.toString(), etPassword.text.toString())
        }

        viewModel.accessToken.observe(this) { token ->
            if (!token.isNullOrEmpty()) {
                sharedPreferences.edit()
                    .putString("access_token", token)
                    .apply()

                Log.d("LoginActivity", "✅ Токен сохранён: $token") // Логируем сохранённый токен

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Log.e("LoginActivity", "❌ Ошибка: токен пустой!")
            }
        }


        viewModel.error.observe(this, Observer { error ->
            tvError.text = error
        })
    }
}
