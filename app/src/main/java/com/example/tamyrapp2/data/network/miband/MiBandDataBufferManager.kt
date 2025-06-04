package com.example.tamyrapp2.presentation.miband

import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.ConcurrentLinkedQueue

object MiBandDataBufferManager {

    private val buffer = ConcurrentLinkedQueue<MiBandDataRequest>()
    private var sendJob: Job? = null
    private var accessToken: String? = null

    fun addData(data: MiBandDataRequest) {
        buffer.add(data)
    }

    fun startPeriodicSending(token: String) {
        accessToken = token
        if (sendJob == null || sendJob?.isCancelled == true) {
            sendJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    delay(600_000L) // 10 минут
                    sendBufferedData()
                }
            }
        }
    }

    private suspend fun sendBufferedData() {
        val token = accessToken ?: return
        while (buffer.isNotEmpty()) {
            val data = buffer.poll() ?: continue
            val call = RetrofitInstance.miBandApi.sendMiBandData("Bearer $token", data)
            try {
                val response = call.execute()
                if (!response.isSuccessful) {
                    // Если отправка неуспешна, вернуть данные обратно в буфер
                    buffer.add(data)
                    break // остановить попытки, ждем следующего цикла
                }
            } catch (e: Exception) {
                // При ошибке сети вернуть данные в буфер
                buffer.add(data)
                break
            }
        }
    }

    fun stopSending() {
        sendJob?.cancel()
        sendJob = null
    }
}
