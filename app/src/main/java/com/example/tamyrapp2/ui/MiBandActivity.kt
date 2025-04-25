/*
package com.example.tamyrapp2.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tamyrapp2.R
import com.example.tamyrapp2.retrofit.miband.FakeMiBandService
import com.example.tamyrapp2.retrofit.miband.MiBandViewModel

class MiBandActivity : AppCompatActivity() {
    private val viewModel: MiBandViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val fakeMiBandService = FakeMiBandService() // Фейковый Mi Band сервис

    companion object {
        private const val REQUEST_SELECT_DEVICE = 2
        private const val REQUEST_BLUETOOTH_PERMISSION = 3
        private const val TAG = "MiBandActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miband)

        sharedPreferences = getSharedPreferences("miband_prefs", Context.MODE_PRIVATE)

        val btnConnect = findViewById<Button>(R.id.btn_connect)
        val btnFakeConnect = findViewById<Button>(R.id.btn_fake_connect) // Кнопка фейкового подключения
        val btnViewData = findViewById<Button>(R.id.btn_view_data)
        val tvDeviceName = findViewById<TextView>(R.id.tv_device_name)

        observeViewModel()

        val savedDevice = sharedPreferences.getString("device_address", null)
        updateUI(savedDevice)

        btnConnect.setOnClickListener {
            if (savedDevice == null) {
                requestBluetoothPermission()
            } else {
                disconnectDevice()
            }
        }

        btnFakeConnect.setOnClickListener {
            connectFakeDevice() // Подключаем фейковый Mi Band
        }

        btnViewData.setOnClickListener {
            startActivity(Intent(this, MiBandDataActivity::class.java))
        }
    }

    /**
     * 📌 Подключение фейкового Mi Band
     */
    private fun connectFakeDevice() {
        fakeMiBandService.connectFakeDevice { connected ->
            if (connected) {
                sharedPreferences.edit().putString("device_address", "FakeMiBand").apply()
                sharedPreferences.edit().putString("device_name", "Фейковый Mi Band").apply()

                updateUI("Фейковый Mi Band")
                Toast.makeText(this, "✅ Фейковый Mi Band подключен", Toast.LENGTH_SHORT).show()

                // Отправка тестовых данных
                fakeMiBandService.sendFakeDataToBackend()
            } else {
                Toast.makeText(this, "❌ Ошибка подключения фейкового Mi Band", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 🔄 Подписка на данные ViewModel
     */
    private fun observeViewModel() {
        viewModel.heartRate.observe(this) { heartRate ->
            Log.d(TAG, "💓 Пульс обновлён: $heartRate уд/мин")
        }

        viewModel.steps.observe(this) { steps ->
            Log.d(TAG, "🚶 Шаги обновлены: $steps")
        }

        viewModel.sleepHours.observe(this) { sleep ->
            Log.d(TAG, "😴 Сон обновлён: $sleep часов")
        }

        viewModel.success.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "✅ Данные успешно отправлены", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, "❌ Ошибка: $errorMessage", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Ошибка: $errorMessage")
            }
        }
    }

    /**
     * 🔄 Обновление UI после выбора устройства
     */
    private fun updateUI(deviceName: String?) {
        val tvDeviceName = findViewById<TextView>(R.id.tv_device_name)
        val btnConnect = findViewById<Button>(R.id.btn_connect)
        val btnViewData = findViewById<Button>(R.id.btn_view_data)

        if (deviceName != null) {
            tvDeviceName.text = "Подключено: $deviceName"
            btnConnect.text = "Отключить"
            btnViewData.isEnabled = true
        } else {
            tvDeviceName.text = "Устройство не подключено"
            btnConnect.text = "+ Подключить устройство"
            btnViewData.isEnabled = false
        }
    }

    /**
     * 🔌 Отключение устройства
     */
    private fun disconnectDevice() {
        sharedPreferences.edit().remove("device_address").apply()
        sharedPreferences.edit().remove("device_name").apply()

        updateUI(null)
    }

    /**
     * 📌 Запрос разрешений для работы с Bluetooth
     */
    private fun requestBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            selectDevice()
        }
    }

    /**
     * 📡 Выбор устройства Bluetooth
     */
    private fun selectDevice() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth не поддерживается", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBtIntent)
        } else {
            val selectDeviceIntent = Intent(this, SelectDeviceActivity::class.java)
            startActivityForResult(selectDeviceIntent, REQUEST_SELECT_DEVICE)
        }
    }

    /**
     * 🎯 Обработка выбора устройства
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_DEVICE && resultCode == Activity.RESULT_OK) {
            val deviceAddress = data?.getStringExtra("device_address")
            val deviceName = data?.getStringExtra("device_name")

            if (deviceAddress != null && deviceName != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Нет разрешений на подключение к устройству", Toast.LENGTH_SHORT).show()
                    return
                }

                try {
                    val bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
                    viewModel.connectToMiBand(bluetoothDevice)

                    sharedPreferences.edit().putString("device_address", deviceAddress).apply()
                    sharedPreferences.edit().putString("device_name", deviceName).apply()

                    updateUI(deviceName)
                } catch (e: SecurityException) {
                    Toast.makeText(this, "Ошибка доступа к Bluetooth устройству", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
*/
package com.example.tamyrapp2.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tamyrapp2.R
import com.example.tamyrapp2.retrofit.miband.FakeMiBandService
import com.example.tamyrapp2.retrofit.miband.MiBandViewModel

class MiBandActivity : AppCompatActivity() {
    private val viewModel: MiBandViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private val fakeMiBandService = FakeMiBandService() // Фейковый Mi Band сервис

    companion object {
        private const val REQUEST_SELECT_DEVICE = 2
        private const val REQUEST_BLUETOOTH_PERMISSION = 3
        private const val TAG = "MiBandActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_miband)

        sharedPreferences = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        val userId = sharedPreferences.getLong("user_id", -1L) // ✅ Получаем `userId`
        if (userId == -1L) {
            Toast.makeText(this, "❌ Ошибка: User ID не найден!", Toast.LENGTH_LONG).show()
            return
        }

        val btnConnect = findViewById<Button>(R.id.btn_connect)
        val btnFakeConnect = findViewById<Button>(R.id.btn_fake_connect) // Кнопка фейкового подключения
        val btnViewData = findViewById<Button>(R.id.btn_view_data)
        val tvDeviceName = findViewById<TextView>(R.id.tv_device_name)

        observeViewModel()

        val savedDevice = sharedPreferences.getString("device_address", null)
        updateUI(savedDevice)

        btnConnect.setOnClickListener {
            if (savedDevice == null) {
                requestBluetoothPermission()
            } else {
                disconnectDevice()
            }
        }

        btnFakeConnect.setOnClickListener {
            connectFakeDevice(userId) // ✅ Передаём `userId`
        }

        btnViewData.setOnClickListener {
            startActivity(Intent(this, MiBandDataActivity::class.java))
        }
    }

    /**
     * 📌 Подключение фейкового Mi Band
     */
    private fun connectFakeDevice(userId: Long) { // ✅ Добавлен параметр userId
        fakeMiBandService.connectFakeDevice { connected ->
            if (connected) {
                sharedPreferences.edit().putString("device_address", "FakeMiBand").apply()
                sharedPreferences.edit().putString("device_name", "Фейковый Mi Band").apply()

                updateUI("Фейковый Mi Band")
                Toast.makeText(this, "✅ Фейковый Mi Band подключен", Toast.LENGTH_SHORT).show()

                // ✅ Передаём `userId` в `sendFakeDataToBackend(userId)`
                fakeMiBandService.sendFakeDataToBackend(userId)
            } else {
                Toast.makeText(this, "❌ Ошибка подключения фейкового Mi Band", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 🔄 Подписка на данные ViewModel
     */
    private fun observeViewModel() {
        viewModel.heartRate.observe(this) { heartRate ->
            Log.d(TAG, "💓 Пульс обновлён: $heartRate уд/мин")
        }

        viewModel.steps.observe(this) { steps ->
            Log.d(TAG, "🚶 Шаги обновлены: $steps")
        }

        viewModel.sleepHours.observe(this) { sleep ->
            Log.d(TAG, "😴 Сон обновлён: $sleep часов")
        }

        viewModel.success.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "✅ Данные успешно отправлены", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, "❌ Ошибка: $errorMessage", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Ошибка: $errorMessage")
            }
        }
    }

    /**
     * 🔄 Обновление UI после выбора устройства
     */
    private fun updateUI(deviceName: String?) {
        val tvDeviceName = findViewById<TextView>(R.id.tv_device_name)
        val btnConnect = findViewById<Button>(R.id.btn_connect)
        val btnViewData = findViewById<Button>(R.id.btn_view_data)

        if (deviceName != null) {
            tvDeviceName.text = "Подключено: $deviceName"
            btnConnect.text = "Отключить"
            btnViewData.isEnabled = true
        } else {
            tvDeviceName.text = "Устройство не подключено"
            btnConnect.text = "+ Подключить устройство"
            btnViewData.isEnabled = false
        }
    }

    /**
     * 🔌 Отключение устройства
     */
    private fun disconnectDevice() {
        sharedPreferences.edit().remove("device_address").apply()
        sharedPreferences.edit().remove("device_name").apply()

        updateUI(null)
    }

    /**
     * 📌 Запрос разрешений для работы с Bluetooth
     */
    private fun requestBluetoothPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            selectDevice()
        }
    }

    /**
     * 📡 Выбор устройства Bluetooth
     */
    private fun selectDevice() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth не поддерживается", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivity(enableBtIntent)
        } else {
            val selectDeviceIntent = Intent(this, SelectDeviceActivity::class.java)
            startActivityForResult(selectDeviceIntent, REQUEST_SELECT_DEVICE)
        }
    }

    /**
     * 🎯 Обработка выбора устройства
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_DEVICE && resultCode == Activity.RESULT_OK) {
            val deviceAddress = data?.getStringExtra("device_address")
            val deviceName = data?.getStringExtra("device_name")

            if (deviceAddress != null && deviceName != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Нет разрешений на подключение к устройству", Toast.LENGTH_SHORT).show()
                    return
                }

                try {
                    val bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
                    viewModel.connectToMiBand(bluetoothDevice)

                    sharedPreferences.edit().putString("device_address", deviceAddress).apply()
                    sharedPreferences.edit().putString("device_name", deviceName).apply()

                    updateUI(deviceName)
                } catch (e: SecurityException) {
                    Toast.makeText(this, "Ошибка доступа к Bluetooth устройству", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

