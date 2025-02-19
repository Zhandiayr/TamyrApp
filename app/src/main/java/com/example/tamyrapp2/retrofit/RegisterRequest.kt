package com.example.tamyrapp2.retrofit

data class RegisterRequest (
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)
