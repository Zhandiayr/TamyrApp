package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.personalinfo.MainPersonalInfoRequest
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_info)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        val etAge = findViewById<EditText>(R.id.et_age)
        val etWeight = findViewById<EditText>(R.id.et_weight)
        val etHeight = findViewById<EditText>(R.id.et_height)
        val spSex = findViewById<Spinner>(R.id.sp_sex)
        val btnSave = findViewById<Button>(R.id.btn_save)

        val adapter = ArrayAdapter.createFromResource(
            this, R.array.sex_options, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSex.adapter = adapter

        btnSave.setOnClickListener {
            val age = etAge.text.toString().toIntOrNull() ?: 0
            val weight = etWeight.text.toString().toIntOrNull() ?: 0
            val height = etHeight.text.toString().toIntOrNull() ?: 0
            val sex = spSex.selectedItem.toString()

            if (age > 0 && weight > 0 && height > 0 && (sex == "Male" || sex == "Female")) {
                savePersonalInfo(age, sex, weight, height)
            } else {
                Toast.makeText(this, "Заполните все поля корректно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePersonalInfo(age: Int, sex: String, weight: Int, height: Int) {
        val token = sharedPreferences.getString("access_token", null)
        val userId = sharedPreferences.getLong("user_id", -1)

        if (token.isNullOrEmpty() || userId == -1L) {
            Toast.makeText(this, "Ошибка: отсутствует access_token или userId", Toast.LENGTH_LONG).show()
            return
        }

        val request = MainPersonalInfoRequest(
            userId = userId,
            age = age,
            sex = sex,
            weight = weight,
            height = height
        )

        Log.d("PersonalInfoActivity", "Отправка данных: $request")

        RetrofitInstance.personalInfoApi.saveOrUpdatePersonalInfo("Bearer $token", request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        sharedPreferences.edit().putBoolean("personal_info_filled", true).apply()
                        Toast.makeText(this@PersonalInfoActivity, "Анкета сохранена!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@PersonalInfoActivity, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(this@PersonalInfoActivity, "Ошибка сохранения: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@PersonalInfoActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
