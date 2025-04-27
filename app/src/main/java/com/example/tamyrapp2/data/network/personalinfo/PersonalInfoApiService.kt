package com.example.tamyrapp2.data.network.personalinfo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PersonalInfoApiService {
    @POST("api/personal-info")
    fun saveOrUpdatePersonalInfo(
        @Header("Authorization") token: String,
        @Body request: MainPersonalInfoRequest
    ): Call<Void>
}
