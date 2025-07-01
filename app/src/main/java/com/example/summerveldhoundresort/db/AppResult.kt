package com.example.summerveldhoundresort.db

//Will be used to return from db, either success and returning what is needed
// of Error, and return nothing
//Good for genericism
sealed class AppResult<out T> {
    data class Success<T>(val data: T): AppResult<T>()
    data class Error<T>(val exception: Exception): AppResult<Nothing>()
}