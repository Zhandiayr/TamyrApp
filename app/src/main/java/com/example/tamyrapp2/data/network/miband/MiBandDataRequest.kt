package com.example.tamyrapp2.data.network.miband

data class MiBandDataRequest(
    val userId: Long,
    val heartRateListJson: String,  // новое поле: JSON-массив пульсов, например "[72,75,70]"
    val steps: Int,
    val caloriesBurned: Int,
    val distance: Int,
    val batteryLevel: Int,
    val timestamp: String
)
