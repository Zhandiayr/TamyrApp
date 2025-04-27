package com.example.tamyrapp2.data.network.miband

data class MiBandDataResponse(
    val userId: Long,
    val heartRate: Int,
    val steps: Int,
    val caloriesBurned: Int,
    val distance: Int,
    val batteryLevel: Int,
    val timestamp: String
)
