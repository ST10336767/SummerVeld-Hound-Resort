package com.example.summerveldhoundresort.db.entities

import java.util.Date

data class Dog(
    // Unique identifier for the dog (could be generated or retrieved from a database)
    var dogID: String = "",

// Name of the dog
    var dogName: String = "",

// Date of birth of the dog (initialized as an empty Date)
    var dogDOB: Date = Date(),

// Breed of the dog (e.g., Labrador, German Shepherd)
    var breed: String = "",

// Color of the dog’s coat (e.g., Brown, Black, White)
    var colour: String = "",

// Gender of the dog (e.g., Male, Female)
    var gender: String = "",

// Additional information or notes about the dog
    var description: String = "",

// URI for the dog's image, possibly stored as an encrypted string (bitcrypt)
    var imageUri: String = "",
)
