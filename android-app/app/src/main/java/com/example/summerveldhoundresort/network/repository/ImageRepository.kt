package com.example.summerveldhoundresort.network.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.network.NetworkConfig
import com.example.summerveldhoundresort.network.models.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.SocketTimeoutException
import kotlin.random.Random
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface

/**
 * Repository for handling image operations with the REST API
 */
class ImageRepository {
    
    private val imageApiService = NetworkConfig.imageApiService
    private val auth = FirebaseAuth.getInstance()
    
    companion object {
        private const val TAG = "ImageRepository"
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val BASE_RETRY_DELAY_MS = 1000L // 1 second
        private const val MAX_RETRY_DELAY_MS = 10000L // 10 seconds
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
     * Execute operation with retry logic for network failures
     */
    private suspend fun <T> executeWithRetry(
        operation: suspend () -> T,
        operationName: String
    ): T {
        var lastException: Exception? = null
        
        repeat(MAX_RETRY_ATTEMPTS) { attempt ->
            try {
                Log.d(TAG, "$operationName - Attempt ${attempt + 1}/$MAX_RETRY_ATTEMPTS")
                return operation()
            } catch (e: Exception) {
                lastException = e
                
                // Only retry on specific network-related exceptions
                val shouldRetry = when (e) {
                    is SocketTimeoutException -> {
                        Log.w(TAG, "$operationName - Timeout on attempt ${attempt + 1}: ${e.message}")
                        true
                    }
                    is java.net.UnknownHostException -> {
                        Log.w(TAG, "$operationName - Network error on attempt ${attempt + 1}: ${e.message}")
                        true
                    }
                    is java.net.ConnectException -> {
                        Log.w(TAG, "$operationName - Connection error on attempt ${attempt + 1}: ${e.message}")
                        true
                    }
                    else -> {
                        Log.e(TAG, "$operationName - Non-retryable error on attempt ${attempt + 1}: ${e.message}")
                        false
                    }
                }
                
                if (!shouldRetry || attempt == MAX_RETRY_ATTEMPTS - 1) {
                    return@repeat
                }
                
                // Exponential backoff with jitter
                val delayMs = minOf(
                    BASE_RETRY_DELAY_MS * (1 shl attempt) + Random.nextLong(0, 1000),
                    MAX_RETRY_DELAY_MS
                )
                Log.d(TAG, "$operationName - Retrying in ${delayMs}ms...")
                delay(delayMs)
            }
        }
        
        throw lastException ?: Exception("Unknown error in $operationName")
    }
    
    /**
     * Compress image to reduce file size and upload time
     */
    private fun compressImage(context: Context, imageUri: Uri, maxWidth: Int = 1200, maxHeight: Int = 1200, quality: Int = 85): File {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        
        // Get image orientation
        val orientation = getImageOrientation(context, imageUri)
        
        // Calculate new dimensions
        val (newWidth, newHeight) = calculateNewDimensions(
            originalBitmap.width, 
            originalBitmap.height, 
            maxWidth, 
            maxHeight
        )
        
        // Resize and rotate bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        val rotatedBitmap = if (orientation != 0) {
            rotateBitmap(scaledBitmap, orientation)
        } else {
            scaledBitmap
        }
        
        // Create compressed file
        val compressedFile = File.createTempFile("compressed_image", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(compressedFile)
        
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputStream.flush()
        outputStream.close()
        
        // Recycle bitmaps to free memory
        originalBitmap.recycle()
        scaledBitmap.recycle()
        if (rotatedBitmap != scaledBitmap) {
            rotatedBitmap.recycle()
        }
        
        Log.d(TAG, "Image compressed: ${compressedFile.length()} bytes")
        return compressedFile
    }
    
    /**
     * Get image orientation from EXIF data
     */
    private fun getImageOrientation(context: Context, imageUri: Uri): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()
            orientation
        } catch (e: Exception) {
            Log.w(TAG, "Could not read EXIF data: ${e.message}")
            ExifInterface.ORIENTATION_NORMAL
        }
    }
    
    /**
     * Calculate new dimensions maintaining aspect ratio
     */
    private fun calculateNewDimensions(originalWidth: Int, originalHeight: Int, maxWidth: Int, maxHeight: Int): Pair<Int, Int> {
        val ratio = minOf(maxWidth.toFloat() / originalWidth, maxHeight.toFloat() / originalHeight)
        return if (ratio < 1) {
            Pair((originalWidth * ratio).toInt(), (originalHeight * ratio).toInt())
        } else {
            Pair(originalWidth, originalHeight)
        }
    }
    
    /**
     * Rotate bitmap based on orientation
     */
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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
    ): AppResult<List<ImageData>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val imageParts = imageUris.map { uri ->
                val file = createFileFromUri(context, uri)
                val mimeType = getMimeType(context, uri) ?: "image/jpeg"
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
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
        var compressedFile: File? = null
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            // Compress image before upload to reduce size and upload time
            compressedFile = compressImage(context, imageUri, maxWidth = 800, maxHeight = 800, quality = 90)
            val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", compressedFile.name, requestFile)
            val petIdBody = petId.toRequestBody("text/plain".toMediaTypeOrNull())
            
            Log.d(TAG, "Starting pet profile image upload for petId: $petId, compressed file size: ${compressedFile.length()} bytes")
            
            val response = executeWithRetry(
                operation = {
                    imageApiService.uploadPetProfileImage(
                        token = "Bearer $token",
                        image = imagePart,
                        petId = petIdBody
                    )
                },
                operationName = "Upload pet profile image"
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { imageData ->
                    Log.d(TAG, "Pet profile image upload successful: ${imageData.publicUrl}")
                    AppResult.Success(imageData)
                } ?: AppResult.Error(Exception("No data received"))
            } else {
                val errorMessage = response.body()?.message ?: "Upload failed"
                val errorCode = response.code()
                Log.e(TAG, "Pet profile image upload failed: $errorMessage (HTTP $errorCode)")
                AppResult.Error(Exception("Upload failed: $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Upload pet profile image error (Ask Gemini)", e)
            AppResult.Error(e)
        } finally {
            // Clean up compressed file
            compressedFile?.let { file ->
                try {
                    file.delete()
                    Log.d(TAG, "Cleaned up compressed file: ${file.name}")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to delete compressed file: ${e.message}")
                }
            }
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
            
            val response = imageApiService.getImageUrl("Bearer $token", filePath)
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { urlData ->
                    AppResult.Success(urlData)
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
            
            val requestBody = SignedUrlRequest(filePath, expiresIn)
            val response = imageApiService.createSignedUrl("Bearer $token", requestBody)
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { signedUrlData ->
                    AppResult.Success(signedUrlData)
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
    suspend fun listImages(folder: String = "", limit: Int = 20, offset: Int = 0): AppResult<List<ImageData>> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val response = imageApiService.listImages("Bearer $token", folder, limit, offset)
            
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { imageList ->
                    AppResult.Success(imageList)
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
    suspend fun deleteImage(filePath: String): AppResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val token = getAuthToken() ?: return@withContext AppResult.Error(
                Exception("User not authenticated")
            )
            
            val response = imageApiService.deleteImage("Bearer $token", filePath)
            
            if (response.isSuccessful && response.body()?.success == true) {
                AppResult.Success(true)
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
     * Create file from URI
     */
    private fun createFileFromUri(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        
        return file
    }
    
    /**
     * Get MIME type from URI
     */
    private fun getMimeType(context: Context, uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }
}
