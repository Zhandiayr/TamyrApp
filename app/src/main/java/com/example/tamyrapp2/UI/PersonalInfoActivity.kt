package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoViewModel

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etAge: EditText
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var btnSave: Button
    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        etName = findViewById(R.id.et_name)
        etLastName = findViewById(R.id.et_lastname)
        etAge = findViewById(R.id.et_age)
        etHeight = findViewById(R.id.et_height)
        etWeight = findViewById(R.id.et_weight)
        spinnerGender = findViewById(R.id.spinner_gender)
        btnSave = findViewById(R.id.btn_save)

        setupGenderSpinner()
        btnSave.setOnClickListener { handleSaveButtonClick() }
        observeSuccess()
        observeError()
    }

    private fun setupGenderSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Male", "Female"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
    }

    private fun handleSaveButtonClick() {
        val name = etName.text.toString()
        val lastName = etLastName.text.toString()
        val age = etAge.text.toString().toIntOrNull()
        val sex = spinnerGender.selectedItem.toString()
        val weight = etWeight.text.toString().toIntOrNull()
        val height = etHeight.text.toString().toIntOrNull()

        if (name.isNotBlank() && lastName.isNotBlank() &&
            age != null && age in 1..120 && weight != null && weight in 30..300 &&
            height != null && height in 50..250)
        {
            personalInfoViewModel.saveOrUpdatePersonalInfo(name, lastName, age, sex, weight, height)
            saveUserName(name, lastName)
        } else {
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserName(name: String, lastName: String) {
        sharedPreferences.edit().apply {
            putString("user_name", name)
            putString("user_lastname", lastName)
            apply()
        }
    }

    private fun observeSuccess() {
        personalInfoViewModel.success.observe(this) { success ->
            if (success) {
                val userId = sharedPreferences.getLong("user_id", -1L)
                if (userId != -1L) {
                    val profileKey = "profile_filled_user_$userId"
                    Log.d("PERSONAL_INFO", "Setting flag $profileKey = true")
                    sharedPreferences.edit().putBoolean(profileKey, true).apply()
                }
                Toast.makeText(this, "Information saved successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    private fun observeError() {
        personalInfoViewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

