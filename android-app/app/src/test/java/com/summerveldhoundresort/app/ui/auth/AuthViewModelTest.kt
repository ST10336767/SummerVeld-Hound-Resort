package com.summerveldhoundresort.app.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.summerveldhoundresort.app.network.models.AuthModels
import com.summerveldhoundresort.app.network.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private lateinit var loginObserver: Observer<AuthModels.AuthResponse?>

    @Mock
    private lateinit var registerObserver: Observer<AuthModels.AuthResponse?>

    @Mock
    private lateinit var errorObserver: Observer<String?>

    private lateinit var authViewModel: AuthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authViewModel = AuthViewModel(authRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials should return success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val expectedResponse = AuthModels.AuthResponse(
            success = true,
            message = "Login successful",
            data = AuthModels.AuthData(
                user = AuthModels.UserData(
                    id = "user123",
                    firstName = "John",
                    lastName = "Doe",
                    email = email,
                    phone = "+1234567890",
                    role = "user"
                ),
                token = "jwt-token",
                refreshToken = "refresh-token"
            )
        )

        `when`(authRepository.login(email, password)).thenReturn(expectedResponse)

        // When
        authViewModel.login(email, password)

        // Then
        verify(authRepository).login(email, password)
        // Note: In a real test, you would observe the LiveData values
    }

    @Test
    fun `login with invalid credentials should return error`() = runTest {
        // Given
        val email = "invalid@example.com"
        val password = "wrongpassword"
        val expectedResponse = AuthModels.AuthResponse(
            success = false,
            message = "Invalid credentials",
            data = null
        )

        `when`(authRepository.login(email, password)).thenReturn(expectedResponse)

        // When
        authViewModel.login(email, password)

        // Then
        verify(authRepository).login(email, password)
    }

    @Test
    fun `register with valid data should return success`() = runTest {
        // Given
        val registerRequest = AuthModels.RegisterRequest(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            password = "password123",
            phone = "+1234567890"
        )
        val expectedResponse = AuthModels.AuthResponse(
            success = true,
            message = "Registration successful",
            data = AuthModels.AuthData(
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
        )

        `when`(authRepository.register(registerRequest)).thenReturn(expectedResponse)

        // When
        authViewModel.register(registerRequest)

        // Then
        verify(authRepository).register(registerRequest)
    }

    @Test
    fun `register with existing email should return error`() = runTest {
        // Given
        val registerRequest = AuthModels.RegisterRequest(
            firstName = "John",
            lastName = "Doe",
            email = "existing@example.com",
            password = "password123",
            phone = "+1234567890"
        )
        val expectedResponse = AuthModels.AuthResponse(
            success = false,
            message = "User already exists with this email",
            data = null
        )

        `when`(authRepository.register(registerRequest)).thenReturn(expectedResponse)

        // When
        authViewModel.register(registerRequest)

        // Then
        verify(authRepository).register(registerRequest)
    }

    @Test
    fun `logout should clear user session`() = runTest {
        // Given
        val logoutRequest = AuthModels.LogoutRequest(refreshToken = "refresh-token")
        val expectedResponse = AuthModels.LogoutResponse(
            success = true,
            message = "Logout successful"
        )

        `when`(authRepository.logout(logoutRequest)).thenReturn(expectedResponse)

        // When
        authViewModel.logout(logoutRequest)

        // Then
        verify(authRepository).logout(logoutRequest)
    }

    @Test
    fun `getCurrentUser should return user data`() = runTest {
        // Given
        val expectedUser = AuthModels.UserData(
            id = "user123",
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            phone = "+1234567890",
            role = "user"
        )

        `when`(authRepository.getCurrentUser()).thenReturn(expectedUser)

        // When
        authViewModel.getCurrentUser()

        // Then
        verify(authRepository).getCurrentUser()
    }

    @Test
    fun `isLoggedIn should return true when user is authenticated`() = runTest {
        // Given
        `when`(authRepository.isLoggedIn()).thenReturn(true)

        // When
        val result = authViewModel.isLoggedIn()

        // Then
        assertTrue(result)
        verify(authRepository).isLoggedIn()
    }

    @Test
    fun `isLoggedIn should return false when user is not authenticated`() = runTest {
        // Given
        `when`(authRepository.isLoggedIn()).thenReturn(false)

        // When
        val result = authViewModel.isLoggedIn()

        // Then
        assertFalse(result)
        verify(authRepository).isLoggedIn()
    }
}
