package com.example.tamyrapp2.UI

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tamyrapp2.R

class DevicesAdapter(
    private val devices: List<BluetoothDevice>,
    private val onDeviceClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DevicesAdapter.DeviceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device)
    }

    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val deviceName: TextView = view.findViewById(R.id.deviceName)
        private val deviceAddress: TextView = view.findViewById(R.id.deviceAddress)

        fun bind(device: BluetoothDevice) {
            deviceName.text = device.name ?: "Неизвестное устройство"
            deviceAddress.text = device.address
            itemView.setOnClickListener { onDeviceClick(device) }
        }
    }
}
