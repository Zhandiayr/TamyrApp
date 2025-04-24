package com.example.tamyrapp2.UI

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        profileImage = findViewById(R.id.profile_image)
        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        // Устанавливаем имя и email пользователя из SharedPreferences
        val userNameTextView = findViewById<TextView>(R.id.tv_user_name)
        val userEmailTextView = findViewById<TextView>(R.id.tv_user_email)

        val firstName = sharedPreferences.getString("user_firstname", "Имя не указано")
        val email = sharedPreferences.getString("user_email", "Email не указан")

        userNameTextView.text = firstName
        userEmailTextView.text = email

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                profileImage.setImageURI(imageUri)
            }
        }

        findViewById<ImageView>(R.id.edit_avatar).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        // ✅ Обработка нажатия на кнопку "Profile"
        val profileButton = findViewById<LinearLayout>(R.id.btn_profile)
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.selectedItemId = R.id.nav_settings

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }
}
