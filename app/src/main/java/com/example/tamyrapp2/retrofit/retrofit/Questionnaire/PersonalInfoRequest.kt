package com.example.tamyrapp2.retrofit.retrofit.Questionnaire

data class PersonalInfoRequest(
    val userId: Long,
    val age: Int,
    val sex: String,
    val weight: Int,
    val height: Int
)

