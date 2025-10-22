package com.summerveldhoundresort.app.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentDogDetailBinding
import com.summerveldhoundresort.app.db.entities.Dog
import java.text.SimpleDateFormat
import java.util.*

class DogDetailFragment : Fragment() {

    private var _binding: FragmentDogDetailBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get dog data from arguments
        val dogId = arguments?.getString("dogID") ?: ""
        val dogName = arguments?.getString("dogName") ?: "Unknown"
        val breed = arguments?.getString("breed") ?: "Unknown"
        val colour = arguments?.getString("colour") ?: "Unknown"
        val gender = arguments?.getString("gender") ?: "Unknown"
        val description = arguments?.getString("description") ?: "No description available."
        val imageUri = arguments?.getString("imageUri") ?: ""
        val dobTimestamp = arguments?.getLong("dogDOB") ?: 0L

        // Display dog information
        binding.textDogNameDetail.text = dogName
        binding.textBreedDetail.text = breed
        binding.textColorDetail.text = colour
        binding.textGenderDetail.text = gender
        binding.textDescriptionDetail.text = description

        // Format and display date of birth
        if (dobTimestamp > 0) {
            val dobDate = Date(dobTimestamp)
            binding.textDobDetail.text = dateFormat.format(dobDate)
        } else {
            binding.textDobDetail.text = "Unknown"
        }

        // Load dog image with Supabase URL support and better error handling
        if (imageUri.isNotEmpty()) {
            android.util.Log.d("DogDetailFragment", "=== DETAIL VIEW IMAGE LOADING ===")
            android.util.Log.d("DogDetailFragment", "Dog: $dogName")
            android.util.Log.d("DogDetailFragment", "Original Image URI: '$imageUri'")
            
            // Convert signed URL to public URL if needed
            val publicImageUrl = if (imageUri.contains("/sign/")) {
                imageUri.replace("/sign/", "/public/").split("?")[0] // Remove token parameter
            } else {
                imageUri
            }
            
            android.util.Log.d("DogDetailFragment", "Using public URL: '$publicImageUrl'")
            android.util.Log.d("DogDetailFragment", "Is signed URL: ${imageUri.contains("token=")}")
            
            Glide.with(this)
                .load(publicImageUrl)
                .placeholder(R.drawable.dog_placeholder)
                .error(R.drawable.dog_placeholder)
                .centerCrop()
                .into(binding.imageDogDetail)
        } else {
            android.util.Log.w("DogDetailFragment", "Empty image URI for dog: $dogName")
            binding.imageDogDetail.setImageResource(R.drawable.dog_placeholder)
        }

        // Back button navigation
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun createBundle(dog: Dog): Bundle {
            return Bundle().apply {
                putString("dogID", dog.dogID)
                putString("dogName", dog.dogName)
                putString("breed", dog.breed)
                putString("colour", dog.colour)
                putString("gender", dog.gender)
                putString("description", dog.description)
                putString("imageUri", dog.imageUri)
                putLong("dogDOB", dog.dogDOB.time)
            }
        }
    }
}

