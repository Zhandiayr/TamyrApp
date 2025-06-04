package com.example.tamyrapp2.data.network.miband

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

data class FakeMiBandData(
    val heartRate: Int,
    val stepsKm: Double,
    val sleepHours: Int,
    val timestamp: String
)

class MiBandFakeDataGenerator(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("fake_miband_data", Context.MODE_PRIVATE)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    private val _liveData = MutableLiveData<FakeMiBandData>()
    val liveData: LiveData<FakeMiBandData> = _liveData

    private var job: Job? = null
    private var currentHeartRate = 80
    private var currentStepsKm = 0.0
    private var currentSleepHours = 7
    private var lastSleepChangeDay = -1

    init {
        loadFromPrefs()
    }

    fun startGenerating() {
        job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                generateData()
                delay(60_000L) // 1 min
            }
        }
    }

    fun stopGenerating() {
        job?.cancel()
    }

    private fun generateData() {
        val calendar = Calendar.getInstance()

        // Меняем пульс каждую минуту на случайное значение между -3, +2 или +5 в диапазоне 70..95
        val change = listOf(-3, 2, 5).random()
        currentHeartRate = (currentHeartRate + change).coerceIn(70, 95)

        // Шаги растут каждую минуту, добавляем от 0.01 до 0.05 км, чтобы было реалистично
        val stepsIncrement = (1..5).random() / 100.0
        currentStepsKm += stepsIncrement

        // Сон меняется раз в день между 6 и 9 часами
        val todayDay = calendar.get(Calendar.DAY_OF_YEAR)
        if (todayDay != lastSleepChangeDay) {
            currentSleepHours = (6..9).random()
            lastSleepChangeDay = todayDay
        }

        val timestamp = dateFormat.format(Date())

        val data = FakeMiBandData(
            heartRate = currentHeartRate,
            stepsKm = currentStepsKm,
            sleepHours = currentSleepHours,
            timestamp = timestamp
        )
        saveToPrefs(data)
        _liveData.postValue(data)
    }

    private fun saveToPrefs(data: FakeMiBandData) {
        prefs.edit()
            .putInt("heartRate", data.heartRate)
            .putFloat("stepsKm", data.stepsKm.toFloat())
            .putInt("sleepHours", data.sleepHours)
            .putString("timestamp", data.timestamp)
            .putInt("lastSleepChangeDay", lastSleepChangeDay)
            .apply()
    }

    private fun loadFromPrefs() {
        currentHeartRate = prefs.getInt("heartRate", 80)
        currentStepsKm = prefs.getFloat("stepsKm", 0f).toDouble()
        currentSleepHours = prefs.getInt("sleepHours", 7)
        lastSleepChangeDay = prefs.getInt("lastSleepChangeDay", -1)
    }
}