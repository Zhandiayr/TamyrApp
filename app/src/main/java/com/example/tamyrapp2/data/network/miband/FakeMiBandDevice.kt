package com.example.tamyrapp2.data.network.miband

data class FakeMiBandDevice(
    val heartRate: Int = 75,
    val steps: Int = 6000,
    val caloriesBurned: Int = 300,
    val batteryLevel: Int = 85
)