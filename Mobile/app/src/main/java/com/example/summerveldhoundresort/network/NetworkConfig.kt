package com.example.summerveldhoundresort.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Network configuration for API client
 */
object NetworkConfig {
    
    // Base URL for your RESTful API
    // Update this to match your actual API server URL
    private const val BASE_URL = "http://10.0.2.2:5000/api/" // For Android emulator
    // For physical device, use your computer's IP address:
    // private const val BASE_URL = "http://192.168.1.100:5000/api/"
    
    // For production, use your deployed API URL:
    // private const val BASE_URL = "https://your-api-domain.com/api/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val imageApiService: com.example.summerveldhoundresort.network.api.ImageApiService = 
        retrofit.create(com.example.summerveldhoundresort.network.api.ImageApiService::class.java)
}
