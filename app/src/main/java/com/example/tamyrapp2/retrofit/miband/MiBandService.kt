/*package com.example.tamyrapp2.retrofit.miband

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.tamyrapp2.retrofit.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MiBandService(private val context: Context) {
    private var bluetoothGatt: BluetoothGatt? = null
    private val api = RetrofitInstance.miBandApi

    companion object {
        private const val TAG = "MiBandService"
        val HEART_RATE_UUID: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb") // UUID ЧСС
    }

    fun connect(device: BluetoothDevice, callback: (Boolean) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ Нет разрешений на BLUETOOTH_CONNECT!")
            return
        }

        try {
            bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(TAG, "✅ Успешное подключение к Mi Band")
                        gatt.discoverServices()
                        callback(true)
                    } else {
                        Log.d(TAG, "❌ Отключено от Mi Band")
                        callback(false)
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    val heartRateService = gatt.getService(HEART_RATE_UUID)
                    val heartRateCharacteristic = heartRateService?.getCharacteristic(HEART_RATE_UUID)

                    heartRateCharacteristic?.let {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "❌ Нет разрешений на чтение характеристик!")
                            return
                        }
                        gatt.readCharacteristic(it)
                    }
                }

                override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
                    if (characteristic.uuid == HEART_RATE_UUID) {
                        val heartRate = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0) ?: 0
                        Log.d(TAG, "💓 Пульс: $heartRate уд/мин")
                        sendDataToBackend(heartRate, 5000, 7, 300, 5, 80) // Временные тестовые данные
                    }
                }
            })
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException при подключении: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException при отключении: ${e.message}")
        }
    }

    private fun sendDataToBackend(heartRate: Int, steps: Int, sleepHours: Int, caloriesBurned: Int, distance: Int, batteryLevel: Int) {
        val request = MiBandDataRequest(
            heartRate = heartRate,
            steps = steps,
            sleepHours = sleepHours,
            caloriesBurned = caloriesBurned,
            distance = distance,
            batteryLevel = batteryLevel
        )

        api.sendMiBandData("Bearer ACCESS_TOKEN", request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "✅ Данные Mi Band отправлены на сервер")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "❌ Ошибка сети: ${t.message}")
            }
        })
    }
}
*/
package com.example.tamyrapp2.retrofit.miband

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.tamyrapp2.retrofit.auth.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MiBandService(private val context: Context) {
    private var bluetoothGatt: BluetoothGatt? = null
    private val api = RetrofitInstance.miBandApi

    companion object {
        private const val TAG = "MiBandService"
        val HEART_RATE_UUID: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb") // UUID для ЧСС
    }

    /**
     * 📡 Подключение к Mi Band
     */
    fun connect(device: BluetoothDevice, userId: Long, callback: (Boolean) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "❌ Нет разрешений на BLUETOOTH_CONNECT!")
            return
        }

        try {
            bluetoothGatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
                override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Log.d(TAG, "✅ Успешное подключение к Mi Band")
                        gatt.discoverServices()
                        callback(true)
                    } else {
                        Log.d(TAG, "❌ Отключено от Mi Band")
                        callback(false)
                    }
                }

                override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                    val heartRateService = gatt.getService(HEART_RATE_UUID)
                    val heartRateCharacteristic = heartRateService?.getCharacteristic(HEART_RATE_UUID)

                    heartRateCharacteristic?.let {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "❌ Нет разрешений на чтение характеристик!")
                            return
                        }
                        gatt.readCharacteristic(it)
                    }
                }

                override fun onCharacteristicRead(
                    gatt: BluetoothGatt,
                    characteristic: BluetoothGattCharacteristic,
                    status: Int
                ) {
                    if (characteristic.uuid == HEART_RATE_UUID) {
                        val heartRate =
                            characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0) ?: 0
                        Log.d(TAG, "💓 Пульс: $heartRate уд/мин")
                        sendDataToBackend(userId, heartRate, 5000, 7, 300, 5, 80)
                    }
                }
            })
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException при подключении: ${e.message}")
        }
    }

    /**
     * 🔌 Отключение от Mi Band
     */
    fun disconnect() {
        try {
            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ SecurityException при отключении: ${e.message}")
        }
    }

    /**
     * 📤 Отправка данных Mi Band на сервер
     */
    private fun sendDataToBackend(
        userId: Long,
        heartRate: Int,
        steps: Int,
        sleepHours: Int,
        caloriesBurned: Int,
        distance: Int,
        batteryLevel: Int
    ) {
        val timestamp = getCurrentTimestamp() // Получаем текущее время в формате строки

        val request = MiBandDataRequest(
            userId = userId,
            heartRate = heartRate,
            steps = steps,
            sleepHours = sleepHours,
            caloriesBurned = caloriesBurned,
            distance = distance,
            batteryLevel = batteryLevel,
            timestamp = timestamp // Передаем строковый формат времени
        )

        api.sendMiBandData("Bearer ACCESS_TOKEN", request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d(
                        TAG, "✅ Данные Mi Band отправлены: " +
                                "Пульс: $heartRate, Шаги: $steps, " +
                                "Сон: $sleepHours ч, Калории: $caloriesBurned, " +
                                "Дистанция: $distance км, Батарея: $batteryLevel%, " +
                                "Время: $timestamp"
                    )
                } else {
                    Log.e(TAG, "⚠️ Ошибка отправки данных. Код ответа: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "❌ Ошибка сети: ${t.message}")
            }
        })
    }

    /**
     * 🕒 Получение текущего времени в формате строки (ISO 8601)
     */
    private fun getCurrentTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
