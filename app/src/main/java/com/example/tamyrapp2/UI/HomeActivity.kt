/*package com.example.tamyrapp2.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.lifecycle.lifecycleScope
import com.example.tamyrapp2.R
import com.example.tamyrapp2.UI.LoginActivity
import com.example.tamyrapp2.UI.NotificationsActivity
import com.example.tamyrapp2.UI.SettingsActivity
import com.example.tamyrapp2.UI.StatisticsActivity
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.health.HealthConnectManager
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import com.example.tamyrapp2.presentation.miband.MiBandViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val miBandViewModel: MiBandViewModel by viewModels()
    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var manager: HealthConnectManager
    private lateinit var permissionLauncher: ActivityResultLauncher<Intent>

    private val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        healthConnectClient = HealthConnectClient.getOrCreate(this)
        manager = HealthConnectManager(this)

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            lifecycleScope.launch {
                val granted = healthConnectClient.permissionController.getGrantedPermissions()
                if (granted.containsAll(permissions)) {
                    syncHealthDataToBackend()
                } else {
                    Toast.makeText(this@HomeActivity, "Нет разрешений для чтения данных", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<Button>(R.id.button_logout).setOnClickListener { logOut() }
        findViewById<Button>(R.id.button_send_fake_data).setOnClickListener { miBandViewModel.sendFakeDeviceData() }

        lifecycleScope.launch {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (!granted.containsAll(permissions)) {
                val intent = healthConnectClient.permissionController.скуфеу
                permissionLauncher.launch(intent)
            } else {
                syncHealthDataToBackend()
            }
        }


        setupBottomNavigation()
    }

    private fun syncHealthDataToBackend() {
        lifecycleScope.launch {
            val data = manager.readLatestHealthData()
            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
            val userId = sharedPreferences.getLong("user_id", -1)
            val accessToken = sharedPreferences.getString("access_token", null)

            val request = MiBandDataRequest(
                userId = userId,
                heartRate = data.heartRate,
                steps = data.steps,
                caloriesBurned = data.calories,
                distance = (data.steps * 0.7).toInt(),
                batteryLevel = 100,
                timestamp = timestamp
            )

            RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(this@HomeActivity, "Данные синхронизированы", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@HomeActivity, "Ошибка синхронизации", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun logOut() {
        sharedPreferences.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
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
}
*/
package com.example.tamyrapp2.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.permission.PermissionController
import androidx.health.connect.client.permission.createRequestPermissionResultContract
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.lifecycle.lifecycleScope
import com.example.tamyrapp2.R
import com.example.tamyrapp2.UI.LoginActivity
import com.example.tamyrapp2.UI.NotificationsActivity
import com.example.tamyrapp2.UI.SettingsActivity
import com.example.tamyrapp2.UI.StatisticsActivity
import com.example.tamyrapp2.data.network.RetrofitInstance
import com.example.tamyrapp2.data.network.health.HealthConnectManager
import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import com.example.tamyrapp2.presentation.miband.MiBandViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val miBandViewModel: MiBandViewModel by viewModels()
    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var manager: HealthConnectManager
    private lateinit var permissionLauncher: ActivityResultLauncher<Set<HealthPermission>>

    private val permissions = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        healthConnectClient = HealthConnectClient.getOrCreate(this)
        manager = HealthConnectManager(this)

        permissionLauncher = registerForActivityResult(
            PermissionController.createRequestPermissionResultContract()
        ) { grantedPermissions ->
            if (grantedPermissions.containsAll(permissions)) {
                syncHealthDataToBackend()
            } else {
                Toast.makeText(this, "Нет разрешений для чтения данных", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.button_logout).setOnClickListener { logOut() }
        findViewById<Button>(R.id.button_send_fake_data).setOnClickListener { miBandViewModel.sendFakeDeviceData() }

        lifecycleScope.launch {
            val granted = healthConnectClient.permissionController.getGrantedPermissions()
            if (!granted.containsAll(permissions)) {
                permissionLauncher.launch(permissions)
            } else {
                syncHealthDataToBackend()
            }
        }

        setupBottomNavigation()
    }

    private fun syncHealthDataToBackend() {
        lifecycleScope.launch {
            val data = manager.readLatestHealthData()
            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
            val userId = sharedPreferences.getLong("user_id", -1)
            val accessToken = sharedPreferences.getString("access_token", null)

            val request = MiBandDataRequest(
                userId = userId,
                heartRate = data.heartRate,
                steps = data.steps,
                caloriesBurned = data.calories,
                distance = (data.steps * 0.7).toInt(),
                batteryLevel = 100,
                timestamp = timestamp
            )

            RetrofitInstance.miBandApi.sendMiBandData("Bearer $accessToken", request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(this@HomeActivity, "Данные синхронизированы", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@HomeActivity, "Ошибка синхронизации", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun logOut() {
        sharedPreferences.edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
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
}