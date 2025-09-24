package com.example.summerveldhoundresort.ui.profile

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentProfileViewBinding
import com.example.summerveldhoundresort.databinding.FragmentRegisterBinding
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import com.example.summerveldhoundresort.ui.auth.AuthViewModel
import com.example.summerveldhoundresort.ui.auth.AuthViewModelFactory

class ProfileViewFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

    //Followed viewbinding documentation for fragments for recommended approach
    // Site: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentProfileViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileViewBinding.inflate(inflater, container, false)

        val fbUserImpl = firebaseUsersImpl()
        val factory = ProfileViewModelFactory{ProfileViewModel(fbUserImpl)}
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        profileViewModel.username.observe(viewLifecycleOwner) {username ->
            binding.tvUsername.text = username
        }


        binding.mbEditProfile.setOnClickListener{
            findNavController().navigate(R.id.action_profileViewFragment_to_editProfileFragment)
        }
        binding.mbDataPrivacy.setOnClickListener{
//            findNavController().navigate(R.id.action_ProfileFragment_to_EditProfileFragment)
        }
        binding.mbChangePassword.setOnClickListener{
            findNavController().navigate(R.id.action_profileViewFragment_to_changePasswordFragment)
        }
        binding.mbSettings.setOnClickListener{
//            findNavController().navigate(R.id.action_ProfileFragment_to_EditProfileFragment)
        }
        binding.mbLogout.setOnClickListener{
            profileViewModel.logout()
        }

        return binding.root
    }

    //Part of viewbinding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}