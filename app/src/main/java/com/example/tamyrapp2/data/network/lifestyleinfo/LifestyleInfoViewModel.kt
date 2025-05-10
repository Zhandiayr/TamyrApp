
package com.example.tamyrapp2.presentation.lifestyle

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.lifestyle.LifestyleInfoRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LifestyleInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun saveLifestyleInfo(smokes: Boolean, drinksAlcohol: Boolean, exercises: Boolean, fruitIntake: String) {
        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)

        if (userId == -1L) {
            _error.value = "User ID not found"
            return
        }

        val request = LifestyleInfoRequest(userId, smokes, drinksAlcohol, exercises, fruitIntake)

        RetrofitInstance.lifestyleApi.saveLifestyleInfo("Bearer $accessToken", request)
            .enqueue(object : Callback<LifestyleInfoRequest> {
                override fun onResponse(call: Call<LifestyleInfoRequest>, response: Response<LifestyleInfoRequest>) {
                    if (response.isSuccessful) {
                        _success.value = true
                    } else {
                        _error.value = "Ошибка сохранения: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<LifestyleInfoRequest>, t: Throwable) {
                    _error.value = "Ошибка сети: ${t.message}"
                }
            })
    }
}
