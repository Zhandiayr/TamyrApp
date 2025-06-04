package com.example.tamyrapp2.presentation.miband

import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentLinkedQueue
object MiBandDataBufferManager {
    private val buffer = mutableListOf<MiBandDataRequest>()

    // Добавляем данные в буфер
    fun addData(data: MiBandDataRequest) {
        buffer.add(data)
    }

    // Метод для запуска периодической отправки данных
    fun startPeriodicSending(accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(600_000L)  // Отправка данных каждые 10 минут
                sendBufferedData(accessToken)
            }
        }
    }

    private suspend fun sendBufferedData(accessToken: String) {
        val dataToSend = buffer.toList()
        buffer.clear()

        // Отправка данных в бэкэнд
        dataToSend.forEach { request ->
            RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", request)
                .enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        // Обработка успешной отправки
                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        // Обработка ошибки
                    }
                })
        }
    }
}
