package com.example.summerveldhoundresort.ui.images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.network.models.ImageData
import com.example.summerveldhoundresort.network.repository.ImageRepository
import kotlinx.coroutines.launch

class ImageViewModel : ViewModel() {
    
    private val imageRepository = ImageRepository()
    
    private val _petProfileUploadResult = MutableLiveData<AppResult<ImageData>>()
    val petProfileUploadResult: LiveData<AppResult<ImageData>> = _petProfileUploadResult
    
    fun uploadPetProfileImage(imageUri: android.net.Uri, petId: String) {
        viewModelScope.launch {
            try {
                _petProfileUploadResult.value = AppResult.Loading
                val result = imageRepository.uploadPetProfileImage(imageUri, petId)
                _petProfileUploadResult.value = result
            } catch (e: Exception) {
                _petProfileUploadResult.value = AppResult.Error(e)
            }
        }
    }
}
