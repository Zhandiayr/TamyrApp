package com.example.tamyrapp2.data.network.personalinfo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PersonalInfoApiService {
    @POST("api/personal-info")
    fun saveOrUpdatePersonalInfo(
        @Header("Authorization") token: String,
        @Body request: MainPersonalInfoRequest
    ): Call<Void>
    @GET("api/personal-info/{id}")
    fun getPersonalInfoById(
        @Path("id") userId: Long,
        @Header("Authorization") token: String
    ): Call<MainPersonalInfoRequest>
    @GET("api/personal-info/age/{userId}")
    fun getAgeByUserId(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): Call<Int>
    @GET("api/personal-info/exists/{userId}")
    fun existsPersonalInfo(
        @Path("userId") userId: Long,
        @Header("Authorization") token: String
    ): Call<Boolean>
}
