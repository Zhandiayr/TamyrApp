package com.example.tamyrapp2.UI
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.tamyrapp2.R
import com.example.tamyrapp2.presentation.miband.MiBandConnectViewModel

class ConnectMiBandActivity : AppCompatActivity() {

    private val viewModel: MiBandConnectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_miband)

        val btnConnect = findViewById<Button>(R.id.btn_connect)
        val etDeviceAddress = findViewById<EditText>(R.id.et_device_address)

        btnConnect.setOnClickListener {
            val deviceAddress = etDeviceAddress.text.toString()

            if (deviceAddress.isNotEmpty()) {
                viewModel.connectToMiBand(deviceAddress)
                Toast.makeText(this, "Connecting to the device $deviceAddress...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Enter the MAC address of the device!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        viewModel.disconnect()
        super.onDestroy()
    }
}
