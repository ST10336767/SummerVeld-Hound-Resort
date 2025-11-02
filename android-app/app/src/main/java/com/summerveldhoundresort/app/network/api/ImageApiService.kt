package com.summerveldhoundresort.app.network.api

import com.summerveldhoundresort.app.network.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for image operations
 */
interface ImageApiService {
    
    /**
     * Upload single image
     */
    @Multipart
    @POST("images/upload")
    suspend fun uploadSingleImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("folder") folder: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("quality") quality: RequestBody?,
        @Part("format") format: RequestBody?
    ): Response<ImageUploadResponse>
    
    /**
     * Upload multiple images
     */
    @Multipart
    @POST("images/upload-multiple")
    suspend fun uploadMultipleImages(
        @Header("Authorization") token: String,
        @Part images: List<MultipartBody.Part>,
        @Part("folder") folder: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("height") height: RequestBody?,
        @Part("quality") quality: RequestBody?,
        @Part("format") format: RequestBody?
    ): Response<MultipleImageUploadResponse>
    
    /**
     * Upload pet profile image
     */
    @Multipart
    @POST("images/pet-profile")
    suspend fun uploadPetProfileImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("petId") petId: RequestBody
    ): Response<ImageUploadResponse>
    
    
    /**
     * Get image URL
     */
    @GET("images/url/{filePath}")
    suspend fun getImageUrl(
        @Header("Authorization") token: String,
        @Path("filePath") filePath: String
    ): Response<ImageUrlResponse>
    
    /**
     * Create signed URL for private access
     */
    @POST("images/signed-url")
    suspend fun createSignedUrl(
        @Header("Authorization") token: String,
        @Body request: SignedUrlRequest
    ): Response<SignedUrlResponse>
    
    /**
     * List images in folder
     */
    @GET("images/list")
    suspend fun listImages(
        @Header("Authorization") token: String,
        @Query("folder") folder: String?,
        @Query("limit") limit: Int?,
        @Query("offset") offset: Int?
    ): Response<ImageListResponse>
    
    /**
     * Delete image
     */
    @DELETE("images/{filePath}")
    suspend fun deleteImage(
        @Header("Authorization") token: String,
        @Path("filePath") filePath: String
    ): Response<ApiErrorResponse>
}
