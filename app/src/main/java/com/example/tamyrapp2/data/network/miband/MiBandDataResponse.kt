package com.example.tamyrapp2.data.network.miband

data class MiBandDataResponse(
    val userId: Long,              // Идентификатор пользователя
    val heartRateListJson: String, // JSON-массив пульсов, например "[72,75,70,68,...]"
    val heartRateList: List<Int>,  // Массив пульсов
    val steps: Int,                // Количество шагов
    val caloriesBurned: Int,      // Количество сожженных калорий
    val distance: Int,             // Пройденное расстояние (в метрах)
    val batteryLevel: Int,        // Уровень заряда батареи (в процентах)
    val timestamp: String         // Время записи данных
)
