package com.example.summerveldhoundresort.network.api

import com.example.summerveldhoundresort.network.models.ImageUploadRequest
import com.example.summerveldhoundresort.network.models.ImageUploadResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageApiService {
    @POST("images/upload")
    suspend fun uploadImage(@Body request: ImageUploadRequest): Response<ImageUploadResponse>
}
