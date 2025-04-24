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

class RegisterActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val etUsername = findViewById<EditText>(R.id.user_login)
        val etEmail = findViewById<EditText>(R.id.user_email)
        val etPassword = findViewById<EditText>(R.id.user_pass)
        val etFirstName = findViewById<EditText>(R.id.user_firstname)
        val etLastName = findViewById<EditText>(R.id.user_lastname)
        val btnRegister = findViewById<Button>(R.id.button_reg)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnRegister.setOnClickListener {
            viewModel.registerUser(
                etUsername.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString(),
                etFirstName.text.toString(),
                etLastName.text.toString()
            )
        }

        viewModel.success.observe(this, Observer { isSuccess ->
            if (isSuccess) {
                // ✅ Сохраняем имя и email в SharedPreferences
                sharedPreferences.edit()
                    .putString("user_firstname", etFirstName.text.toString())
                    .putString("user_email", etEmail.text.toString())
                    .apply()

                Toast.makeText(this, "Регистрация успешна! Войдите в аккаунт.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        })

        viewModel.error.observe(this, Observer { error ->
            tvResult.text = error
        })
    }
}
