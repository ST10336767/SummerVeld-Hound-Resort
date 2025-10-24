package com.summerveldhoundresort.app.network.models

import org.junit.Test
import org.junit.Assert.*

class ImageModelsTest {

    @Test
    fun `ImageUploadResponse should create with success and data`() {
        // Given
        val success = true
        val message = "Image uploaded successfully"
        val imageData = ImageData(
            id = "img123",
            url = "https://example.com/image.jpg",
            filename = "image.jpg",
            size = 1024,
            uploadedAt = "2024-01-15T10:00:00Z"
        )

        // When
        val response = ImageUploadResponse(
            success = success,
            message = message,
            data = imageData
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
        assertEquals(imageData, response.data)
        assertEquals("img123", response.data.id)
        assertEquals("https://example.com/image.jpg", response.data.url)
    }

    @Test
    fun `ImageData should create with all image information`() {
        // Given
        val id = "img123"
        val url = "https://example.com/image.jpg"
        val filename = "image.jpg"
        val size = 1024L
        val uploadedAt = "2024-01-15T10:00:00Z"

        // When
        val imageData = ImageData(
            id = id,
            url = url,
            filename = filename,
            size = size,
            uploadedAt = uploadedAt
        )

        // Then
        assertEquals(id, imageData.id)
        assertEquals(url, imageData.url)
        assertEquals(filename, imageData.filename)
        assertEquals(size, imageData.size)
        assertEquals(uploadedAt, imageData.uploadedAt)
    }

    @Test
    fun `MultipleImageUploadResponse should create with multiple images`() {
        // Given
        val success = true
        val message = "Images uploaded successfully"
        val images = listOf(
            ImageData(
                id = "img1",
                url = "https://example.com/image1.jpg",
                filename = "image1.jpg",
                size = 1024,
                uploadedAt = "2024-01-15T10:00:00Z"
            ),
            ImageData(
                id = "img2",
                url = "https://example.com/image2.jpg",
                filename = "image2.jpg",
                size = 2048,
                uploadedAt = "2024-01-15T10:01:00Z"
            )
        )

        // When
        val response = MultipleImageUploadResponse(
            success = success,
            message = message,
            data = MultipleImageData(images = images)
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
        assertEquals(2, response.data.images.size)
        assertEquals("img1", response.data.images[0].id)
        assertEquals("img2", response.data.images[1].id)
    }

    @Test
    fun `ImageUrlResponse should create with image URL`() {
        // Given
        val success = true
        val message = "Image URL generated"
        val imageUrl = "https://example.com/signed-url.jpg"

        // When
        val response = ImageUrlResponse(
            success = success,
            message = message,
            data = ImageUrlData(url = imageUrl)
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
        assertEquals(imageUrl, response.data.url)
    }

    @Test
    fun `SignedUrlResponse should create with signed URL`() {
        // Given
        val success = true
        val message = "Signed URL generated"
        val signedUrl = "https://example.com/signed-url?token=abc123"

        // When
        val response = SignedUrlResponse(
            success = success,
            message = message,
            data = SignedUrlData(url = signedUrl)
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
        assertEquals(signedUrl, response.data.url)
    }

    @Test
    fun `ImageListResponse should create with list of images`() {
        // Given
        val success = true
        val message = "Images retrieved successfully"
        val images = listOf(
            ImageListItem(
                id = "img1",
                url = "https://example.com/image1.jpg",
                filename = "image1.jpg",
                uploadedAt = "2024-01-15T10:00:00Z"
            ),
            ImageListItem(
                id = "img2",
                url = "https://example.com/image2.jpg",
                filename = "image2.jpg",
                uploadedAt = "2024-01-15T10:01:00Z"
            )
        )

        // When
        val response = ImageListResponse(
            success = success,
            message = message,
            data = ImageListData(images = images)
        )

        // Then
        assertTrue(response.success)
        assertEquals(message, response.message)
        assertEquals(2, response.data.images.size)
        assertEquals("img1", response.data.images[0].id)
        assertEquals("img2", response.data.images[1].id)
    }

    @Test
    fun `ImageMetadata should create with metadata information`() {
        // Given
        val width = 1920
        val height = 1080
        val format = "JPEG"
        val colorSpace = "sRGB"
        val hasAlpha = false

        // When
        val metadata = ImageMetadata(
            width = width,
            height = height,
            format = format,
            colorSpace = colorSpace,
            hasAlpha = hasAlpha
        )

        // Then
        assertEquals(width, metadata.width)
        assertEquals(height, metadata.height)
        assertEquals(format, metadata.format)
        assertEquals(colorSpace, metadata.colorSpace)
        assertFalse(metadata.hasAlpha)
    }

    @Test
    fun `ApiErrorResponse should create with error information`() {
        // Given
        val success = false
        val message = "Error occurred"
        val error = "Validation failed"
        val code = 400

        // When
        val response = ApiErrorResponse(
            success = success,
            message = message,
            error = error,
            code = code
        )

        // Then
        assertFalse(response.success)
        assertEquals(message, response.message)
        assertEquals(error, response.error)
        assertEquals(code, response.code)
    }

    @Test
    fun `ImageUploadOptions should create with upload options`() {
        // Given
        val maxSize = 5242880L // 5MB
        val allowedFormats = listOf("JPEG", "PNG", "WEBP")
        val quality = 85

        // When
        val options = ImageUploadOptions(
            maxSize = maxSize,
            allowedFormats = allowedFormats,
            quality = quality
        )

        // Then
        assertEquals(maxSize, options.maxSize)
        assertEquals(allowedFormats, options.allowedFormats)
        assertEquals(quality, options.quality)
    }

    @Test
    fun `PetProfileUploadRequest should create with pet profile data`() {
        // Given
        val petId = "pet123"
        val petName = "Buddy"
        val imageUrl = "https://example.com/pet-image.jpg"

        // When
        val request = PetProfileUploadRequest(
            petId = petId,
            petName = petName,
            imageUrl = imageUrl
        )

        // Then
        assertEquals(petId, request.petId)
        assertEquals(petName, request.petName)
        assertEquals(imageUrl, request.imageUrl)
    }
}
