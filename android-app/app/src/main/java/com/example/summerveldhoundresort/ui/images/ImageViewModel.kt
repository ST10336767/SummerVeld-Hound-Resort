package com.example.summerveldhoundresort.ui.images

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.network.models.*
import com.example.summerveldhoundresort.network.repository.ImageRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for handling image operations
 */
class ImageViewModel(application: Application) : AndroidViewModel(application) {
    
    private val imageRepository = ImageRepository()
    
    // LiveData for upload results
    private val _uploadResult = MutableLiveData<AppResult<ImageData>>()
    val uploadResult: LiveData<AppResult<ImageData>> = _uploadResult
    
    private val _multipleUploadResult = MutableLiveData<AppResult<MultipleImageData>>()
    val multipleUploadResult: LiveData<AppResult<MultipleImageData>> = _multipleUploadResult
    
    private val _petProfileUploadResult = MutableLiveData<AppResult<ImageData>>()
    val petProfileUploadResult: LiveData<AppResult<ImageData>> = _petProfileUploadResult
    
    
    // LiveData for other operations
    private val _imageUrlResult = MutableLiveData<AppResult<ImageUrlData>>()
    val imageUrlResult: LiveData<AppResult<ImageUrlData>> = _imageUrlResult
    
    private val _signedUrlResult = MutableLiveData<AppResult<SignedUrlData>>()
    val signedUrlResult: LiveData<AppResult<SignedUrlData>> = _signedUrlResult
    
    private val _imageListResult = MutableLiveData<AppResult<ImageListData>>()
    val imageListResult: LiveData<AppResult<ImageListData>> = _imageListResult
    
    private val _deleteResult = MutableLiveData<AppResult<Unit>>()
    val deleteResult: LiveData<AppResult<Unit>> = _deleteResult
    
    // Loading states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    /**
     * Upload single image
     */
    fun uploadSingleImage(
        imageUri: Uri,
        options: ImageUploadOptions = ImageUploadOptions()
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.uploadSingleImage(
                context = getApplication(),
                imageUri = imageUri,
                options = options
            )
            _uploadResult.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * Upload multiple images
     */
    fun uploadMultipleImages(
        imageUris: List<Uri>,
        options: ImageUploadOptions = ImageUploadOptions()
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.uploadMultipleImages(
                context = getApplication(),
                imageUris = imageUris,
                options = options
            )
            _multipleUploadResult.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * Upload pet profile image
     */
    fun uploadPetProfileImage(imageUri: Uri, petId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.uploadPetProfileImage(
                context = getApplication(),
                imageUri = imageUri,
                petId = petId
            )
            _petProfileUploadResult.value = result
            _isLoading.value = false
        }
    }
    
    
    /**
     * Get image URL
     */
    fun getImageUrl(filePath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.getImageUrl(filePath)
            _imageUrlResult.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * Create signed URL
     */
    fun createSignedUrl(filePath: String, expiresIn: Int = 3600) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.createSignedUrl(filePath, expiresIn)
            _signedUrlResult.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * List images
     */
    fun listImages(folder: String = "", limit: Int = 50, offset: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.listImages(folder, limit, offset)
            _imageListResult.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * Delete image
     */
    fun deleteImage(filePath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = imageRepository.deleteImage(filePath)
            _deleteResult.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * Clear results
     */
    fun clearResults() {
        _uploadResult.value = null
        _multipleUploadResult.value = null
        _petProfileUploadResult.value = null
        _imageUrlResult.value = null
        _signedUrlResult.value = null
        _imageListResult.value = null
        _deleteResult.value = null
    }
}
