package com.example.tamyrapp2.data.network.miband

import com.example.tamyrapp2.data.network.miband.MiBandDataRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MiBandApiService {

    @POST("api/miband/data")
    fun sendMiBandData(
        @Header("Authorization") token: String,
        @Body data: MiBandDataRequest
    ): Call<Void>

    @GET("api/miband/data/{userId}")
    fun getMiBandData(
        @Header("Authorization") token: String,
        @Path("userId") userId: Long
    ): Call<List<MiBandDataResponse>>
}
