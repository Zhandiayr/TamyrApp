package com.example.tamyrapp2.retrofit
import retrofit2.Retrofit
import AuthApiService
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/" // IP для эмулятора

    val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
