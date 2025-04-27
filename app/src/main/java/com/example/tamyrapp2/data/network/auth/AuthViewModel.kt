package com.example.tamyrapp2.data.network.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.example.tamyrapp2.data.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val workManager = WorkManager.getInstance(application)

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _accessToken = MutableLiveData<String?>()
    val accessToken: LiveData<String?> = _accessToken

    private val _refreshToken = MutableLiveData<String?>()
    val refreshToken: LiveData<String?> = _refreshToken

    private val _userId = MutableLiveData<Long?>()
    val userId: LiveData<Long?> = _userId

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    val userFirstName = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()

    private fun cleanupObsoleteWorkers() {
        val workManager = WorkManager.getInstance(getApplication<Application>())
        workManager.pruneWork() // Удаляет все старые сломанные задачи
    }

    fun registerUser(username: String, email: String, password: String, firstName: String, lastName: String) {
        val request = RegisterRequest(username, email, password, firstName, lastName)
        RetrofitInstance.authApi.registerUser(request).enqueue(object : Callback<Void> { // <-- исправил тут
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    userFirstName.value = firstName
                    userEmail.value = email
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
    /*
    fun loginUser(username: String, password: String) {
        val request = LoginRequest(username, password)
        RetrofitInstance.authApi.loginUser(request).enqueue(object : Callback<AuthResponse> { // <-- исправил тут
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()

                    _accessToken.value = authResponse?.accessToken
                    _refreshToken.value = authResponse?.refreshToken
                    _userId.value = authResponse?.userId

                    with(sharedPreferences.edit()) {
                        putString("access_token", authResponse?.accessToken)
                        putString("refresh_token", authResponse?.refreshToken)
                        putLong("user_id", authResponse?.userId ?: -1)
                        apply()
                    }

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
     */
    fun loginUser(username: String, password: String) {
        val request = LoginRequest(username, password)
        RetrofitInstance.authApi.loginUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()

                    _accessToken.value = authResponse?.accessToken
                    _refreshToken.value = authResponse?.refreshToken
                    _userId.value = authResponse?.userId

                    with(sharedPreferences.edit()) {
                        putString("access_token", authResponse?.accessToken)
                        putString("refresh_token", authResponse?.refreshToken)
                        putLong("user_id", authResponse?.userId ?: -1)
                        apply()
                    }

                    cleanupObsoleteWorkers() // <--- Сначала чистим мусор
                    scheduleTokenRefresh()   // <--- Потом ставим новый воркер
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

        RetrofitInstance.authApi.refreshToken("Bearer $refreshToken").enqueue(object : Callback<AuthResponse> { // <-- исправил тут
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
    /*
    private fun scheduleTokenRefresh() {
        // ✅ Сначала отменяем ВСЕ старые задачи
        workManager.cancelAllWork()

        // ✅ Потом ставим новую задачу на обновление токена
        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "TokenRefreshWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    */
    private fun scheduleTokenRefresh() {
        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(1, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(getApplication<Application>()).enqueueUniquePeriodicWork(
            "TokenRefreshWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}
