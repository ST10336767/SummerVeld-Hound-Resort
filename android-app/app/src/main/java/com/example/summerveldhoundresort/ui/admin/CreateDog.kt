package com.example.summerveldhoundresort.ui.admin

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.databinding.FragmentCreateDogBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.entities.Dog
import com.example.summerveldhoundresort.ui.images.ImageViewModel
import com.example.summerveldhoundresort.utils.ImagePickerUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import com.example.summerveldhoundresort.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateDog : Fragment() {
    private var _binding: FragmentCreateDogBinding? = null
    private val binding get() = _binding!!

    private val firestoreDb = FirebaseFirestore.getInstance()
    private val imageViewModel: ImageViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var dogId: String? = null

    // Date formatter
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Glide.with(this).load(it).into(binding.imageViewDogPicture)
            binding.imageViewDogPicture.visibility = View.VISIBLE
            binding.textViewPlaceholderText.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageUploadObserver()

        binding.frameLayoutPicturePlaceholder.setOnClickListener {
            showImagePickerOptions()
        }

        // Use both click and focus listeners for robust behavior
        binding.editTextDob.setOnClickListener {
            showDatePickerDialog()
        }

        binding.editTextDob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showDatePickerDialog()
            }
        }

        binding.buttonAddDog.setOnClickListener {
            saveDogData()

        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Create a Calendar object with the selected date
                calendar.set(selectedYear, selectedMonth, selectedDay)
                // Format the date and set it to the EditText
                binding.editTextDob.setText(dateFormat.format(calendar.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun saveDogData() {
        val dogName = binding.editTextName.text.toString().trim()
        val dogBreed = binding.editTextBreed.text.toString().trim()
        val dogDobString = binding.editTextDob.text.toString().trim()
        val dogColour = binding.editTextColour.text.toString().trim()
        val dogGender = binding.spinnerGender.selectedItem?.toString()?.trim() ?: ""
        val dogDescription = binding.editTextDescription.text.toString().trim()

        if (dogName.isEmpty() || dogBreed.isEmpty() || dogDobString.isEmpty() ||
            dogColour.isEmpty() || dogGender.isEmpty() || dogDescription.isEmpty() || selectedImageUri == null
        ) {
            Toast.makeText(requireContext(), "Please fill in all fields and select a picture.", Toast.LENGTH_SHORT).show()
            return
        }

        val dogDOB: Date = try {
            dateFormat.parse(dogDobString) ?: Date()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid DOB format. Please use the date picker.", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate dog ID first
        dogId = firestoreDb.collection("dogs").document().id
        
        binding.buttonAddDog.isEnabled = false
        uploadPetProfileImage(dogName, dogDOB, dogBreed, dogColour, dogGender, dogDescription)

    }

    private fun setupImageUploadObserver() {
        imageViewModel.petProfileUploadResult.observe(viewLifecycleOwner, Observer { result ->
            android.util.Log.d("CreateDog", "Image upload observer triggered: $result")
            when (result) {
                is AppResult.Success -> {
                    android.util.Log.d("CreateDog", "Image upload successful, saving dog data to Firestore")
                    Toast.makeText(requireContext(), "Image uploaded! Saving dog data...", Toast.LENGTH_SHORT).show()
                    
                    // Image uploaded successfully, now save dog data to Firestore
                    saveDogToFirestore(
                        dogName = arguments?.getString("dogName") ?: "",
                        dogDOB = arguments?.getSerializable("dogDOB") as? Date ?: Date(),
                        dogBreed = arguments?.getString("dogBreed") ?: "",
                        dogColour = arguments?.getString("dogColour") ?: "",
                        dogGender = arguments?.getString("dogGender") ?: "",
                        dogDescription = arguments?.getString("dogDescription") ?: "",
                        imageData = result.data
                    )
                }
                is AppResult.Error -> {
                    binding.buttonAddDog.isEnabled = true
                    android.util.Log.e("CreateDog", "Image upload failed: ${result.exception.message}")
                    
                    // Provide user-friendly error messages
                    val errorMessage = when {
                        result.exception.message?.contains("timeout", ignoreCase = true) == true -> {
                            "Image upload timed out. Please check your internet connection and try again with a smaller image."
                        }
                        result.exception.message?.contains("network", ignoreCase = true) == true -> {
                            "Network error. Please check your internet connection and try again."
                        }
                        result.exception.message?.contains("size", ignoreCase = true) == true -> {
                            "Image file is too large. Please select a smaller image."
                        }
                        else -> {
                            "Image upload failed. Please try again. Error: ${result.exception.message}"
                        }
                    }
                    
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
                else -> {
                    android.util.Log.d("CreateDog", "Image upload result: $result")
                }
            }
        })
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Camera", "Gallery", "Cancel")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Dog Picture")
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

    private fun uploadPetProfileImage(
        dogName: String, dogDOB: Date, dogBreed: String, dogColour: String,
        dogGender: String, dogDescription: String
    ) {
        android.util.Log.d("CreateDog", "Starting image upload process")
        android.util.Log.d("CreateDog", "Dog ID for upload: $dogId")
        android.util.Log.d("CreateDog", "Selected image URI: $selectedImageUri")
        
        selectedImageUri?.let { uri ->
            android.util.Log.d("CreateDog", "Storing dog data in arguments for observer")
            
            // Store dog data temporarily to use in observer
            arguments = Bundle().apply {
                putString("dogName", dogName)
                putSerializable("dogDOB", dogDOB)
                putString("dogBreed", dogBreed)
                putString("dogColour", dogColour)
                putString("dogGender", dogGender)
                putString("dogDescription", dogDescription)
            }
            
            // Show progress message to user
            Toast.makeText(requireContext(), "Uploading image... This may take a moment for large images.", Toast.LENGTH_LONG).show()
            
            android.util.Log.d("CreateDog", "Calling imageViewModel.uploadPetProfileImage")
            imageViewModel.uploadPetProfileImage(uri, dogId ?: "")
        } ?: run {
            android.util.Log.e("CreateDog", "Selected image URI is null!")
            Toast.makeText(requireContext(), "No image selected!", Toast.LENGTH_SHORT).show()
            binding.buttonAddDog.isEnabled = true
        }
    }

    private fun saveDogToFirestore(
        dogName: String, dogDOB: Date, dogBreed: String,
        dogColour: String, dogGender: String, dogDescription: String, imageData: com.example.summerveldhoundresort.network.models.ImageData
    ) {
        android.util.Log.d("CreateDog", "Starting to save dog to Firestore")
        android.util.Log.d("CreateDog", "Dog ID: $dogId")
        android.util.Log.d("CreateDog", "Image URL: ${imageData.publicUrl}")
        
        val dog = Dog(
            dogID = dogId ?: "",
            dogName = dogName,
            dogDOB = dogDOB,
            breed = dogBreed,
            colour = dogColour,
            gender = dogGender,
            description = dogDescription,
            imageUri = imageData.publicUrl,
            createdAt = Date(),
            updatedAt = Date()
        )

        android.util.Log.d("CreateDog", "Dog object created: $dog")

        firestoreDb.collection("dogs")
            .document(dogId ?: "")
            .set(dog)
            .addOnSuccessListener {
                android.util.Log.d("CreateDog", "Dog saved successfully to Firestore")
                binding.buttonAddDog.isEnabled = true
                Toast.makeText(requireContext(), "Dog added successfully!", Toast.LENGTH_SHORT).show()
                resetFields()
            }
            .addOnFailureListener { e ->
                android.util.Log.e("CreateDog", "Failed to save dog to Firestore: ${e.message}")
                binding.buttonAddDog.isEnabled = true
                Toast.makeText(requireContext(), "Failed to add dog: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun resetFields() {
        binding.editTextName.text?.clear()
        binding.editTextBreed.text?.clear()
        binding.editTextDob.text?.clear()
        binding.editTextColour.text?.clear()
        binding.editTextDescription.text?.clear()
        binding.spinnerGender.setSelection(0)
        binding.imageViewDogPicture.setImageDrawable(null)
        binding.imageViewDogPicture.visibility = View.GONE
        binding.textViewPlaceholderText.visibility = View.VISIBLE
        selectedImageUri = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        ImagePickerUtils.handleImagePickerResult(
            requestCode = requestCode,
            resultCode = resultCode,
            data = data,
            onImageSelected = { uri ->
                selectedImageUri = uri
                Glide.with(this).load(uri).into(binding.imageViewDogPicture)
                binding.imageViewDogPicture.visibility = View.VISIBLE
                binding.textViewPlaceholderText.visibility = View.GONE
            },
            onError = { error ->
                Toast.makeText(context, "Image picker error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
