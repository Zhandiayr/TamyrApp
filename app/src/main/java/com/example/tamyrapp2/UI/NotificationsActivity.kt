package com.example.tamyrapp2.UI

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.example.tamyrapp2.R
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoViewModel
import com.example.tamyrapp2.data.network.survey.SurveyDataRequest
import com.example.tamyrapp2.data.network.survey.SurveyReminderWorker
import com.example.tamyrapp2.data.network.survey.SurveyViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationsActivity : AppCompatActivity() {

    private val surveyViewModel: SurveyViewModel by viewModels()
    private val personalInfoViewModel: PersonalInfoViewModel by viewModels()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var listView: ListView
    private lateinit var allUnanswered: List<SurveyDataRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        listView = findViewById(R.id.surveyListView)

        val token = sharedPreferences.getString("access_token", null)
        val userId = sharedPreferences.getLong("user_id", -1L)

        if (token != null && userId != -1L) {
            personalInfoViewModel.fetchUserAge()
            personalInfoViewModel.userAge.observe(this) { userAge ->
                if (userAge > 0) {
                    loadSurveys(userId, token, userAge)
                } else {
                    Toast.makeText(this, "Не удалось получить возраст пользователя", Toast.LENGTH_SHORT).show()
                }
            }
        }

        initBottomNavigation()
    }

    private fun loadSurveys(userId: Long, token: String, userAge: Int) {
        surveyViewModel.fetchUnansweredSurveys(userId, token)
        surveyViewModel.unansweredSurveys.observe(this) { surveys ->

            surveys.forEach {
                Log.d("SURVEY_DEBUG", "ID=${it.surveyId}, TYPE=${it.surveyType}, DAILY=${it.isDaily}, DESC=${it.surveyDescription}")
            }

            val filtered = surveys.filter {
                it.isDaily || (
                        (userAge < 40 && it.surveyType.equals("youth", ignoreCase = true)) ||
                                (userAge >= 40 && it.surveyType.equals("elderly", ignoreCase = true))
                        )
            }

            allUnanswered = filtered

            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                filtered.map { it.surveyDescription ?: "Без описания" }
            )
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val intent = Intent(this, SurveyActivity::class.java)
                intent.putExtra("surveyId", filtered[position].surveyId)
                startActivity(intent)
            }

            scheduleDailySurveyReminderIfNeeded(filtered)
        }
    }

    private fun scheduleDailySurveyReminderIfNeeded(surveys: List<SurveyDataRequest>) {
        if (surveys.none { it.isDaily }) return

        val now = Calendar.getInstance()
        val targetHour = 20
        val currentHour = now.get(Calendar.HOUR_OF_DAY)
        val delay = if (currentHour >= targetHour) {
            24 - (currentHour - targetHour)
        } else {
            targetHour - currentHour
        }

        val request = PeriodicWorkRequestBuilder<SurveyReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay.toLong(), TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "dailySurveyReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    private fun initBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_notifications
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_notifications -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
