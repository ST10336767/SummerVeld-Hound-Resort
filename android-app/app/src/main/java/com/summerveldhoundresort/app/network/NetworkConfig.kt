package com.summerveldhoundresort.app.network

import com.summerveldhoundresort.app.BuildConfig
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
    // Updated to use Render deployment
    private const val BASE_URL = "https://summerveld-api.onrender.com/api/"
    
    // Alternative URLs for development (uncomment if needed):
    // private const val BASE_URL = "http://10.0.2.2:5000/api/" // For Android emulator
    // private const val BASE_URL = "http://192.168.1.100:5000/api/" // For physical device
    
    private val okHttpClient = OkHttpClient.Builder().apply {
        // Only add logging interceptor in debug builds for security
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addInterceptor(loggingInterceptor)
        }
        
        connectTimeout(60, TimeUnit.SECONDS)
        readTimeout(300, TimeUnit.SECONDS)  // 5 minutes for read operations (image uploads)
        writeTimeout(300, TimeUnit.SECONDS) // 5 minutes for write operations (image uploads)
    }.build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val imageApiService: com.summerveldhoundresort.app.network.api.ImageApiService = 
        retrofit.create(com.summerveldhoundresort.app.network.api.ImageApiService::class.java)

    val authApiService: com.summerveldhoundresort.app.network.api.AuthApiService = 
        retrofit.create(com.summerveldhoundresort.app.network.api.AuthApiService::class.java)
}
