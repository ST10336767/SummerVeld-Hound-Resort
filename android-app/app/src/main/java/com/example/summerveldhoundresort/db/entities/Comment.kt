package com.example.summerveldhoundresort.db.entities

data class Comment(
    val userId: String = "",
    val username: String = "",
    val text: String = "",
    val timestamp: Long = 0
)
