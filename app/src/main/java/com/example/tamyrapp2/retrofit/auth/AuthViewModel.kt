/*package com.example.tamyrapp2.retrofit.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val workManager = WorkManager.getInstance(application)

    private val _success = MutableLiveData<Boolean>() // ✅ Добавлено для обработки успешной регистрации
    val success: LiveData<Boolean> = _success

    private val _accessToken = MutableLiveData<String?>()
    val accessToken: LiveData<String?> = _accessToken

    private val _refreshToken = MutableLiveData<String?>()
    val refreshToken: LiveData<String?> = _refreshToken

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun registerUser(username: String, email: String, password: String, firstName: String, lastName: String) {
        val request = RegisterRequest(username, email, password, firstName, lastName)
        RetrofitInstance.api.registerUser(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _success.value = true // ✅ Успешная регистрация
                } else {
                    _error.value = response.errorBody()?.string() ?: "Ошибка регистрации: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun loginUser(username: String, password: String) {
        val request = LoginRequest(username, password)
        RetrofitInstance.api.loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    val refresh = response.body()?.refreshToken

                    _accessToken.value = token
                    _refreshToken.value = refresh

                    // ✅ Сохраняем токены
                    sharedPreferences.edit().putString("access_token", token).apply()
                    sharedPreferences.edit().putString("refresh_token", refresh).apply()

                    // ✅ Запускаем обновление токена
                    scheduleTokenRefresh()
                } else {
                    _error.value = response.errorBody()?.string() ?: "Ошибка входа: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun refreshAccessToken() {
        val refreshToken = sharedPreferences.getString("refresh_token", null)
        if (refreshToken.isNullOrEmpty()) {
            _error.value = "Отсутствует refresh токен"
            return
        }

        RetrofitInstance.api.refreshToken("Bearer $refreshToken").enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val newToken = response.body()?.accessToken
                    _accessToken.value = newToken

                    // ✅ Обновляем токен в SharedPreferences
                    sharedPreferences.edit().putString("access_token", newToken).apply()
                } else {
                    _error.value = response.errorBody()?.string() ?: "Ошибка обновления токена: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    private fun scheduleTokenRefresh() {
        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "TokenRefreshWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
*/
package com.example.tamyrapp2.retrofit.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val workManager = WorkManager.getInstance(application)
    private val api = RetrofitInstance.authApi

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _accessToken = MutableLiveData<String?>()
    val accessToken: LiveData<String?> = _accessToken

    private val _refreshToken = MutableLiveData<String?>()
    val refreshToken: LiveData<String?> = _refreshToken

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun registerUser(username: String, email: String, password: String, firstName: String, lastName: String) {
        val request = RegisterRequest(username, email, password, firstName, lastName)
        api.registerUser(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _error.value = response.errorBody()?.string() ?: "Ошибка регистрации: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun loginUser(username: String, password: String) {
        val request = LoginRequest(username, password)
        api.loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    val refresh = response.body()?.refreshToken

                    _accessToken.value = token
                    _refreshToken.value = refresh

                    sharedPreferences.edit().putString("access_token", token).apply()
                    sharedPreferences.edit().putString("refresh_token", refresh).apply()

                    scheduleTokenRefresh()
                } else {
                    _error.value = response.errorBody()?.string() ?: "Ошибка входа: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun refreshAccessToken() {
        val refreshToken = sharedPreferences.getString("refresh_token", null)
        if (refreshToken.isNullOrEmpty()) {
            _error.value = "Отсутствует refresh токен"
            return
        }

        api.refreshToken("Bearer $refreshToken").enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val newToken = response.body()?.accessToken
                    _accessToken.value = newToken
                    sharedPreferences.edit().putString("access_token", newToken).apply()
                } else {
                    _error.value = response.errorBody()?.string() ?: "Ошибка обновления токена: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    private fun scheduleTokenRefresh() {
        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "TokenRefreshWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
