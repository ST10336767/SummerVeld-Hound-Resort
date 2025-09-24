package com.example.summerveldhoundresort.network.models

import com.google.gson.annotations.SerializedName

/**
 * Data models for image API operations
 */

data class ImageUploadResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ImageData?,
    @SerializedName("error")
    val error: String?
)

data class ImageData(
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("publicUrl")
    val publicUrl: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("originalName")
    val originalName: String
)

data class MultipleImageUploadResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: MultipleImageData?,
    @SerializedName("error")
    val error: String?
)

data class MultipleImageData(
    @SerializedName("successful")
    val successful: List<ImageData>,
    @SerializedName("failed")
    val failed: List<String>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("successfulCount")
    val successfulCount: Int,
    @SerializedName("failedCount")
    val failedCount: Int
)

data class ImageUrlResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ImageUrlData?,
    @SerializedName("error")
    val error: String?
)

data class ImageUrlData(
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("publicUrl")
    val publicUrl: String
)

data class SignedUrlResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: SignedUrlData?,
    @SerializedName("error")
    val error: String?
)

data class SignedUrlData(
    @SerializedName("signedUrl")
    val signedUrl: String,
    @SerializedName("expiresAt")
    val expiresAt: String
)

data class ImageListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: ImageListData?,
    @SerializedName("error")
    val error: String?
)

data class ImageListData(
    @SerializedName("images")
    val images: List<ImageListItem>,
    @SerializedName("folder")
    val folder: String,
    @SerializedName("count")
    val count: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("offset")
    val offset: Int
)

data class ImageListItem(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("last_accessed_at")
    val lastAccessedAt: String,
    @SerializedName("metadata")
    val metadata: ImageMetadata,
    @SerializedName("publicUrl")
    val publicUrl: String
)

data class ImageMetadata(
    @SerializedName("eTag")
    val eTag: String,
    @SerializedName("size")
    val size: Long,
    @SerializedName("mimetype")
    val mimetype: String,
    @SerializedName("cacheControl")
    val cacheControl: String,
    @SerializedName("lastModified")
    val lastModified: String,
    @SerializedName("contentLength")
    val contentLength: Long,
    @SerializedName("httpStatusCode")
    val httpStatusCode: Int
)

data class ApiErrorResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("error")
    val error: String?
)

// Request models
data class SignedUrlRequest(
    @SerializedName("filePath")
    val filePath: String,
    @SerializedName("expiresIn")
    val expiresIn: Int = 3600
)

data class ImageUploadOptions(
    val folder: String = "",
    val width: Int? = null,
    val height: Int? = null,
    val quality: Int? = null,
    val format: String? = null
)

data class PetProfileUploadRequest(
    @SerializedName("petId")
    val petId: String
)
