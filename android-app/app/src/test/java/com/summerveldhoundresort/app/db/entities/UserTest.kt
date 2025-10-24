package com.summerveldhoundresort.app.db.entities

import org.junit.Test
import org.junit.Assert.*

class UserTest {

    @Test
    fun `user creation with all parameters should work correctly`() {
        // Given
        val userId = "user123"
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val phone = "+1234567890"
        val role = "user"

        // When
        val user = User(
            id = userId,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            role = role
        )

        // Then
        assertEquals(userId, user.id)
        assertEquals(firstName, user.firstName)
        assertEquals(lastName, user.lastName)
        assertEquals(email, user.email)
        assertEquals(phone, user.phone)
        assertEquals(role, user.role)
    }

    @Test
    fun `user creation with minimal parameters should work correctly`() {
        // Given
        val firstName = "Jane"
        val lastName = "Smith"

        // When
        val user = User(
            id = "",
            firstName = firstName,
            lastName = lastName,
            email = "",
            phone = "",
            role = ""
        )

        // Then
        assertEquals(firstName, user.firstName)
        assertEquals(lastName, user.lastName)
        assertTrue(user.id.isEmpty())
        assertTrue(user.email.isEmpty())
        assertTrue(user.phone.isEmpty())
        assertTrue(user.role.isEmpty())
    }

    @Test
    fun `user with valid email format should be accepted`() {
        // Given
        val validEmails = listOf(
            "test@example.com",
            "user.name@domain.co.uk",
            "user+tag@example.org",
            "123@test.com"
        )

        validEmails.forEach { email ->
            // When
            val user = User(
                id = "test",
                firstName = "Test",
                lastName = "User",
                email = email,
                phone = "",
                role = "user"
            )

            // Then
            assertEquals(email, user.email)
        }
    }

    @Test
    fun `user with empty name should be valid`() {
        // Given
        val user = User(
            id = "test",
            firstName = "",
            lastName = "",
            email = "test@example.com",
            phone = "",
            role = "user"
        )

        // Then
        assertTrue(user.firstName.isEmpty())
        assertTrue(user.lastName.isEmpty())
    }

    @Test
    fun `user with special characters in name should work`() {
        // Given
        val specialFirstName = "José-María"
        val specialLastName = "O'Connor-Smith"
        val user = User(
            id = "test",
            firstName = specialFirstName,
            lastName = specialLastName,
            email = "test@example.com",
            phone = "",
            role = "user"
        )

        // Then
        assertEquals(specialFirstName, user.firstName)
        assertEquals(specialLastName, user.lastName)
    }
}
