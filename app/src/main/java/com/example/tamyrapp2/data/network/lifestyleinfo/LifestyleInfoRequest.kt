
package com.example.tamyrapp2.data.network.lifestyle

data class LifestyleInfoRequest(
    val userId: Long,
    val smokes: Boolean,
    val drinksAlcohol: Boolean,
    val exercises: Boolean,
    val fruitIntake: String // "Low", "Moderate", "High"
)
