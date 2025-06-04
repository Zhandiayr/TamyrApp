package com.example.tamyrapp2.data.network.miband

data class MiBandDataRequest(
    val userId: Long,               // ID пользователя, который отправляет данные
    val heartRateListJson: String,  // JSON-массив пульсов, например "[72,75,70,68,...]"
    val heartRateList: List<Int>,   // Массив пульса
    val steps: Int,                 // Количество шагов
    val caloriesBurned: Int,       // Количество сожженных калорий
    val distance: Int,              // Пройденное расстояние (в метрах)
    val batteryLevel: Int,         // Уровень заряда батареи (в процентах)
    val timestamp: String          // Время записи данных
)
