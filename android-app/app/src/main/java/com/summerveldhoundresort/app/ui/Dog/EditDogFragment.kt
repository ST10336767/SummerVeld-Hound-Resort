package com.summerveldhoundresort.app.ui.Dog

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentEditDogBinding
import com.summerveldhoundresort.app.db.AppResult
import com.summerveldhoundresort.app.db.entities.Dog
import com.summerveldhoundresort.app.ui.images.ImageViewModel
import com.summerveldhoundresort.app.utils.ImagePickerUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditDogFragment : Fragment() {

    private var _binding: FragmentEditDogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var dog: Dog? = null
    private val imageViewModel: ImageViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var isImageChanged = false

    // Remove the old getContent launcher - we'll use ImagePicker directly

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Scrollable edit description
        binding.editDescription.setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                v.parent.requestDisallowInterceptTouchEvent(false)
            }

            v.performClick()
            v.onTouchEvent(event)
            true
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val targetPadding = imeInsets.bottom

            v.setPadding(0, 0, 0, targetPadding)
            insets
        }

        // Auto Scroll
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val scrollView = binding.EditDogScroll
            val focusedView = requireActivity().currentFocus ?: return@addOnGlobalLayoutListener

            val rect = android.graphics.Rect()
            scrollView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = scrollView.rootView.height
            val keyboardHeight = screenHeight - rect.bottom
            val isKeyboardVisible = keyboardHeight > screenHeight * 0.15

            if (isKeyboardVisible) {
                scrollView.postDelayed({
                    val location = IntArray(2)
                    focusedView.getLocationInWindow(location)
                    val focusedBottom = location[1] + focusedView.height

                    // Extra space for multiline
                    val extraPadding = if (focusedView is android.widget.EditText &&
                        focusedView.lineCount > 1) 300 else 150

                    // Scrolls if the field is  hidden
                    if (focusedBottom > rect.bottom - 80) {
                        scrollView.smoothScrollTo(
                            0,
                            scrollView.scrollY + (focusedBottom - rect.bottom + extraPadding)
                        )
                    }
                }, 100)
            }
        }

        setupImageUploadObserver()
        
        dog = arguments?.getSerializable("dog") as? Dog

        dog?.let { d ->
            binding.editDogName.setText(d.dogName)
            binding.editBreed.setText(d.breed)
            binding.editColour.setText(d.colour)
            binding.editDescription.setText(d.description)
            if (d.dogDOB.time > 0) {
                binding.editDob.setText(dateFormat.format(d.dogDOB))
            }

            if (d.imageUri.isNotEmpty()) {
                Glide.with(this)
                    .load(d.imageUri)
                    .placeholder(R.drawable.dog_placeholder)
                    .centerCrop()
                    .into(binding.imageDogEdit)
                binding.textViewPlaceholderText.visibility = View.GONE
            } else {
                binding.textViewPlaceholderText.visibility = View.VISIBLE
            }
        }

        // Set up image click listener
        binding.frameLayoutPicturePlaceholder.setOnClickListener {
            showImagePickerOptions()
        }

        // Gender Spinner setup
        val genderAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Male", "Female")
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = genderAdapter
        dog?.gender?.let { currentGender ->
            val pos = genderAdapter.getPosition(currentGender)
            if (pos >= 0) binding.spinnerGender.setSelection(pos)
        }

        // Date picker
        binding.editDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            if (dog != null) calendar.time = dog!!.dogDOB
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(requireContext(), { _, y, m, d ->
                calendar.set(y, m, d)
                binding.editDob.setText(dateFormat.format(calendar.time))
                dog?.dogDOB = calendar.time
            }, year, month, day).show()
        }

        // Save
        binding.buttonSaveDog.setOnClickListener {
            saveDogChanges()
        }

// Delete
        binding.buttonDeleteDog.setOnClickListener {
            deleteDog()
        }

// Cancel/Back
        binding.buttonCancelEdit.setOnClickListener {
            try {
                // Since this fragment is used in an Activity, finish the activity
                if (isAdded && !requireActivity().isFinishing) {
                    requireActivity().finish()
                }
            } catch (e: Exception) {
                // If there's an error, try alternative navigation
                try {
                    requireActivity().onBackPressed()
                } catch (e2: Exception) {
                    // Last resort - just log the error
                    android.util.Log.e("EditDogFragment", "Error handling back button", e2)
                }
            }
        }
    }

    private fun saveDogChanges() {
        val id = dog?.dogID ?: return
        
        // Validate required fields
        val dogName = binding.editDogName.text.toString().trim()
        val breed = binding.editBreed.text.toString().trim()
        val colour = binding.editColour.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        
        if (dogName.isEmpty() || breed.isEmpty() || colour.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (isImageChanged && selectedImageUri != null) {
            // Upload new image first
            uploadNewImage(id)
        } else {
            // Update without changing image
            updateDogData(id, dogName, breed, colour, description, dog?.imageUri ?: "")
        }
    }
    
    private fun uploadNewImage(id: String) {
        selectedImageUri?.let { uri ->
            Toast.makeText(requireContext(), "Uploading new image...", Toast.LENGTH_SHORT).show()
            imageViewModel.uploadPetProfileImage(uri, id)
        }
    }
    
    private fun updateDogData(id: String, dogName: String, breed: String, colour: String, description: String, imageUri: String) {
        val updatedData = mapOf(
            "dogName" to dogName,
            "breed" to breed,
            "colour" to colour,
            "gender" to binding.spinnerGender.selectedItem.toString(),
            "description" to description,
            "dogDOB" to (dog?.dogDOB ?: Date()),
            "imageUri" to imageUri,
            "updatedAt" to Date()
        )

        db.collection("dogs").document(id)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Dog updated successfully!", Toast.LENGTH_SHORT).show()
                try {
                    // Since this fragment is used in an Activity, finish the activity
                    if (isAdded && !requireActivity().isFinishing) {
                        requireActivity().finish()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("EditDogFragment", "Error finishing activity after update", e)
                }
            }
            .addOnFailureListener { _ ->
                Toast.makeText(requireContext(), "Error updating dog. Try again.", Toast.LENGTH_LONG).show()
            }
    }
    
    private fun setupImageUploadObserver() {
        // Observe the correct LiveData for pet profile uploads
        imageViewModel.petProfileUploadResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AppResult.Success -> {
                    val imageData = result.data
                    val imageUrl = imageData.publicUrl
                    // Update the dog data with the new image URL
                    dog?.let { d ->
                        updateDogData(
                            d.dogID ?: "",
                            binding.editDogName.text.toString().trim(),
                            binding.editBreed.text.toString().trim(),
                            binding.editColour.text.toString().trim(),
                            binding.editDescription.text.toString().trim(),
                            imageUrl
                        )
                    }
                }
                is AppResult.Error -> {
                    Toast.makeText(requireContext(), "Image upload failed. Try again.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    // Handle other states if needed
                }
            }
        }
    }
    
    private fun showImagePickerOptions() {
        val options = arrayOf("Camera", "Gallery", "Cancel")
        
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Change Dog Photo")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        ImagePickerUtils.handleImagePickerResult(
            requestCode = requestCode,
            resultCode = resultCode,
            data = data,
            onImageSelected = { uri ->
                selectedImageUri = uri
                isImageChanged = true
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.dog_placeholder)
                    .centerCrop()
                    .into(binding.imageDogEdit)
                binding.textViewPlaceholderText.visibility = View.GONE
                Toast.makeText(requireContext(), "Image selected successfully", Toast.LENGTH_SHORT).show()
            },
            onError = { error ->
                Toast.makeText(requireContext(), "Error selecting image: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun deleteDog() {
        val id = dog?.dogID ?: return
        
        // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Dog")
            .setMessage("Are you sure you want to delete ${dog?.dogName}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                performDelete(id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun performDelete(id: String) {
        db.collection("dogs").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Dog deleted successfully!", Toast.LENGTH_SHORT).show()
                try {
                    // Since this fragment is used in an Activity, finish the activity
                    if (isAdded && !requireActivity().isFinishing) {
                        requireActivity().finish()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("EditDogFragment", "Error finishing activity after delete", e)
                }
            }
            .addOnFailureListener { _ ->
                Toast.makeText(requireContext(), "Error deleting dog. Try again.", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
