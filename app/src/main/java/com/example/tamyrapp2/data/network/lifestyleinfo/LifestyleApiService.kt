package com.example.tamyrapp2.data.network.lifestyle

import retrofit2.Call
import retrofit2.http.*

interface LifestyleApiService {

    @POST("api/lifestyle-info")
    fun saveLifestyleInfo(
        @Header("Authorization") token: String,
        @Body lifestyleInfo: LifestyleInfoRequest
    ): Call<LifestyleInfoRequest>

    @GET("api/lifestyle-info/{id}")
    fun getLifestyleInfoById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<LifestyleInfoRequest>
}
