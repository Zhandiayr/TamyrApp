package com.example.tamyrapp2.ui.personalInfo

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoViewModel
import com.example.tamyrapp2.data.network.personalinfo.MainPersonalInfoRequest
import com.example.tamyrapp2.UI.HomeActivity

class PersonalInfoActivity : AppCompatActivity() {

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

        // Инициализация элементов UI
        etName = findViewById(R.id.et_name)
        etLastName = findViewById(R.id.et_lastname)
        etAge = findViewById(R.id.et_age)
        etHeight = findViewById(R.id.et_height)
        etWeight = findViewById(R.id.et_weight)
        spinnerGender = findViewById(R.id.spinner_gender)
        btnSave = findViewById(R.id.btn_save)

        // Инициализация Spinner для выбора пола
        setupGenderSpinner()

        // Обработка нажатия кнопки "Save Info"
        btnSave.setOnClickListener {
            handleSaveButtonClick()
        }

        // Наблюдатель для успешного сохранения данных
        observeSuccess()

        // Наблюдатель для ошибок
        observeError()
    }

    private fun setupGenderSpinner() {
        // Создаем список с вариантами пола
        val genderOptions = listOf("Male", "Female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter
    }

    private fun handleSaveButtonClick() {
        // Получаем данные с экрана
        val name = etName.text.toString()
        val lastName = etLastName.text.toString()
        val age = etAge.text.toString().toIntOrNull()
        val sex = spinnerGender.selectedItem.toString()
        val weight = etWeight.text.toString().toIntOrNull()
        val height = etHeight.text.toString().toIntOrNull()

        // Проверяем, что все данные введены корректно
        if (name.isNotEmpty() && lastName.isNotEmpty() && age != null && weight != null && height != null) {
            // Отправляем данные через ViewModel
            personalInfoViewModel.saveOrUpdatePersonalInfo(age, sex, weight, height)

            // Сохраняем дополнительные данные (например, имя и фамилию) в SharedPreferences
            saveUserName(name, lastName)
        } else {
            // Показываем ошибку, если данные невалидны
            Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserName(name: String, lastName: String) {
        // Сохраняем имя и фамилию в SharedPreferences для дальнейшего использования
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("user_name", name)
            putString("user_lastname", lastName)
            apply()
        }
    }

    private fun observeSuccess() {
        personalInfoViewModel.success.observe(this, { success ->
            if (success) {
                // Информируем пользователя об успешном сохранении данных
                Toast.makeText(this, "Information saved successfully!", Toast.LENGTH_SHORT).show()

                // После успешного сохранения перенаправляем пользователя на страницу Home
                startActivity(Intent(this, HomeActivity::class.java))
                finish()  // Закрываем текущую активность
            }
        })
    }

    private fun observeError() {
        personalInfoViewModel.error.observe(this, { errorMessage ->
            errorMessage?.let {
                // Показываем ошибку, если она произошла
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
