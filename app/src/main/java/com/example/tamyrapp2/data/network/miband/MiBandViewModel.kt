/*package com.example.tamyrapp2.presentation.miband

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import com.example.tamyrapp2.data.network.miband.MiBandDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MiBandViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _dataList = MutableLiveData<List<MiBandDataResponse>>()
    val dataList: LiveData<List<MiBandDataResponse>> = _dataList

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun sendTestDeviceData() {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId == -1L) {
            _error.value = "Ошибка: не найден userId"
            return
        }

        // ✅ Правильное создание времени без LocalDateTime
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

        val fakeData = MiBandDataRequest(
            userId = userId,
            heartRate = 72,
            steps = 5000,
            sleepHours = 7,
            caloriesBurned = 250,
            distance = 3500,
            batteryLevel = 80,
            timestamp = timestamp
        )

        RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", fakeData)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _success.value = true
                    } else {
                        _error.value = "Ошибка отправки данных: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _error.value = "Ошибка сети: ${t.message}"
                }
            })
    }

    fun getUserMiBandData() {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId == -1L) {
            _error.value = "Ошибка: не найден userId"
            return
        }

        RetrofitInstance.miBandApi.getMiBandData("Bearer $accessToken", userId)
            .enqueue(object : Callback<List<MiBandDataResponse>> {
                override fun onResponse(
                    call: Call<List<MiBandDataResponse>>,
                    response: Response<List<MiBandDataResponse>>
                ) {
                    if (response.isSuccessful) {
                        _dataList.value = response.body()
                    } else {
                        _error.value = "Ошибка загрузки данных: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<MiBandDataResponse>>, t: Throwable) {
                    _error.value = "Ошибка сети: ${t.message}"
                }
            })
    }
}
*/
package com.example.tamyrapp2.presentation.miband

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import com.example.tamyrapp2.data.network.miband.MiBandDataResponse
import com.example.tamyrapp2.fake.FakeMiBandDevice
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MiBandViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _dataList = MutableLiveData<List<MiBandDataResponse>>()
    val dataList: LiveData<List<MiBandDataResponse>> = _dataList

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun sendFakeDeviceData() {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId == -1L) {
            _error.value = "Ошибка: не найден userId"
            return
        }

        val fakeDevice = FakeMiBandDevice() // Создаем фейковое устройство

        val distance = (fakeDevice.steps * 0.7).toInt() // Расчёт дистанции
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

        val request = MiBandDataRequest(
            userId = userId,
            heartRate = fakeDevice.heartRate,
            steps = fakeDevice.steps,
            caloriesBurned = fakeDevice.caloriesBurned,
            distance = distance,
            batteryLevel = fakeDevice.batteryLevel,
            timestamp = timestamp
        )

        RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _success.value = true
                    } else {
                        _error.value = "Ошибка отправки данных: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _error.value = "Ошибка сети: ${t.message}"
                }
            })
    }
}
