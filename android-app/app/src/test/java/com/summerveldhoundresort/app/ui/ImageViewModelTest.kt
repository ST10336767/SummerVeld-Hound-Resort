package com.summerveldhoundresort.app.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.summerveldhoundresort.app.TestConfig
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
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class ImageViewModelTest : TestConfig() {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @Mock
    private lateinit var mockImageRepository: com.summerveldhoundresort.app.repository.ImageRepository
    
    private lateinit var imageViewModel: ImageViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        imageViewModel = ImageViewModel(mockImageRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `upload image should update loading state`() = runTest {
        // Given
        val mockFile = java.io.File("test-image.jpg")
        whenever(mockImageRepository.uploadImage(any(), any(), any(), any(), any()))
            .thenReturn(com.summerveldhoundresort.app.models.ImageUploadResult(
                success = true,
                message = "Success",
                data = com.summerveldhoundresort.app.models.ImageData(
                    fileName = "test.jpg",
                    filePath = "test/test.jpg",
                    publicUrl = "https://test.com/image.jpg",
                    size = 12345,
                    originalName = "test.jpg"
                )
            ))
        
        // When
        imageViewModel.uploadImage(mockFile, "test", 300, 300, 90, "jpeg")
        
        // Then
        assertThat(imageViewModel.isLoading.value).isTrue()
    }
    
    @Test
    fun `upload image success should update state correctly`() = runTest {
        // Given
        val mockFile = java.io.File("test-image.jpg")
        val expectedResult = com.summerveldhoundresort.app.models.ImageUploadResult(
            success = true,
            message = "Success",
            data = com.summerveldhoundresort.app.models.ImageData(
                fileName = "test.jpg",
                filePath = "test/test.jpg",
                publicUrl = "https://test.com/image.jpg",
                size = 12345,
                originalName = "test.jpg"
            )
        )
        whenever(mockImageRepository.uploadImage(any(), any(), any(), any(), any()))
            .thenReturn(expectedResult)
        
        // When
        imageViewModel.uploadImage(mockFile, "test", 300, 300, 90, "jpeg")
        
        // Then
        assertThat(imageViewModel.isLoading.value).isFalse()
        assertThat(imageViewModel.uploadResult.value).isEqualTo(expectedResult)
        assertThat(imageViewModel.errorMessage.value).isNull()
    }
    
    @Test
    fun `upload image failure should update error state`() = runTest {
        // Given
        val mockFile = java.io.File("test-image.jpg")
        val expectedResult = com.summerveldhoundresort.app.models.ImageUploadResult(
            success = false,
            message = "Upload failed",
            error = "Network error"
        )
        whenever(mockImageRepository.uploadImage(any(), any(), any(), any(), any()))
            .thenReturn(expectedResult)
        
        // When
        imageViewModel.uploadImage(mockFile, "test", 300, 300, 90, "jpeg")
        
        // Then
        assertThat(imageViewModel.isLoading.value).isFalse()
        assertThat(imageViewModel.uploadResult.value).isEqualTo(expectedResult)
        assertThat(imageViewModel.errorMessage.value).isEqualTo("Upload failed")
    }
    
    @Test
    fun `clear error should reset error message`() {
        // Given
        imageViewModel.errorMessage.value = "Test error"
        
        // When
        imageViewModel.clearError()
        
        // Then
        assertThat(imageViewModel.errorMessage.value).isNull()
    }
}
