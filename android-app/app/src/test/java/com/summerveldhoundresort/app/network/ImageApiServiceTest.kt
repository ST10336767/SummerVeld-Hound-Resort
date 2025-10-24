package com.summerveldhoundresort.app.network

import com.google.common.truth.Truth.assertThat
import com.summerveldhoundresort.app.TestConfig
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageApiServiceTest : TestConfig() {
    
    private lateinit var mockWebServer: MockWebServer
    private lateinit var imageApiService: ImageApiService
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        imageApiService = retrofit.create(ImageApiService::class.java)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
    
    @Test
    fun `upload single image should return success response`() = runBlocking {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "success": true,
                    "message": "Image uploaded successfully",
                    "data": {
                        "fileName": "test-image.jpg",
                        "filePath": "test/test-image.jpg",
                        "publicUrl": "https://test.supabase.co/storage/v1/object/public/images/test/test-image.jpg",
                        "size": 12345,
                        "originalName": "test-image.jpg"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)
        
        // When
        val response = imageApiService.uploadImage(
            image = null, // Mock multipart body
            folder = "test",
            width = 300,
            height = 300,
            quality = 90,
            format = "jpeg"
        )
        
        // Then
        assertThat(response.isSuccessful).isTrue()
        response.body()?.let { body ->
            assertThat(body.success).isTrue()
            assertThat(body.data.fileName).isEqualTo("test-image.jpg")
            assertThat(body.data.publicUrl).contains("supabase.co")
        }
    }
    
    @Test
    fun `upload image should handle error response`() = runBlocking {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody("""
                {
                    "success": false,
                    "message": "Invalid file type",
                    "error": "Only JPEG, PNG, WebP, and GIF images are allowed"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)
        
        // When
        val response = imageApiService.uploadImage(
            image = null,
            folder = "test"
        )
        
        // Then
        assertThat(response.isSuccessful).isFalse()
        assertThat(response.code()).isEqualTo(400)
    }
    
    @Test
    fun `get image URL should return public URL`() = runBlocking {
        // Given
        val filePath = "test/test-image.jpg"
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "success": true,
                    "data": {
                        "publicUrl": "https://test.supabase.co/storage/v1/object/public/images/test/test-image.jpg"
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)
        
        // When
        val response = imageApiService.getImageUrl(filePath)
        
        // Then
        assertThat(response.isSuccessful).isTrue()
        response.body()?.let { body ->
            assertThat(body.success).isTrue()
            assertThat(body.data.publicUrl).contains("supabase.co")
        }
    }
}
