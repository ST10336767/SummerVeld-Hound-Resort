package com.example.summerveldhoundresort.ui.images

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentImageUploadBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.network.models.ImageUploadOptions
import com.example.summerveldhoundresort.utils.ImagePickerUtils

/**
 * Fragment for uploading images
 */
class ImageUploadFragment : Fragment() {
    
    private var _binding: FragmentImageUploadBinding? = null
    private val binding get() = _binding!!
    
    private val imageViewModel: ImageViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageUploadBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupClickListeners()
        setupObservers()
    }
    
    private fun setupClickListeners() {
        binding.btnSelectImage.setOnClickListener {
            showImagePickerOptions()
        }
        
        binding.btnUploadImage.setOnClickListener {
            uploadImage()
        }
        
        
        binding.btnClearImage.setOnClickListener {
            clearSelectedImage()
        }
        
        // Set up back button click listener
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupObservers() {
        // Observe upload result
        imageViewModel.uploadResult.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is AppResult.Success -> {
                    Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                    binding.tvUploadResult.text = "Upload successful!\nURL: ${result.data.publicUrl}"
                }
                is AppResult.Error -> {
                    Toast.makeText(context, "Upload failed: ${result.exception.message}", Toast.LENGTH_LONG).show()
                    binding.tvUploadResult.text = "Upload failed: ${result.exception.message}"
                }
                else -> {}
            }
        })
        
        
        // Observe loading state
        imageViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnUploadImage.isEnabled = !isLoading
        })
    }
    
    private fun showImagePickerOptions() {
        val options = arrayOf("Camera", "Gallery", "Cancel")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Image Source")
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
    
    private fun displaySelectedImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(binding.ivSelectedImage)
        
        binding.tvSelectedImage.text = "Image selected: ${uri.lastPathSegment}"
        binding.btnUploadImage.isEnabled = true
        binding.btnUploadUserProfile.isEnabled = true
    }
    
    private fun uploadImage() {
        selectedImageUri?.let { uri ->
            val options = ImageUploadOptions(
                folder = "general",
                width = 800,
                height = 600,
                quality = 85,
                format = "jpeg"
            )
            imageViewModel.uploadSingleImage(uri, options)
        } ?: run {
            Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }
    
    
    private fun clearSelectedImage() {
        selectedImageUri = null
        binding.ivSelectedImage.setImageResource(R.drawable.ic_launcher_foreground)
        binding.tvSelectedImage.text = "No image selected"
        binding.tvUploadResult.text = ""
        binding.btnUploadImage.isEnabled = false
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        ImagePickerUtils.handleImagePickerResult(
            requestCode = requestCode,
            resultCode = resultCode,
            data = data,
            onImageSelected = { uri ->
                selectedImageUri = uri
                displaySelectedImage(uri)
            },
            onError = { error ->
                Toast.makeText(context, "Image picker error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
