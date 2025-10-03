# API Integration Guide

This guide explains how to use the new API integration with your Render backend.

## 🚀 Quick Start

Your Android app is now configured to use the Render API at:
**https://summerveld-api.onrender.com**

## 🔧 What Was Updated

### 1. Network Configuration
- **Updated `NetworkConfig.kt`** with Render API URL
- **Added HTTPS support** (disabled cleartext traffic)
- **Created `AuthApiService`** for authentication endpoints
- **Created `AuthRepository`** for API communication

### 2. Authentication System
- **Added API-based authentication** alongside Firebase
- **Created new models** for API requests/responses
- **Updated `AuthViewModel`** with API methods

## 📱 How to Use

### Option 1: Use API Authentication (Recommended)

```kotlin
// In your Fragment/Activity
class LoginFragment : Fragment() {
    
    private lateinit var authViewModel: AuthViewModel
    
    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            val result = authViewModel.loginViaApi(email, password)
            
            when (result) {
                is AppResult.Success -> {
                    val authData = result.data
                    // Store tokens for future API calls
                    // Navigate to main activity
                }
                is AppResult.Error -> {
                    // Handle error
                    Toast.makeText(context, "Login failed: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun registerUser(firstName: String, lastName: String, email: String, password: String, phone: String) {
        lifecycleScope.launch {
            val result = authViewModel.registerViaApi(firstName, lastName, email, password, phone)
            
            when (result) {
                is AppResult.Success -> {
                    val authData = result.data
                    // User registered successfully
                    // Navigate to main activity
                }
                is AppResult.Error -> {
                    // Handle error
                    Toast.makeText(context, "Registration failed: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
```

### Option 2: Keep Using Firebase (Legacy)

The Firebase authentication methods are still available:

```kotlin
// Legacy Firebase methods (still work)
val result = authViewModel.login(email, password)
val result = authViewModel.register(username, email, password, phone, date)
```

## 🔑 API Endpoints Available

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user profile
- `POST /api/auth/logout` - Logout user

### Images (Existing)
- `POST /api/images/upload` - Upload single image
- `POST /api/images/upload-multiple` - Upload multiple images
- `POST /api/images/pet-profile` - Upload pet profile image

## 🧪 Testing

### 1. Test API Connection
```kotlin
// Test if API is reachable
lifecycleScope.launch {
    try {
        val response = NetworkConfig.authApiService.login(
            AuthModels.LoginRequest("test@example.com", "password123")
        )
        Log.d("API_TEST", "API Response: ${response.code()}")
    } catch (e: Exception) {
        Log.e("API_TEST", "API Error: ${e.message}")
    }
}
```

### 2. Test Registration
```kotlin
val result = authViewModel.registerViaApi(
    firstName = "John",
    lastName = "Doe", 
    email = "john.doe@example.com",
    password = "password123",
    phone = "+27123456789"
)
```

### 3. Test Login
```kotlin
val result = authViewModel.loginViaApi(
    email = "john.doe@example.com",
    password = "password123"
)
```

## 🔄 Migration Strategy

### Phase 1: Test API (Current)
- Keep Firebase authentication as fallback
- Test API authentication in parallel
- Use `loginViaApi()` and `registerViaApi()` methods

### Phase 2: Full Migration
- Replace Firebase calls with API calls
- Update all authentication flows
- Remove Firebase authentication dependency

### Phase 3: Cleanup
- Remove Firebase authentication code
- Keep Firebase for other features (if needed)

## 🐛 Troubleshooting

### Common Issues

1. **Network Error**
   - Check internet connection
   - Verify API URL is correct
   - Check Render deployment status

2. **Authentication Failed**
   - Verify user credentials
   - Check API logs in Render dashboard
   - Ensure JWT secrets are set in Render

3. **CORS Error**
   - API is configured for mobile apps
   - Should not occur with proper configuration

### Debug Mode

Enable network logging in `NetworkConfig.kt`:
```kotlin
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

## 📋 Next Steps

1. **Test the integration** with the provided examples
2. **Update your UI** to use the new API methods
3. **Add error handling** for better user experience
4. **Implement token storage** for persistent login
5. **Add loading states** for better UX

## 🔗 Resources

- **API Base URL**: https://summerveld-api.onrender.com
- **Health Check**: https://summerveld-api.onrender.com/health
- **Render Dashboard**: https://dashboard.render.com/
- **API Documentation**: Check your backend README

---

Your Android app is now ready to connect to the Render API! 🎉
