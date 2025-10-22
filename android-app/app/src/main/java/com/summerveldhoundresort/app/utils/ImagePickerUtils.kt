package com.summerveldhoundresort.app.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker

/**
 * Utility class for handling image picker operations
 */
object ImagePickerUtils {

    /**
     * Launch image picker for single image selection
     */
    fun launchImagePicker(fragment: Fragment) {
        ImagePicker.with(fragment)
            .crop() // Crop image
            .compress(1024) // Final image size will be less than 1 MB
            .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080
            .start()
    }

    /**
     * Launch image picker for camera capture
     */
    fun launchCamera(fragment: Fragment) {
        ImagePicker.with(fragment)
            .cameraOnly()
            .crop() // Crop image
            .compress(1024) // Final image size will be less than 1 MB
            .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080
            .start()
    }

    /**
     * Launch image picker with gallery only
     */
    fun launchGallery(fragment: Fragment) {
        ImagePicker.with(fragment)
            .galleryOnly()
            .crop() // Crop image
            .compress(1024) // Final image size will be less than 1 MB
            .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080
            .start()
    }

    /**
     * Launch image picker for profile images (square crop)
     */
    fun launchProfileImagePicker(fragment: Fragment) {
        ImagePicker.with(fragment)
            .cropSquare() // Crop to square for profile images
            .compress(512) // Smaller size for profile images
            .maxResultSize(400, 400) // Profile image size
            .start()
    }

    /**
     * Handle the result from ImagePicker in onActivityResult
     */
    fun handleImagePickerResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        onImageSelected: (Uri) -> Unit,
        onError: (String) -> Unit
    ) {
        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data!!
            onImageSelected(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            onError(ImagePicker.getError(data))
        } else {
            onError("Task Cancelled")
        }
    }
}
