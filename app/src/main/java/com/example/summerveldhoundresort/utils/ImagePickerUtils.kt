package com.example.summerveldhoundresort.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker

object ImagePickerUtils {
    
    fun launchCamera(fragment: Fragment) {
        ImagePicker.with(fragment)
            .cameraOnly()
            .start()
    }
    
    fun launchGallery(fragment: Fragment) {
        ImagePicker.with(fragment)
            .galleryOnly()
            .start()
    }
    
    fun launchCamera(activity: Activity) {
        ImagePicker.with(activity)
            .cameraOnly()
            .start()
    }
    
    fun launchGallery(activity: Activity) {
        ImagePicker.with(activity)
            .galleryOnly()
            .start()
    }
    
    fun handleImagePickerResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onImageSelected: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return
            onImageSelected(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            onError(ImagePicker.getError(data))
        }
    }
}
