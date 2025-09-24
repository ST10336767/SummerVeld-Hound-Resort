package com.example.summerveldhoundresort.ui.images

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.summerveldhoundresort.databinding.FragmentImageUploadBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.utils.ImagePickerUtils

class ImageUploadFragment : Fragment() {
    private var _binding: FragmentImageUploadBinding? = null
    private val binding get() = _binding!!
    
    private val imageViewModel: ImageViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageUploadBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupImageUploadObserver()
        
        binding.buttonSelectImage.setOnClickListener {
            showImagePickerOptions()
        }
        
        binding.buttonUploadImage.setOnClickListener {
            uploadImage()
        }
    }
    
    private fun setupImageUploadObserver() {
        imageViewModel.petProfileUploadResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is AppResult.Success -> {
                    binding.buttonUploadImage.isEnabled = true
                    Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                }
                is AppResult.Error -> {
                    binding.buttonUploadImage.isEnabled = true
                    Toast.makeText(requireContext(), "Image upload failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
                is AppResult.Loading -> {
                    binding.buttonUploadImage.isEnabled = false
                }
            }
        })
    }
    
    private fun showImagePickerOptions() {
        val options = arrayOf("Camera", "Gallery", "Cancel")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> {} // Cancel
                }
            }
            .show()
    }
    
    private fun openCamera() {
        ImagePickerUtils.launchCamera(this)
    }
    
    private fun openGallery() {
        ImagePickerUtils.launchGallery(this)
    }
    
    private fun uploadImage() {
        selectedImageUri?.let { uri ->
            val petId = "temp_pet_id" // This should be passed from the calling activity/fragment
            imageViewModel.uploadPetProfileImage(uri, petId)
        } ?: run {
            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        ImagePickerUtils.handleImagePickerResult(
            requestCode = requestCode,
            resultCode = resultCode,
            data = data,
            onImageSelected = { uri ->
                selectedImageUri = uri
                binding.imageViewSelected.setImageURI(uri)
                binding.imageViewSelected.visibility = View.VISIBLE
            },
            onError = { error ->
                Toast.makeText(context, "Image picker error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
