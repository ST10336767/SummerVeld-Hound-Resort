package com.summerveldhoundresort.app.db.repos

import com.summerveldhoundresort.app.db.AppResult
import com.summerveldhoundresort.app.db.entities.User

interface UsersRepo {

    suspend fun register(name: String, email: String, password: String, phoneNumber: String,  creationDate: String): AppResult<Unit>
    suspend fun login(identifier: String, password: String): AppResult<User>
    fun logout()
    suspend fun getCurrentUser(): AppResult<User?>

    suspend fun updateUser(user: User)
    suspend fun getUserBy(identifier : String): AppResult<User?>
}
