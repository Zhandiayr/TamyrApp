package com.example.tamyrapp2
import AuthViewModel
import androidx.activity.viewModels
import androidx.lifecycle.Observer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.user_login)
        val etEmail = findViewById<EditText>(R.id.user_email)
        val etPassword = findViewById<EditText>(R.id.user_pass)
        val etFirstName = findViewById<EditText>(R.id.user_firstname)
        val etLastName = findViewById<EditText>(R.id.user_lastname)
        val btnRegister = findViewById<Button>(R.id.button_reg)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                viewModel.registerUser(username, email, password, firstName, lastName)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.accessToken.observe(this, Observer { accessToken ->
            accessToken?.let {
                tvResult.text == "Access Token: $it"
            }
        })

        viewModel.refreshToken.observe(this, Observer { refreshToken ->
            refreshToken?.let {
                tvResult.append("\nRefresh Token: $it")
            }
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                tvResult.text == it
            }
        })
    }
}
