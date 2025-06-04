package com.example.tamyrapp2.presentation.miband

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.miband.FakeMiBandDevice
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import com.example.tamyrapp2.data.network.miband.MiBandDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson

class MiBandViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _dataList = MutableLiveData<List<MiBandDataResponse>?>()
    val dataList: LiveData<List<MiBandDataResponse>?> = _dataList

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun addFakeDeviceDataToBuffer() {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId == -1L) {
            _error.value = "Error: userId not found"
            return
        }

        val fakeDevice = FakeMiBandDevice()

        val distance = (fakeDevice.steps * 0.7).toInt()
        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

        val heartRateList = List(7) { fakeDevice.heartRate }
        val heartRateListJson = Gson().toJson(heartRateList)

        val request = MiBandDataRequest(
            userId = userId,
            heartRateListJson = heartRateListJson,
            steps = fakeDevice.steps,
            caloriesBurned = fakeDevice.caloriesBurned,
            distance = distance,
            batteryLevel = fakeDevice.batteryLevel,
            timestamp = timestamp
        )

        MiBandDataBufferManager.addData(request)
    }

    fun startSendingBufferPeriodically() {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        MiBandDataBufferManager.startPeriodicSending(accessToken)
    }

    fun getUserMiBandData() {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId == -1L) {
            _error.value = "Error: userId not found"
            return
        }

        RetrofitInstance.miBandApi.getMiBandData("Bearer $accessToken", userId)
            .enqueue(object : Callback<List<MiBandDataResponse>> {
                override fun onResponse(
                    call: Call<List<MiBandDataResponse>>,
                    response: Response<List<MiBandDataResponse>>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body.isNullOrEmpty()) {
                            _error.value = "No data received from server"
                            _dataList.value = emptyList()
                        } else {
                            _dataList.value = body
                        }
                    } else {
                        _error.value = "Error loading data: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<MiBandDataResponse>>, t: Throwable) {
                    _error.value = "Network error: ${t.message}"
                }
            })
    }
}
