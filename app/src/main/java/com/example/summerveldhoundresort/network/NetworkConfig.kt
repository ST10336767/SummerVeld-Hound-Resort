package com.example.summerveldhoundresort.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkConfig {
    private const val BASE_URL = "http://localhost:3000/api/" // Update with your actual API URL
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val imageApiService: com.example.summerveldhoundresort.network.api.ImageApiService by lazy {
        retrofit.create(com.example.summerveldhoundresort.network.api.ImageApiService::class.java)
    }
}
