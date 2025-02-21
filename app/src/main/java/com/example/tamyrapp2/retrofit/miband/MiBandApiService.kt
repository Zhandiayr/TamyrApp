package com.example.tamyrapp2.retrofit.miband

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MiBandApiService {
    @POST("miband/data")
    fun sendMiBandData(
        @Header("Authorization") token: String,
        @Body request: MiBandDataRequest
    ): Call<Void>
}
