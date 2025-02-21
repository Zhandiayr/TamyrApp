package com.example.tamyrapp2.retrofit.miband

import android.app.Application
import android.bluetooth.BluetoothDevice
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
                success.postValue(response.isSuccessful)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                error.postValue("Ошибка отправки данных: ${t.message}")
            }
        })
    }

    fun disconnect() {
        miBandService.disconnect()
    }
}
