package com.example.tamyrapp2.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.retrofit.miband.MiBandViewModel

class MiBandDataActivity : AppCompatActivity() {
    private val viewModel: MiBandViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miband_data)

        sharedPreferences = getSharedPreferences("miband_prefs", Context.MODE_PRIVATE)

        val tvHeartRate = findViewById<TextView>(R.id.tv_heart_rate)
        val tvSteps = findViewById<TextView>(R.id.tv_steps)
        val tvSleep = findViewById<TextView>(R.id.tv_sleep)

        // Обновляем UI данными из ViewModel
        viewModel.heartRate.observe(this) { heartRate ->
            tvHeartRate.text = "Пульс: $heartRate уд/мин"
        }

        viewModel.steps.observe(this) { steps ->
            tvSteps.text = "Шаги: $steps"
        }

        viewModel.sleepHours.observe(this) { sleep ->
            tvSleep.text = "Сон: $sleep ч."
        }
    }
}
