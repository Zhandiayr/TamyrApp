package com.example.tamyrapp2.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R

class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val btnConnectMiBand = findViewById<Button>(R.id.button_connect_miband)
        val btnViewData = findViewById<Button>(R.id.button_view_data)
        val btnLogout = findViewById<Button>(R.id.button_logout)

        btnConnectMiBand.setOnClickListener {
            startActivity(Intent(this, MiBandActivity::class.java))
        }

        btnViewData.setOnClickListener {
            startActivity(Intent(this, MiBandDataActivity::class.java))
        }

        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        sharedPreferences.edit().remove("access_token").apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
