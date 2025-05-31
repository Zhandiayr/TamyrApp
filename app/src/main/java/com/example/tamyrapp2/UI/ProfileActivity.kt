package com.example.tamyrapp2.UI

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button
    private lateinit var profileImage: ImageView
    private lateinit var editAvatar: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        cancelButton = findViewById(R.id.cancelButton)
        saveButton = findViewById(R.id.saveButton)
        profileImage = findViewById(R.id.profile_image)
        editAvatar = findViewById(R.id.edit_avatar)

        loadUserData()

        cancelButton.setOnClickListener {
            finish() // Закрывает экран без сохранения изменений
        }

        saveButton.setOnClickListener {
            // Пока нет логики сохранения
            finish()
        }
    }

    private fun loadUserData() {
        val name = sharedPreferences.getString("user_name", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")

        tvUserName.text = name
        tvUserEmail.text = email
    }
}
