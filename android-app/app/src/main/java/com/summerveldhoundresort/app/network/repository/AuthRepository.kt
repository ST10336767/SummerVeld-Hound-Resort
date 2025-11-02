package com.summerveldhoundresort.app.network.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.summerveldhoundresort.app.db.AppResult
import com.summerveldhoundresort.app.network.NetworkConfig
import com.summerveldhoundresort.app.network.models.AuthModels
import com.summerveldhoundresort.app.network.api.AuthApiService
import retrofit2.HttpException
import java.io.IOException

/**
 * Repository for handling authentication through API calls
 */
class AuthRepository {
    
    private val authApiService: AuthApiService = NetworkConfig.authApiService
    
    companion object {
        private const val ERROR_NETWORK_CONNECTION_FAILED = "Network connection failed"
    }
    
    /**
     * Register a new user via API
     */
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String
    ): AppResult<AuthModels.AuthData> {
        return try {
            val request = AuthModels.RegisterRequest(
                firstName = firstName,
                lastName = lastName,
                email = email,
                password = password,
                phone = phone
            )
            
            val response = authApiService.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                if (authData != null) {
                    Log.d(TAG, "Registration successful via API")
                    AppResult.Success(authData)
                } else {
                    AppResult.Error(Exception("Registration response data is null"))
                }
            } else {
                val errorMessage = response.body()?.message ?: "Registration failed"
                Log.e(TAG, "Registration failed: $errorMessage")
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during registration", e)
            AppResult.Error(Exception("Network error: ${e.message}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network error during registration", e)
            AppResult.Error(Exception(ERROR_NETWORK_CONNECTION_FAILED))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during registration", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Login user via API
     */
    suspend fun login(email: String, password: String): AppResult<AuthModels.AuthData> {
        return try {
            val request = AuthModels.LoginRequest(
                email = email,
                password = password
            )
            
            val response = authApiService.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                if (authData != null) {
                    Log.d(TAG, "Login successful via API")
                    AppResult.Success(authData)
                } else {
                    AppResult.Error(Exception("Login response data is null"))
                }
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Log.e(TAG, "Login failed: $errorMessage")
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during login", e)
            AppResult.Error(Exception("Network error: ${e.message}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network error during login", e)
            AppResult.Error(Exception(ERROR_NETWORK_CONNECTION_FAILED))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during login", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Get current user profile via API
     */
    suspend fun getCurrentUser(token: String): AppResult<AuthModels.UserProfile> {
        return try {
            val response = authApiService.getCurrentUser("Bearer $token")
            
            if (response.isSuccessful && response.body()?.success == true) {
                val userProfile = response.body()?.data?.user
                if (userProfile != null) {
                    Log.d(TAG, "Get current user successful via API")
                    AppResult.Success(userProfile)
                } else {
                    AppResult.Error(Exception("User profile data is null"))
                }
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get user profile - HTTP ${response.code()}"
                Log.e(TAG, "Get current user failed: $errorMessage")
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during get current user", e)
            AppResult.Error(Exception("Network error: ${e.message}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network error during get current user", e)
            AppResult.Error(Exception(ERROR_NETWORK_CONNECTION_FAILED))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during get current user", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Logout user via API
     */
    suspend fun logout(token: String, refreshToken: String?): AppResult<Unit> {
        return try {
            val request = AuthModels.LogoutRequest(refreshToken = refreshToken)
            val response = authApiService.logout("Bearer $token", request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                Log.d(TAG, "Logout successful via API")
                AppResult.Success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Logout failed"
                Log.e(TAG, "Logout failed: $errorMessage")
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during logout", e)
            AppResult.Error(Exception("Network error: ${e.message}"))
        } catch (e: IOException) {
            Log.e(TAG, "Network error during logout", e)
            AppResult.Error(Exception(ERROR_NETWORK_CONNECTION_FAILED))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during logout", e)
            AppResult.Error(e)
        }
    }
}
