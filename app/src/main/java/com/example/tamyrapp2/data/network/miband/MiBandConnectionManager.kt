/*package com.example.tamyrapp2.miband

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.*

class MiBandConnectionManager(private val context: Context) {

    private var bluetoothGatt: BluetoothGatt? = null

    companion object {
        private const val TAG = "MiBandConnectionManager"

        private val SERVICE_HEART_RATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val CHARACTERISTIC_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    }

    fun connect(device: BluetoothDevice) {
        if (!hasBluetoothConnectPermission()) {
            Log.e(TAG, "Нет разрешения BLUETOOTH_CONNECT для подключения")
            return
        }
        try {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException при connectGatt: ${e.message}")
        }
    }

    fun disconnect() {
        if (!hasBluetoothConnectPermission()) {
            Log.e(TAG, "Нет разрешения BLUETOOTH_CONNECT для отключения")
            return
        }
        try {
            bluetoothGatt?.disconnect()
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException при disconnect: ${e.message}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Подключено к Mi Band, поиск сервисов...")
                if (hasBluetoothConnectPermission()) {
                    try {
                        gatt.discoverServices()
                    } catch (e: SecurityException) {
                        Log.e(TAG, "SecurityException при discoverServices: ${e.message}")
                    }
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Отключено от Mi Band")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Сервисы найдены, читаем пульс...")
                readHeartRate(gatt)
            }
        }

        private fun readHeartRate(gatt: BluetoothGatt) {
            val service = gatt.getService(SERVICE_HEART_RATE)
            val characteristic = service?.getCharacteristic(CHARACTERISTIC_HEART_RATE_MEASUREMENT)
            if (characteristic != null && hasBluetoothConnectPermission()) {
                try {
                    gatt.readCharacteristic(characteristic)
                    Log.d(TAG, "Чтение сердцебиения...")
                } catch (e: SecurityException) {
                    Log.e(TAG, "SecurityException при readCharacteristic: ${e.message}")
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.uuid == CHARACTERISTIC_HEART_RATE_MEASUREMENT) {
                    val heartRate = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
                    Log.d(TAG, "Пульс: $heartRate BPM")
                }
            }
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
 */
package com.example.tamyrapp2.miband

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.*

class MiBandConnectionManager(private val context: Context) {

    private var bluetoothGatt: BluetoothGatt? = null

    var heartRate: Int? = null
    var steps: Int? = null
    var calories: Int? = null
    var batteryLevel: Int? = null

    companion object {
        private const val TAG = "MiBandConnectionManager"

        // UUID Mi Band
        private val SERVICE_HEART_RATE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
        private val CHARACTERISTIC_HEART_RATE_MEASUREMENT = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

        private val SERVICE_FITNESS = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb") // Приватный сервис Mi Band
        private val CHARACTERISTIC_STEPS = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb")

        private val SERVICE_BATTERY = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
        private val CHARACTERISTIC_BATTERY_LEVEL = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    }

    fun connect(device: BluetoothDevice) {
        if (!hasBluetoothConnectPermission()) {
            Log.e(TAG, "Нет разрешения BLUETOOTH_CONNECT для подключения")
            return
        }
        try {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException при connectGatt: ${e.message}")
        }
    }

    fun disconnect() {
        if (!hasBluetoothConnectPermission()) {
            Log.e(TAG, "Нет разрешения BLUETOOTH_CONNECT для отключения")
            return
        }
        try {
            bluetoothGatt?.disconnect()
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException при disconnect: ${e.message}")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Подключено к Mi Band, поиск сервисов...")
                if (hasBluetoothConnectPermission()) {
                    try {
                        gatt.discoverServices()
                    } catch (e: SecurityException) {
                        Log.e(TAG, "SecurityException при discoverServices: ${e.message}")
                    }
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "Отключено от Mi Band")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Сервисы найдены, читаем данные...")
                readAllData(gatt)
            }
        }

        private fun readAllData(gatt: BluetoothGatt) {
            readHeartRate(gatt)
            readStepsAndCalories(gatt)
            readBatteryLevel(gatt)
        }

        private fun readHeartRate(gatt: BluetoothGatt) {
            val service = gatt.getService(SERVICE_HEART_RATE)
            val characteristic = service?.getCharacteristic(CHARACTERISTIC_HEART_RATE_MEASUREMENT)
            if (characteristic != null && hasBluetoothConnectPermission()) {
                try {
                    gatt.readCharacteristic(characteristic)
                    Log.d(TAG, "Чтение пульса...")
                } catch (e: SecurityException) {
                    Log.e(TAG, "SecurityException при чтении пульса: ${e.message}")
                }
            }
        }

        private fun readStepsAndCalories(gatt: BluetoothGatt) {
            val service = gatt.getService(SERVICE_FITNESS)
            val characteristic = service?.getCharacteristic(CHARACTERISTIC_STEPS)
            if (characteristic != null && hasBluetoothConnectPermission()) {
                try {
                    gatt.readCharacteristic(characteristic)
                    Log.d(TAG, "Чтение шагов и калорий...")
                } catch (e: SecurityException) {
                    Log.e(TAG, "SecurityException при чтении шагов/калорий: ${e.message}")
                }
            }
        }

        private fun readBatteryLevel(gatt: BluetoothGatt) {
            val service = gatt.getService(SERVICE_BATTERY)
            val characteristic = service?.getCharacteristic(CHARACTERISTIC_BATTERY_LEVEL)
            if (characteristic != null && hasBluetoothConnectPermission()) {
                try {
                    gatt.readCharacteristic(characteristic)
                    Log.d(TAG, "Чтение заряда батареи...")
                } catch (e: SecurityException) {
                    Log.e(TAG, "SecurityException при чтении батареи: ${e.message}")
                }
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (characteristic.uuid) {
                    CHARACTERISTIC_HEART_RATE_MEASUREMENT -> {
                        heartRate = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1)
                        Log.d(TAG, "Пульс: $heartRate BPM")
                    }
                    CHARACTERISTIC_STEPS -> {
                        val data = characteristic.value
                        if (data != null && data.size >= 4) {
                            steps = (data[1].toInt() and 0xFF) shl 8 or (data[0].toInt() and 0xFF)
                            calories = (data[3].toInt() and 0xFF) shl 8 or (data[2].toInt() and 0xFF)
                            Log.d(TAG, "Шаги: $steps, Калории: $calories")
                        }
                    }
                    CHARACTERISTIC_BATTERY_LEVEL -> {
                        batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0)
                        Log.d(TAG, "Батарея: $batteryLevel%")
                    }
                }
            }
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
