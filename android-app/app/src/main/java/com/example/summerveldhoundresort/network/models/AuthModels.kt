package com.example.summerveldhoundresort.network.models

import com.google.gson.annotations.SerializedName

/**
 * Authentication-related data models
 */

// Request Models
data class RegisterRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LogoutRequest(
    @SerializedName("refreshToken") val refreshToken: String?
)

// Response Models
data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: AuthData
)

data class AuthData(
    @SerializedName("user") val user: UserData,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String,
    @SerializedName("lastLogin") val lastLogin: String? = null
)

data class UserResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: UserProfileData
)

data class UserProfileData(
    @SerializedName("user") val user: UserProfile
)

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String,
    @SerializedName("address") val address: Address? = null,
    @SerializedName("emergencyContact") val emergencyContact: EmergencyContact? = null,
    @SerializedName("lastLogin") val lastLogin: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class Address(
    @SerializedName("street") val street: String? = null,
    @SerializedName("city") val city: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("zipCode") val zipCode: String? = null,
    @SerializedName("country") val country: String? = null
)

data class EmergencyContact(
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("relationship") val relationship: String? = null
)

data class LogoutResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)
