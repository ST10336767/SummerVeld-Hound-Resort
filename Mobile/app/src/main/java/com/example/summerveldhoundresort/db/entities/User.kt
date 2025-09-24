package com.example.summerveldhoundresort.db.entities

import java.util.UUID

data class User(
    var userID: String = UUID.randomUUID().toString(),
    var username : String = "",
    var password : String = "",
    var email : String = "",
    var phoneNumber : String = "",
    val creationDate : String = "",
    var role: String = "user"
)
