package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.tamyrapp2.R
import com.example.tamyrapp2.retrofit.retrofit.auth.AuthViewModel

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
        val tvSignUp = findViewById<TextView>(R.id.tv_sign_up) // Добавили обработку нажатия

        btnSubmit.setOnClickListener {
            viewModel.loginUser(etUsername.text.toString(), etPassword.text.toString())
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        viewModel.accessToken.observe(this, Observer { token ->
            if (token != null) {
                sharedPreferences.edit().apply {
                    putString("access_token", token)
                    putString("refresh_token", viewModel.refreshToken.value)
                    viewModel.userFirstName.value?.let { putString("user_firstname", it) }
                    viewModel.userEmail.value?.let { putString("user_email", it) }
                    apply()
                }

                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })

        viewModel.error.observe(this, Observer { error ->
            tvError.text = error
        })
    }
}
