package com.example.summerveldhoundresort.db.repos

import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.entities.User

interface UsersRepo {

    suspend fun register(name: String, email: String, password: String, phoneNumber: String,  creationDate: String): AppResult<Unit>
    suspend fun login(identifier: String, password: String): AppResult<User>
    suspend fun logout()
    suspend fun getCurrentUser(): AppResult<User?>

    suspend fun updateUser(user: User)
    suspend fun getUserBy(identifier : String): AppResult<User?>
}