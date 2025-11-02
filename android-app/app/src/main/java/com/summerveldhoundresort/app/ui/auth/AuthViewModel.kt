package com.summerveldhoundresort.app.ui.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summerveldhoundresort.app.db.AppResult
import com.summerveldhoundresort.app.db.entities.User
import com.summerveldhoundresort.app.db.implementations.firebaseUsersImpl
import com.summerveldhoundresort.app.network.repository.AuthRepository
import com.summerveldhoundresort.app.network.models.AuthModels
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseUserRepo: firebaseUsersImpl,
    private val apiAuthRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    // LiveData for storing authentication tokens
    private val _authTokens = MutableLiveData<AuthModels.AuthData?>()
    val authTokens: LiveData<AuthModels.AuthData?> = _authTokens

    /**
     * Register user via API (recommended)
     */
    suspend fun registerViaApi(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phone: String
    ): AppResult<AuthModels.AuthData> {
        val result = apiAuthRepo.register(firstName, lastName, email, password, phone)
        
        if (result is AppResult.Success) {
            // Store tokens for future use
            _authTokens.postValue(result.data)
        }
        
        return result
    }

    /**
     * Login user via API (recommended)
     */
    suspend fun loginViaApi(email: String, password: String): AppResult<AuthModels.AuthData> {
        val result = apiAuthRepo.login(email, password)
        
        if (result is AppResult.Success) {
            // Store tokens for future use
            _authTokens.postValue(result.data)
        }
        
        return result
    }

    /**
     * Legacy Firebase registration (kept for compatibility)
     */
    suspend fun register(username: String, email: String, password: String, phoneNumber: String, creationDate:String): AppResult<Unit>{
        return  firebaseUserRepo.register(username, email, password, phoneNumber, creationDate)
    }

    /**
     * Legacy Firebase login (kept for compatibility)
     */
    suspend fun login(userLoginField: String, password: String) : AppResult<User>{
        return firebaseUserRepo.login(userLoginField, password)
    }

    /**
     * Get current user via API
     */
    suspend fun getCurrentUserViaApi(): AppResult<AuthModels.UserProfile> {
        val tokens = _authTokens.value
        if (tokens == null) {
            return AppResult.Error(Exception("No authentication tokens available"))
        }
        
        return apiAuthRepo.getCurrentUser(tokens.token)
    }

    /**
     * Logout via API
     */
    suspend fun logoutViaApi(): AppResult<Unit> {
        val tokens = _authTokens.value
        if (tokens == null) {
            return AppResult.Error(Exception("No authentication tokens available"))
        }
        
        val result = apiAuthRepo.logout(tokens.token, tokens.refreshToken)
        
        if (result is AppResult.Success) {
            // Clear stored tokens
            _authTokens.postValue(null)
        }
        
        return result
    }

    /**
     * Clear authentication tokens
     */
    fun clearTokens() {
        _authTokens.postValue(null)
    }
}
