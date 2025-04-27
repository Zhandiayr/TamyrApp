/*package com.example.tamyrapp2.retrofit.retrofit.auth

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val api: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
*/
package com.example.tamyrapp2.data.network

import com.example.tamyrapp2.data.network.auth.AuthApiService
import com.example.tamyrapp2.data.network.lifestyle.LifestyleApiService
import com.example.tamyrapp2.data.network.miband.MiBandApiService
import com.example.tamyrapp2.data.network.personalinfo.PersonalInfoApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val personalInfoApi: PersonalInfoApiService by lazy {
        retrofit.create(PersonalInfoApiService::class.java)
    }

    val miBandApi: MiBandApiService by lazy {
        retrofit.create(MiBandApiService::class.java)
    }

    val lifestyleApi: LifestyleApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LifestyleApiService::class.java)
    }


}
