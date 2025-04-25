/*package com.example.tamyrapp2.retrofit.miband

import android.util.Log
import com.example.tamyrapp2.retrofit.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class FakeMiBandService {
    private val api = RetrofitInstance.miBandApi

    companion object {
        private const val TAG = "FakeMiBandService"
    }

    /**
     * Фейковое подключение к Mi Band (симуляция подключения)
     */
    fun connectFakeDevice(callback: (Boolean) -> Unit) {
        Log.d(TAG, "✅ Фейковый Mi Band подключен")
        callback(true) // Симуляция успешного подключения
    }

    /**
     * Отправка фейковых данных на бэкенд
     */
    fun sendFakeDataToBackend() {
        val fakeHeartRate = Random.nextInt(60, 100) // Пульс
        val fakeSteps = Random.nextInt(5000, 15000) // Шаги
        val fakeSleepHours = Random.nextInt(4, 9) // Сон в часах
        val fakeCalories = Random.nextInt(150, 600) // Калории
        val fakeDistance = Random.nextInt(2, 10) // Дистанция (км)
        val fakeBattery = Random.nextInt(20, 100) // Заряд батареи

        val request = MiBandDataRequest(
            heartRate = fakeHeartRate,
            steps = fakeSteps,
            sleepHours = fakeSleepHours,
            caloriesBurned = fakeCalories,
            distance = fakeDistance,
            batteryLevel = fakeBattery
        )

        api.sendMiBandData("Bearer ACCESS_TOKEN", request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "✅ Фейковые данные отправлены: " +
                        "Пульс: $fakeHeartRate, Шаги: $fakeSteps, " +
                        "Сон: $fakeSleepHours ч, Калории: $fakeCalories, " +
                        "Дистанция: $fakeDistance км, Батарея: $fakeBattery%")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "❌ Ошибка отправки фейковых данных: ${t.message}")
            }
        })
    }
}
*/
package com.example.tamyrapp2.retrofit.miband

import android.util.Log
import com.example.tamyrapp2.retrofit.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import kotlin.random.Random

class FakeMiBandService {
    private val api = RetrofitInstance.miBandApi

    companion object {
        private const val TAG = "FakeMiBandService"
    }

    /**
     * 📡 Фейковое подключение к Mi Band (симуляция подключения)
     */
    fun connectFakeDevice(callback: (Boolean) -> Unit) {
        Log.d(TAG, "✅ Фейковый Mi Band подключен")
        callback(true) // Симуляция успешного подключения
    }

    /**
     * 📤 Отправка фейковых данных на бэкенд
     */
    fun sendFakeDataToBackend(userId: Long) {
        val fakeHeartRate = Random.nextInt(60, 100) // Пульс
        val fakeSteps = Random.nextInt(5000, 15000) // Шаги
        val fakeSleepHours = Random.nextInt(4, 9) // Сон в часах
        val fakeCalories = Random.nextInt(150, 600) // Калории
        val fakeDistance = Random.nextInt(2, 10) // Дистанция (км)
        val fakeBattery = Random.nextInt(20, 100) // Заряд батареи
        val timestamp = Date(System.currentTimeMillis()).toString() // ✅ Используем Date вместо LocalDateTime

        val request = MiBandDataRequest(
            userId = userId, // Передаем userId
            heartRate = fakeHeartRate,
            steps = fakeSteps,
            sleepHours = fakeSleepHours,
            caloriesBurned = fakeCalories,
            distance = fakeDistance,
            batteryLevel = fakeBattery,
            timestamp = timestamp // ✅ Передаем в виде строки
        )

        api.sendMiBandData("Bearer ACCESS_TOKEN", request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "✅ Фейковые данные отправлены: " +
                            "Пульс: $fakeHeartRate, Шаги: $fakeSteps, " +
                            "Сон: $fakeSleepHours ч, Калории: $fakeCalories, " +
                            "Дистанция: $fakeDistance км, Батарея: $fakeBattery%, " +
                            "Время: $timestamp")
                } else {
                    Log.e(TAG, "⚠️ Ошибка отправки данных. Код ответа: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "❌ Ошибка сети при отправке фейковых данных: ${t.message}")
            }
        })
    }
}
