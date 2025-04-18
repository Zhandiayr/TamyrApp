/*import com.example.tamyrapp2.retrofit.auth.AuthResponse
import com.example.tamyrapp2.retrofit.auth.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("login") // Эндпоинт для входа
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>
}
 */
package com.example.tamyrapp2.retrofit.retrofit.auth
import com.example.tamyrapp2.retrofit.retrofit.Questionnaire.PersonalInfoRequest


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("registration")
    fun registerUser(@Body request: RegisterRequest): Call<Void>

    @POST("login") // Авторизация
    fun loginUser(@Body request: LoginRequest): Call<AuthResponse>

    @POST("refresh_token") // Обновление токена
    fun refreshToken(@Header("Authorization") token: String): Call<AuthResponse>

    @POST("api/personal-info") // Исправлен путь
    fun savePersonalInfo(@Header("Authorization") token: String, @Body request: PersonalInfoRequest): Call<Void>
}
