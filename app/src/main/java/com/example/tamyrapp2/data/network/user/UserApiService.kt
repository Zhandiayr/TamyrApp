package com.example.tamyrapp2.data.network.user

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApiService {
    @GET("api/user/me")
    fun getCurrentUser(@Header("Authorization") token: String): Call<UserDto>
}
