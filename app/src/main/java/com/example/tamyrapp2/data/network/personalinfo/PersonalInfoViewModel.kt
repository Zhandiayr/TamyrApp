package com.example.tamyrapp2.data.network.personalinfo

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PersonalInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun saveOrUpdatePersonalInfo(age: Int, sex: String, weight: Int, height: Int) {
        val accessToken = sharedPreferences.getString("access_token", null)
        val userId = sharedPreferences.getLong("user_id", -1)

        if (accessToken.isNullOrEmpty() || userId == -1L) {
            _error.value = "Ошибка авторизации: токен или ID пользователя отсутствует"
            return
        }

        val request = MainPersonalInfoRequest(
            userId = userId,
            age = age,
            sex = sex,
            weight = weight,
            height = height
        )

        RetrofitInstance.personalInfoApi.saveOrUpdatePersonalInfo("Bearer $accessToken", request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _success.value = true
                    } else {
                        _error.value = response.errorBody()?.string() ?: "Ошибка сохранения: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _error.value = "Ошибка сети: ${t.message}"
                }
            })
    }
}
