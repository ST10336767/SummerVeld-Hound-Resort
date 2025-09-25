package com.example.summerveldhoundresort.network.api

import com.example.summerveldhoundresort.network.models.AuthModels
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit API service interface for authentication operations
 */
interface AuthApiService {
    
    /**
     * Register a new user
     */
    @POST("auth/register")
    suspend fun register(
        @Body request: AuthModels.RegisterRequest
    ): Response<AuthModels.AuthResponse>
    
    /**
     * Login user
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: AuthModels.LoginRequest
    ): Response<AuthModels.AuthResponse>
    
    /**
     * Get current user profile
     */
    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<AuthModels.UserResponse>
    
    /**
     * Logout user
     */
    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body request: AuthModels.LogoutRequest
    ): Response<AuthModels.LogoutResponse>
}
