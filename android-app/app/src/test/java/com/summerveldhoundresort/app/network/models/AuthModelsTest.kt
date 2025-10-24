package com.summerveldhoundresort.app.network.models

import org.junit.Test
import org.junit.Assert.*

class AuthModelsTest {

    @Test
    fun `RegisterRequest should create with all parameters`() {
        // Given
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val password = "password123"
        val phone = "+1234567890"

        // When
        val request = AuthModels.RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            phone = phone
        )

        // Then
        assertEquals(firstName, request.firstName)
        assertEquals(lastName, request.lastName)
        assertEquals(email, request.email)
        assertEquals(password, request.password)
        assertEquals(phone, request.phone)
    }

    @Test
    fun `LoginRequest should create with email and password`() {
        // Given
        val email = "john.doe@example.com"
        val password = "password123"

        // When
        val request = AuthModels.LoginRequest(
            email = email,
            password = password
        )

        // Then
        assertEquals(email, request.email)
        assertEquals(password, request.password)
    }

    @Test
    fun `AuthResponse should create with success and data`() {
        // Given
        val success = true
        val message = "Login successful"
        val authData = AuthModels.AuthData(
            user = AuthModels.UserData(
                id = "user123",
                firstName = "John",
                lastName = "Doe",
                email = "john.doe@example.com",
                phone = "+1234567890",
                role = "user"
            ),
            token = "jwt-token",
            refreshToken = "refresh-token"
        )

        // When
        val response = AuthModels.AuthResponse(
            success = success,
            message = message,
            data = authData
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
        assertEquals(authData, response.data)
        assertEquals("user123", response.data.user.id)
        assertEquals("jwt-token", response.data.token)
    }

    @Test
    fun `UserData should create with all user information`() {
        // Given
        val id = "user123"
        val firstName = "John"
        val lastName = "Doe"
        val email = "john.doe@example.com"
        val phone = "+1234567890"
        val role = "user"

        // When
        val userData = AuthModels.UserData(
            id = id,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            role = role
        )

        // Then
        assertEquals(id, userData.id)
        assertEquals(firstName, userData.firstName)
        assertEquals(lastName, userData.lastName)
        assertEquals(email, userData.email)
        assertEquals(phone, userData.phone)
        assertEquals(role, userData.role)
    }

    @Test
    fun `Address should create with all address fields`() {
        // Given
        val street = "123 Main St"
        val city = "New York"
        val state = "NY"
        val zipCode = "10001"
        val country = "USA"

        // When
        val address = AuthModels.Address(
            street = street,
            city = city,
            state = state,
            zipCode = zipCode,
            country = country
        )

        // Then
        assertEquals(street, address.street)
        assertEquals(city, address.city)
        assertEquals(state, address.state)
        assertEquals(zipCode, address.zipCode)
        assertEquals(country, address.country)
    }

    @Test
    fun `EmergencyContact should create with contact information`() {
        // Given
        val name = "Jane Doe"
        val phone = "+1987654321"
        val relationship = "Spouse"

        // When
        val emergencyContact = AuthModels.EmergencyContact(
            name = name,
            phone = phone,
            relationship = relationship
        )

        // Then
        assertEquals(name, emergencyContact.name)
        assertEquals(phone, emergencyContact.phone)
        assertEquals(relationship, emergencyContact.relationship)
    }

    @Test
    fun `LogoutRequest should create with refresh token`() {
        // Given
        val refreshToken = "refresh-token-123"

        // When
        val request = AuthModels.LogoutRequest(
            refreshToken = refreshToken
        )

        // Then
        assertEquals(refreshToken, request.refreshToken)
    }

    @Test
    fun `LogoutResponse should create with success status`() {
        // Given
        val success = true
        val message = "Logout successful"

        // When
        val response = AuthModels.LogoutResponse(
            success = success,
            message = message
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
    }
}
