package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.UI.ond.OnboardingActivity
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.auth.AuthViewModel
import com.example.tamyrapp2.data.network.personalinfo.MainPersonalInfoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            checkIfPersonalInfoExists(userId!!, accessToken!!)
        }
    }

    private fun checkIfPersonalInfoExists(userId: Long, token: String) {
        RetrofitInstance.personalInfoApi.existsPersonalInfo(userId, "Bearer $token")
            .enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful && response.body() != null) {
                        val exists = response.body()!!
                        val intent = if (exists) {
                            Intent(this@LoginActivity, HomeActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, OnboardingActivity::class.java)
                        }
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        showError("Failed to check personal info")
                    }
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}

