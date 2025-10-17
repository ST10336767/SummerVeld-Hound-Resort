package com.example.summerveldhoundresort.ui.profile

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentEditProfileBinding
import com.example.summerveldhoundresort.databinding.FragmentProfileViewBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        val fbUserImpl = firebaseUsersImpl()
        val factory = ProfileViewModelFactory{ProfileViewModel(fbUserImpl)}
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        //Populating the fields with user info
        lifecycleScope.launch {
            when (val result = profileViewModel.getCurrentUser()) {
                is AppResult.Success -> {
                    result.data?.let { user ->
                        binding.edtName.hint = user.username
                        binding.edtEmail.hint = user.email
                        binding.edtPhoneNum.hint = user.phoneNumber
                    }
                }
                is AppResult.Error -> {
                    Log.e(TAG, "Failed to fetch user", result.exception)
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonUpdate.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val name = binding.edtName.text.toString()
            val phoneNum = binding.edtPhoneNum.text.toString()

            //Updating user details
            Log.d(TAG, "Updating user info now")
            profileViewModel.updateUserInfo(name.ifEmpty { null }, email.ifEmpty { null }, phoneNum.ifEmpty { null })
            Toast.makeText(requireContext(), "Profile info updated successfully", Toast.LENGTH_SHORT).show()
        }

        // Set up back button click listener
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

}