package com.summerveldhoundresort.app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentAdminProfileBinding
import com.summerveldhoundresort.app.ui.auth.AuthActivity

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set current user info
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Display name as big text, email as small text
            val displayName = user.displayName ?: user.email?.substringBefore("@") ?: "Admin User"
            binding.tvUsername.text = displayName
            binding.tvUserEmail.text = user.email ?: ""
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }

        // Back button
        binding.btnBack.setOnClickListener {
            // Go back to admin dashboard
            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
