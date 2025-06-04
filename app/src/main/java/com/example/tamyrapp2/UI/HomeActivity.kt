package com.example.tamyrapp2.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.presentation.miband.MiBandViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.tamyrapp2.data.network.miband.MiBandFakeDataGenerator
import com.example.tamyrapp2.data.network.miband.FakeMiBandData
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import com.google.gson.Gson
import kotlinx.coroutines.*
import com.example.tamyrapp2.data.network.RetrofitInstance


class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private val miBandViewModel: MiBandViewModel by viewModels()
    private lateinit var fakeDataGenerator: MiBandFakeDataGenerator

    private lateinit var tvHeartValue: TextView
    private lateinit var tvStepsValue: TextView
    private lateinit var tvSleepValue: TextView
    private lateinit var tvDaysAgo: TextView
    private lateinit var ivRefresh: ImageView

    private val accumulatedData = mutableListOf<FakeMiBandData>()
    private var sendJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        // Инициализация view для отображения данных
        tvHeartValue = findViewById(R.id.tv_heart_value)
        tvStepsValue = findViewById(R.id.tv_steps_value)
        tvSleepValue = findViewById(R.id.tv_sleep_value)
        tvDaysAgo = findViewById(R.id.tv_days_ago)
        ivRefresh = findViewById(R.id.iv_refresh)

        // Инициализация генератора фейковых данных
        fakeDataGenerator = MiBandFakeDataGenerator(this)

        // Подписываемся на LiveData с фейковыми данными
        fakeDataGenerator.liveData.observe(this) { data ->
            updateUIWithFakeData(data)
            accumulateData(data)
        }

        // Запуск генерации фейковых данных
        fakeDataGenerator.startGenerating()

        // Запускаем корутину для отправки накопленных данных каждые 10 минут
        sendJob = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                delay(60_000L) // 10 минут
                sendAccumulatedData()
            }
        }

        // Подписываемся на LiveData с данными Mi Band из backend
        miBandViewModel.dataList.observe(this) { dataList ->
            if (!dataList.isNullOrEmpty()) {
                val latestData = dataList.last()
                tvHeartValue.text = "${latestData.heartRate} bpm"
                val distanceKm = latestData.steps * 0.7 / 1000.0
                tvStepsValue.text = String.format("%.2f km", distanceKm)
                tvSleepValue.text = "0 hrs" // Заглушка, sleep пока нет
                tvDaysAgo.text = "Just now"
            }
        }

        // Обработка ошибок и успехов (например, показать Toast)
        miBandViewModel.error.observe(this) { errorMsg ->
            errorMsg?.let {
                // Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        miBandViewModel.success.observe(this) { success ->
            if (success) {
                // Можно уведомить пользователя об успешной отправке
            }
        }

        // Загрузка данных с backend при старте
        miBandViewModel.getUserMiBandData()

        // Кнопка обновления данных вручную
        ivRefresh.setOnClickListener {
            miBandViewModel.getUserMiBandData()
        }

        // Bottom Navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }

    private fun updateUIWithFakeData(data: FakeMiBandData) {
        tvHeartValue.text = "${data.heartRate} bpm"
        tvStepsValue.text = String.format("%.2f km", data.stepsKm)
        tvSleepValue.text = "${data.sleepHours} hrs"
        tvDaysAgo.text = "Just now"
    }

    private fun accumulateData(data: FakeMiBandData) {
        synchronized(accumulatedData) {
            accumulatedData.add(data)
        }
    }

    private fun sendAccumulatedData() {
        val dataToSend: List<FakeMiBandData>
        synchronized(accumulatedData) {
            if (accumulatedData.isEmpty()) return
            dataToSend = accumulatedData.toList()
            accumulatedData.clear()
        }

        val accessToken = sharedPreferences.getString("access_token", null) ?: return
        val userId = sharedPreferences.getLong("user_id", -1)
        if (userId == -1L) return

        for (item in dataToSend) {
            val heartRateListJson = Gson().toJson(List(7) { item.heartRate })
            val stepsInt = (item.stepsKm * 1000).toInt()

            val request = MiBandDataRequest(
                userId = userId,
                heartRateListJson = heartRateListJson,
                steps = stepsInt,
                caloriesBurned = 0,
                distance = stepsInt,
                batteryLevel = 0,
                timestamp = item.timestamp
            )

            RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", request)
                .enqueue(object : retrofit2.Callback<Void> {
                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        // Опционально обработать ответ
                    }
                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        // Опционально обработать ошибку
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fakeDataGenerator.stopGenerating()
        sendJob?.cancel()
    }
}
