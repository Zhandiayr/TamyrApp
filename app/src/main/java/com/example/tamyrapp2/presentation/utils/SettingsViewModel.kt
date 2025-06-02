package com.example.tamyrapp2.presentation.utils

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.user.UserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Application.MODE_PRIVATE)

    fun loadUserEmail() {
        val token = sharedPreferences.getString("access_token", null)
        if (token == null) {
            _error.value = "Токен не найден"
            return
        }
        val authHeader = "Bearer $token"

        RetrofitInstance.userApi.getCurrentUser(authHeader).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    _email.value = response.body()?.email ?: "Email не указан"
                } else {
                    _error.value = "Ошибка сервера: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }
}
