package com.example.summerveldhoundresort.ui.auth

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.entities.User
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepo: firebaseUsersImpl) : ViewModel() {

    suspend fun register(username: String, email: String, password: String, phoneNumber: String, creationDate:String): AppResult<Unit>{
        return  userRepo.register(username, email, password, phoneNumber, creationDate)
    }

    suspend fun login(userLoginField: String, password: String) : AppResult<User>{
           return userRepo.login(userLoginField, password)
    }

}