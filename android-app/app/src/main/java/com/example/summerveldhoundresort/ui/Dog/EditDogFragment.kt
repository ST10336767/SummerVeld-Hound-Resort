package com.example.summerveldhoundresort.ui.saved

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentEditDogBinding
import com.example.summerveldhoundresort.db.entities.Dog
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EditDogFragment : Fragment() {

    private var _binding: FragmentEditDogBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private var dog: Dog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditDogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            }
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
            findNavController().navigateUp()
        }
}

        private fun saveDogChanges() {
        val id = dog?.dogID ?: return
        val updatedData = mapOf(
            "dogName" to binding.editDogName.text.toString().trim(),
            "breed" to binding.editBreed.text.toString().trim(),
            "colour" to binding.editColour.text.toString().trim(),
            "gender" to binding.spinnerGender.selectedItem.toString(),
            "description" to binding.editDescription.text.toString().trim(),
            "dogDOB" to (dog?.dogDOB ?: Date()),
            "imageUri" to dog?.imageUri,
            "updatedAt" to Date()
        )

        db.collection("dogs").document(id)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Dog updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error updating dog: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deleteDog() {
        val id = dog?.dogID ?: return
        db.collection("dogs").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Dog deleted successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error deleting dog: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
