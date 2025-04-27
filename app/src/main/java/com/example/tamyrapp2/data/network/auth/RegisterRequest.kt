package com.example.tamyrapp2.data.network.auth

data class RegisterRequest (
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)
