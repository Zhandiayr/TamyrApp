package com.example.tamyrapp2.presentation.miband

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
import java.util.*

class MiBandViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _dataList = MutableLiveData<List<MiBandDataResponse>>()
    val dataList: LiveData<List<MiBandDataResponse>> = _dataList

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun sendMiBandDataBatch(
        heartRateList: List<Int>,
        steps: Int,
        caloriesBurned: Int,
        distance: Int,
        batteryLevel: Int
    ) {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)
        if (userId == -1L) {
            _error.value = "Error: userId not found"
            return
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

        val request = MiBandDataRequest(
            userId = userId,
            heartRateList = heartRateList,
            steps = steps,
            caloriesBurned = caloriesBurned,
            distance = distance,
            batteryLevel = batteryLevel,
            timestamp = timestamp
        )

        RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        _success.value = true
                    } else {
                        _error.value = "Data transmission error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    _error.value = "Network error: ${t.message}"
                }
            })
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
                        _dataList.value = response.body()
                    } else {
                        _error.value = "Loading data error: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<MiBandDataResponse>>, t: Throwable) {
                    _error.value = "Network error: ${t.message}"
                }
            })
    }
}
