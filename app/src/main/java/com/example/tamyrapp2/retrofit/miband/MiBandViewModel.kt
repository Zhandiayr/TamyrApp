package com.example.tamyrapp2.retrofit.miband

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.retrofit.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MiBandViewModel(application: Application) : AndroidViewModel(application) {
    private val miBandService = MiBandService(application)
    private val api = RetrofitInstance.miBandApi

    val heartRate = MutableLiveData<Int>()
    val steps = MutableLiveData<Int>()
    val sleepHours = MutableLiveData<Int>()
    val caloriesBurned = MutableLiveData<Int>()
    val distance = MutableLiveData<Int>()
    val batteryLevel = MutableLiveData<Int>()
    val success = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    fun connectToMiBand(device: BluetoothDevice) {
        miBandService.connect(device) { connected ->
            if (!connected) error.postValue("Не удалось подключиться к Mi Band")
        }
    }

    fun sendDataToBackend(token: String) {
        if (token.isEmpty() || token.isBlank()) {
            Log.e("MiBandViewModel", "❌ Ошибка: Токен пустой!")
            error.postValue("Ошибка аутентификации: пустой токен")
            return
        }

        Log.d("MiBandViewModel", "📡 Отправка данных с токеном: $token") // Логируем токен

        val request = MiBandDataRequest(
            heartRate = heartRate.value ?: 0,
            steps = steps.value ?: 0,
            sleepHours = sleepHours.value ?: 0,
            caloriesBurned = caloriesBurned.value ?: 0,
            distance = distance.value ?: 0,
            batteryLevel = batteryLevel.value ?: 0
        )

        api.sendMiBandData("Bearer $token", request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    success.postValue(true)
                    Log.d("MiBandViewModel", "✅ Данные успешно отправлены")
                } else {
                    Log.e("MiBandViewModel", "❌ Ошибка на сервере: ${response.errorBody()?.string()}")
                    error.postValue("Ошибка сервера: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MiBandViewModel", "❌ Ошибка сети: ${t.message}")
                error.postValue("Ошибка сети: ${t.message}")
            }
        })
    }


    fun disconnect() {
        miBandService.disconnect()
    }
}
