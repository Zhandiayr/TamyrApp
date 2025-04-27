package com.example.tamyrapp2.data.network.personalinfo

data class MainPersonalInfoRequest(
    val userId: Long,
    val age: Int,
    val sex: String,  // "Male" или "Female"
    val weight: Int,  // Вес в кг
    val height: Int   // Рост в см
)
