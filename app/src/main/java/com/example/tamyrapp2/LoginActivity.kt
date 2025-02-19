package com.example.tamyrapp2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tamyrapp2.viewmodel.AuthViewModel

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

        viewModel.accessToken.observe(this, Observer { token ->
            if (token != null) {
                sharedPreferences.edit().putString("access_token", token).apply()
                sharedPreferences.edit().putString("refresh_token", viewModel.refreshToken.value).apply()
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })

        viewModel.error.observe(this, Observer { error ->
            tvError.text = error
        })
    }
}
