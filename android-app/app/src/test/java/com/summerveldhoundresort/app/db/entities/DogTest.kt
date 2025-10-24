package com.summerveldhoundresort.app.db.entities

import org.junit.Test
import org.junit.Assert.*
import java.util.Date

class DogTest {

    @Test
    fun `dog creation with all parameters should work correctly`() {
        // Given
        val dogId = "dog123"
        val dogName = "Buddy"
        val breed = "Golden Retriever"
        val colour = "Golden"
        val gender = "Male"
        val description = "Friendly and energetic dog"
        val dogDOB = Date()
        val imageUri = "https://example.com/dog.jpg"
        val ownerId = "owner123"

        // When
        val dog = Dog(
            id = dogId,
            dogName = dogName,
            breed = breed,
            colour = colour,
            gender = gender,
            description = description,
            dogDOB = dogDOB,
            imageUri = imageUri,
            ownerId = ownerId
        )

        // Then
        assertEquals(dogId, dog.id)
        assertEquals(dogName, dog.dogName)
        assertEquals(breed, dog.breed)
        assertEquals(colour, dog.colour)
        assertEquals(gender, dog.gender)
        assertEquals(description, dog.description)
        assertEquals(dogDOB, dog.dogDOB)
        assertEquals(imageUri, dog.imageUri)
        assertEquals(ownerId, dog.ownerId)
    }

    @Test
    fun `dog creation with minimal parameters should work correctly`() {
        // Given
        val dogName = "Max"
        val breed = "Labrador"

        // When
        val dog = Dog(
            id = "",
            dogName = dogName,
            breed = breed,
            colour = "",
            gender = "",
            description = "",
            dogDOB = Date(0),
            imageUri = "",
            ownerId = ""
        )

        // Then
        assertEquals(dogName, dog.dogName)
        assertEquals(breed, dog.breed)
        assertTrue(dog.id.isEmpty())
        assertTrue(dog.colour.isEmpty())
        assertTrue(dog.gender.isEmpty())
        assertTrue(dog.description.isEmpty())
        assertTrue(dog.imageUri.isEmpty())
        assertTrue(dog.ownerId.isEmpty())
    }

    @Test
    fun `dog with empty name should be valid`() {
        // Given
        val dog = Dog(
            id = "test",
            dogName = "",
            breed = "Test Breed",
            colour = "Brown",
            gender = "Male",
            description = "Test dog",
            dogDOB = Date(),
            imageUri = "",
            ownerId = "owner123"
        )

        // Then
        assertTrue(dog.dogName.isEmpty())
        assertEquals("Test Breed", dog.breed)
    }

    @Test
    fun `dog with special characters in name should work`() {
        // Given
        val specialName = "Buddy-2 & Max's Friend"
        val dog = Dog(
            id = "test",
            dogName = specialName,
            breed = "Mixed",
            colour = "Multi",
            gender = "Male",
            description = "Special character test",
            dogDOB = Date(),
            imageUri = "",
            ownerId = "owner123"
        )

        // Then
        assertEquals(specialName, dog.dogName)
    }
}
