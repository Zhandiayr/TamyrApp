package com.example.tamyrapp2.ui // Переименовано с UI → ui (с маленькой буквы)

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamyrapp2.R
import com.example.tamyrapp2.UI.DevicesAdapter
import com.example.tamyrapp2.presentation.miband.MiBandConnectViewModel

@SuppressLint("MissingPermission") // добавлено для методов внутри проверок
class ScanDevicesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DevicesAdapter

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val viewModel: MiBandConnectViewModel by viewModels()

    private val devices = mutableListOf<BluetoothDevice>()

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                device?.let {
                    if (!devices.contains(it)) {
                        devices.add(it)
                        adapter.notifyItemInserted(devices.size - 1)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_devices)

        recyclerView = findViewById(R.id.recyclerViewDevices)
        adapter = DevicesAdapter(devices) { device -> connectToDevice(device) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        requestPermissionsAndStartDiscovery()
    }

    private fun requestPermissionsAndStartDiscovery() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissionsResult ->
            if (permissionsResult.all { it.value }) {
                startDiscovery()
            } else {
                Toast.makeText(this, "Bluetooth permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
        launcher.launch(permissions.toTypedArray())
    }

    private fun startDiscovery() {
        if (!hasBluetoothScanPermission()) {
            Toast.makeText(this, "Нет разрешения на сканирование Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(bluetoothReceiver, filter)

        bluetoothAdapter?.startDiscovery()
    }

    private fun connectToDevice(device: BluetoothDevice) {
        if (hasBluetoothScanPermission()) {
            bluetoothAdapter?.cancelDiscovery()
        }
        viewModel.connectToMiBand(device.address)
        Toast.makeText(this, "Подключение к ${device.name ?: "устройству"}...", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(bluetoothReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        viewModel.disconnect()
    }

    private fun hasBluetoothScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
