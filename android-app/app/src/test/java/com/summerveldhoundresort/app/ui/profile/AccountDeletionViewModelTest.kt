package com.summerveldhoundresort.app.ui.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
class AccountDeletionViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: com.summerveldhoundresort.app.network.repository.AuthRepository

    @Mock
    private lateinit var userRepository: com.summerveldhoundresort.app.network.repository.UserRepository

    @Mock
    private lateinit var deletionObserver: Observer<Boolean>

    @Mock
    private lateinit var errorObserver: Observer<String?>

    private lateinit var accountDeletionViewModel: AccountDeletionViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        accountDeletionViewModel = AccountDeletionViewModel(authRepository, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deleteAccount should delete user account successfully`() = runTest {
        // Given
        val userId = "user123"
        val password = "password123"
        `when`(authRepository.deleteAccount(userId, password)).thenReturn(true)

        // When
        accountDeletionViewModel.deleteAccount(userId, password)

        // Then
        verify(authRepository).deleteAccount(userId, password)
    }

    @Test
    fun `deleteAccount should handle invalid password`() = runTest {
        // Given
        val userId = "user123"
        val wrongPassword = "wrongpassword"
        `when`(authRepository.deleteAccount(userId, wrongPassword)).thenReturn(false)

        // When
        accountDeletionViewModel.deleteAccount(userId, wrongPassword)

        // Then
        verify(authRepository).deleteAccount(userId, wrongPassword)
    }

    @Test
    fun `deleteAccount should handle non-existent user`() = runTest {
        // Given
        val nonExistentUserId = "nonexistent"
        val password = "password123"
        `when`(authRepository.deleteAccount(nonExistentUserId, password)).thenReturn(false)

        // When
        accountDeletionViewModel.deleteAccount(nonExistentUserId, password)

        // Then
        verify(authRepository).deleteAccount(nonExistentUserId, password)
    }

    @Test
    fun `deleteAccount should handle repository error`() = runTest {
        // Given
        val userId = "user123"
        val password = "password123"
        val errorMessage = "Account deletion failed"
        `when`(authRepository.deleteAccount(userId, password)).thenThrow(RuntimeException(errorMessage))

        // When
        accountDeletionViewModel.deleteAccount(userId, password)

        // Then
        verify(authRepository).deleteAccount(userId, password)
    }

    @Test
    fun `validatePassword should return true for valid password`() = runTest {
        // Given
        val validPassword = "password123"

        // When
        val isValid = accountDeletionViewModel.validatePassword(validPassword)

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `validatePassword should return false for empty password`() = runTest {
        // Given
        val emptyPassword = ""

        // When
        val isValid = accountDeletionViewModel.validatePassword(emptyPassword)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `validatePassword should return false for short password`() = runTest {
        // Given
        val shortPassword = "123"

        // When
        val isValid = accountDeletionViewModel.validatePassword(shortPassword)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `validatePassword should return false for null password`() = runTest {
        // Given
        val nullPassword: String? = null

        // When
        val isValid = accountDeletionViewModel.validatePassword(nullPassword)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun `confirmDeletion should require password confirmation`() = runTest {
        // Given
        val password = "password123"
        val confirmPassword = "password123"

        // When
        val isConfirmed = accountDeletionViewModel.confirmDeletion(password, confirmPassword)

        // Then
        assertTrue(isConfirmed)
    }

    @Test
    fun `confirmDeletion should return false for mismatched passwords`() = runTest {
        // Given
        val password = "password123"
        val wrongConfirmPassword = "differentpassword"

        // When
        val isConfirmed = accountDeletionViewModel.confirmDeletion(password, wrongConfirmPassword)

        // Then
        assertFalse(isConfirmed)
    }

    @Test
    fun `confirmDeletion should return false for empty confirm password`() = runTest {
        // Given
        val password = "password123"
        val emptyConfirmPassword = ""

        // When
        val isConfirmed = accountDeletionViewModel.confirmDeletion(password, emptyConfirmPassword)

        // Then
        assertFalse(isConfirmed)
    }

    @Test
    fun `getDeletionWarningMessage should return appropriate warning`() = runTest {
        // When
        val warningMessage = accountDeletionViewModel.getDeletionWarningMessage()

        // Then
        assertNotNull(warningMessage)
        assertTrue(warningMessage.contains("permanent"))
        assertTrue(warningMessage.contains("delete"))
    }

    @Test
    fun `isDeletionConfirmed should track confirmation state`() = runTest {
        // Given
        val password = "password123"
        val confirmPassword = "password123"

        // When
        accountDeletionViewModel.confirmDeletion(password, confirmPassword)
        val isConfirmed = accountDeletionViewModel.isDeletionConfirmed()

        // Then
        assertTrue(isConfirmed)
    }

    @Test
    fun `clearConfirmation should reset confirmation state`() = runTest {
        // Given
        val password = "password123"
        val confirmPassword = "password123"
        accountDeletionViewModel.confirmDeletion(password, confirmPassword)

        // When
        accountDeletionViewModel.clearConfirmation()
        val isConfirmed = accountDeletionViewModel.isDeletionConfirmed()

        // Then
        assertFalse(isConfirmed)
    }
}
