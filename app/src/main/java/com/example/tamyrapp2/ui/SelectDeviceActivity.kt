package com.example.tamyrapp2.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.tamyrapp2.R

class SelectDeviceActivity : AppCompatActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_device)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        listView = findViewById(R.id.device_list)
        sharedPreferences = getSharedPreferences("miband_prefs", Context.MODE_PRIVATE)

        // Проверяем, есть ли уже подключённое устройство
        val savedDevice = sharedPreferences.getString("device_address", null)
        if (savedDevice != null) {
            Toast.makeText(this, "Уже подключено устройство: $savedDevice", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Проверяем разрешения перед загрузкой списка устройств
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BLUETOOTH_PERMISSION
            )
        } else {
            loadPairedDevices()
        }
    }

    private fun loadPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_BLUETOOTH_PERMISSION
            )
            return
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val deviceList = mutableListOf<String>()
        val deviceMap = mutableMapOf<String, BluetoothDevice>()

        pairedDevices?.forEach { device ->
            if (device.name.contains("Mi Band", ignoreCase = true)) { // Фильтруем только браслеты Mi Band
                deviceList.add("${device.name} (${device.address})")
                deviceMap[device.name] = device
            }
        }

        if (deviceList.isEmpty()) {
            Toast.makeText(this, "Устройства Mi Band не найдены", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val deviceName = deviceList[position].substringBefore(" (")
            val deviceAddress = deviceMap[deviceName]?.address

            if (deviceAddress != null) {
                sharedPreferences.edit().putString("device_address", deviceAddress).apply()
                sharedPreferences.edit().putString("device_name", deviceName).apply()

                val resultIntent = Intent()
                resultIntent.putExtra("device_name", deviceName)
                resultIntent.putExtra("device_address", deviceAddress)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPairedDevices()
            } else {
                Toast.makeText(this, "Разрешение на доступ к Bluetooth-устройствам отклонено", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
