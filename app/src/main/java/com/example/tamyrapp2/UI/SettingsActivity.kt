package com.example.tamyrapp2.UI

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tamyrapp2.R
import com.example.tamyrapp2.presentation.utils.SettingsViewModel
import com.example.tamyrapp2.presentation.utils.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var userEmailTextView: TextView

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        profileImage = findViewById(R.id.profile_image)
        userEmailTextView = findViewById(R.id.tv_user_email)

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

        findViewById<LinearLayout>(R.id.btn_profile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btn_notifications).setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btn_life_data).setOnClickListener {
            startActivity(Intent(this, LifestyleInfoActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btn_privacy).setOnClickListener {
            startActivity(Intent(this, SecurityPolicyActivity::class.java))
        }

        findViewById<Button>(R.id.button_logout).setOnClickListener {
            logOut()
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

        // Инициализация ViewModel с фабрикой
        val factory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        // Подписка на LiveData email
        viewModel.email.observe(this) { email ->
            userEmailTextView.text = email
        }

        // Подписка на ошибки
        viewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Загружаем email с backend
        viewModel.loadUserEmail()
    }

    private fun logOut() {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            clear() // Очистить всё
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
