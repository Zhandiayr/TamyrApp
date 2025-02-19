package com.example.tamyrapp2.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tamyrapp2.retrofit.AuthResponse
import com.example.tamyrapp2.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TokenRefreshWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPreferences = applicationContext.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val refreshToken = sharedPreferences.getString("refresh_token", null)

        if (refreshToken.isNullOrEmpty()) {
            return Result.failure()
        }

        RetrofitInstance.api.refreshToken("Bearer $refreshToken").enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val newToken = response.body()?.accessToken

                    // ✅ Сохраняем новый токен
                    sharedPreferences.edit().putString("access_token", newToken).apply()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                // Логируем ошибку, но не падаем
            }
        })

        return Result.success()
    }
}
