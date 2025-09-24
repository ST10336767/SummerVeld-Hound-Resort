package com.example.summerveldhoundresort.network.models

data class ImageData(
    val id: String,
    val publicUrl: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val uploadedAt: String
)

data class ImageUploadRequest(
    val image: String, // Base64 encoded image
    val fileName: String,
    val mimeType: String
)

data class ImageUploadResponse(
    val success: Boolean,
    val data: ImageData?,
    val message: String?
)
