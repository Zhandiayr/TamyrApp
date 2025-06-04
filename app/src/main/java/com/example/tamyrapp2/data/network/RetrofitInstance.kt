package com.example.tamyrapp2.data.network

import com.example.tamyrapp2.data.network.auth.AuthApiService
import com.example.tamyrapp2.data.network.lifestyle.LifestyleApiService
import com.example.tamyrapp2.data.network.miband.MiBandApiService
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoApiService
import com.example.tamyrapp2.data.network.survey.SurveyApiService
import com.example.tamyrapp2.data.network.user.UserApiService

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.*
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val personalInfoApi: PersonalInfoApiService by lazy {
        retrofit.create(PersonalInfoApiService::class.java)
    }

    val miBandApi: MiBandApiService by lazy {
        retrofit.create(MiBandApiService::class.java)
    }

    val lifestyleApi: LifestyleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LifestyleApiService::class.java)
    }
    val surveyApi: SurveyApiService by lazy {
        retrofit.create(SurveyApiService::class.java)
    }

    val userApi: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }
}

// --- Добавляем менеджер для буферизации и отправки данных раз в 10 минут ---
object MiBandDataBufferManager {
    private val buffer = mutableListOf<MiBandDataRequest>()
    private var isSending = false

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun addData(data: MiBandDataRequest) {
        synchronized(buffer) {
            buffer.add(data)
        }
    }

    fun startPeriodicSending(accessToken: String) {
        coroutineScope.launch {
            while (true) {
                delay(10 * 60 * 1000L) // 10 минут
                sendBufferedData(accessToken)
            }
        }
    }

    private suspend fun sendBufferedData(accessToken: String) {
        if (isSending) return
        isSending = true

        val dataToSend: List<MiBandDataRequest>
        synchronized(buffer) {
            dataToSend = buffer.toList()
            buffer.clear()
        }

        for (data in dataToSend) {
            try {
                val response = RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", data).execute()
                if (!response.isSuccessful) {
                    // Если ошибка — вернуть данные обратно в буфер
                    synchronized(buffer) {
                        buffer.add(data)
                    }
                }
            } catch (e: Exception) {
                // При исключении вернуть данные обратно в буфер
                synchronized(buffer) {
                    buffer.add(data)
                }
            }
        }
        isSending = false
    }
}
