package com.example.tamyrapp2.UI

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
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        // ✅ Проверяем, заполнял ли пользователь анкету
        val isPersonalInfoFilled = sharedPreferences.getBoolean("personal_info_filled", false)

        if (!isPersonalInfoFilled) {
            // Если анкета не заполнена, отправляем пользователя на `PersonalInfoActivity`
            val intent = Intent(this, PersonalInfoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val btnLogout = findViewById<Button>(R.id.button_logout)
        btnLogout.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        // Удаляем токены из SharedPreferences
        sharedPreferences.edit().clear().apply()

        // Перенаправляем пользователя на экран входа
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}


