package com.example.tamyrapp2.data.network.personalinfo

data class MainPersonalInfoRequest(
    val userId: Long,
    val name: String?,
    val surname: String?,
    val age: Int?,
    val sex: String?,
    val weight: Int?,
    val height: Int?
)
