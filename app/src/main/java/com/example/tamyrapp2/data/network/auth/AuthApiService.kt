package com.example.tamyrapp2.data.network.auth

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("registration")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @POST("login")
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @POST("refresh_token")
    fun refreshToken(@Header("Authorization") token: String): Call<AuthResponse>
}
