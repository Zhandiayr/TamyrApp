package com.example.tamyrapp2.data.network.auth

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: Long
)
