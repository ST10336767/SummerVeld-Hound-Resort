package com.example.summerveldhoundresort.network.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.network.NetworkConfig
import com.example.summerveldhoundresort.network.models.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * Repository for handling image operations with the REST API
 */
class ImageRepository {
    
    private val imageApiService = NetworkConfig.imageApiService
    private val auth = FirebaseAuth.getInstance()
    
    companion object {
        private const val TAG = "ImageRepository"
    }
    
    /**
     * Get authentication token from Firebase Auth
     */
    private fun getAuthToken(): String? {
        // Temporary test token for development - remove in production
        return "test-token"
        
        // Original Firebase token code (uncomment when Firebase is fixed)
        // return auth.currentUser?.getIdToken(false)?.result?.token
    }
    
    /**
     * Upload single image
     */
    suspend fun uploadSingleImage(
        context: Context,
        imageUri: Uri,
        options: ImageUploadOptions = ImageUploadOptions()
    ): AppResult<ImageData> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val file = createFileFromUri(context, imageUri)
            val mimeType = getMimeType(context, imageUri) ?: "image/jpeg"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            
            val folderBody = options.folder.toRequestBody("text/plain".toMediaTypeOrNull())
            val widthBody = options.width?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val heightBody = options.height?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val qualityBody = options.quality?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val formatBody = options.format?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val response = imageApiService.uploadSingleImage(
                token = "Bearer $token",
                image = imagePart,
                folder = folderBody,
                width = widthBody,
                height = heightBody,
                quality = qualityBody,
                format = formatBody
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { imageData ->
                    AppResult.Success(imageData)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Upload failed"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload single image error", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Upload multiple images
     */
    suspend fun uploadMultipleImages(
        context: Context,
        imageUris: List<Uri>,
        options: ImageUploadOptions = ImageUploadOptions()
    ): AppResult<MultipleImageData> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val imageParts = imageUris.map { uri ->
                val file = createFileFromUri(context, uri)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("images", file.name, requestFile)
            }
            
            val folderBody = options.folder.toRequestBody("text/plain".toMediaTypeOrNull())
            val widthBody = options.width?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val heightBody = options.height?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val qualityBody = options.quality?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val formatBody = options.format?.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val response = imageApiService.uploadMultipleImages(
                token = "Bearer $token",
                images = imageParts,
                folder = folderBody,
                width = widthBody,
                height = heightBody,
                quality = qualityBody,
                format = formatBody
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { data ->
                    AppResult.Success(data)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Upload failed"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload multiple images error", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Upload pet profile image
     */
    suspend fun uploadPetProfileImage(
        context: Context,
        imageUri: Uri,
        petId: String
    ): AppResult<ImageData> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val file = createFileFromUri(context, imageUri)
            val mimeType = getMimeType(context, imageUri) ?: "image/jpeg"
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val petIdBody = petId.toRequestBody("text/plain".toMediaTypeOrNull())
            
            val response = imageApiService.uploadPetProfileImage(
                token = "Bearer $token",
                image = imagePart,
                petId = petIdBody
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { imageData ->
                    AppResult.Success(imageData)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Upload failed"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload pet profile image error", e)
            AppResult.Error(e)
        }
    }
    
    
    /**
     * Get image URL
     */
    suspend fun getImageUrl(filePath: String): AppResult<ImageUrlData> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val response = imageApiService.getImageUrl(
                token = "Bearer $token",
                filePath = filePath
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { data ->
                    AppResult.Success(data)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Failed to get image URL"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Get image URL error", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Create signed URL for private access
     */
    suspend fun createSignedUrl(filePath: String, expiresIn: Int = 3600): AppResult<SignedUrlData> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val request = SignedUrlRequest(filePath, expiresIn)
            val response = imageApiService.createSignedUrl(
                token = "Bearer $token",
                request = request
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { data ->
                    AppResult.Success(data)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Failed to create signed URL"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Create signed URL error", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * List images in folder
     */
    suspend fun listImages(
        folder: String = "",
        limit: Int = 50,
        offset: Int = 0
    ): AppResult<ImageListData> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val response = imageApiService.listImages(
                token = "Bearer $token",
                folder = folder.ifEmpty { null },
                limit = limit,
                offset = offset
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { data ->
                    AppResult.Success(data)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Failed to list images"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "List images error", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Delete image
     */
    suspend fun deleteImage(filePath: String): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val response = imageApiService.deleteImage(
                token = "Bearer $token",
                filePath = filePath
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                AppResult.Success(Unit)
            } else {
                val errorMessage = response.body()?.message ?: "Failed to delete image"
                AppResult.Error(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Delete image error", e)
            AppResult.Error(e)
        }
    }
    
    /**
     * Create a temporary file from URI
     */
    private fun createFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        
        return tempFile
    }
    
    /**
     * Get MIME type from URI
     */
    private fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }
}
