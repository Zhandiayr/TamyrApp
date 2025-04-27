package com.example.tamyrapp2.presentation.miband

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import com.example.tamyrapp2.miband.MiBandConnectionManager

class MiBandConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var connectionManager: MiBandConnectionManager? = null

    fun connectToMiBand(deviceAddress: String) {
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        if (device != null) {
            connectionManager = MiBandConnectionManager(getApplication())
            connectionManager?.connect(device)
        }
    }

    fun disconnect() {
        connectionManager?.disconnect()
    }
}
