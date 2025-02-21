package com.example.tamyrapp2.retrofit.miband

data class MiBandDataRequest(
    val heartRate: Int,
    val steps: Int,
    val sleepHours: Int,
    val caloriesBurned: Int,
    val distance: Int,
    val batteryLevel: Int
)