package com.summerveldhoundresort.app.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentForgotPassawordBinding // Corrected binding class name
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth

class ForgotPassawordFragment : Fragment() {

    private var _binding: FragmentForgotPassawordBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth // Declare FirebaseAuth instance

    companion object {
        private const val TAG = "ForgotPassawordFragment" // A tag for logging
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPassawordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance() // Initialize Firebase Auth

        // Set up back button click listener
        binding.buttonBackToLogin.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.forgotPassswordButton.setOnClickListener {
            val emailAddress = binding.editTextTextEmailAddress.text.toString().trim()

            if (emailAddress.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your email address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Send password reset email
            firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Password reset email sent to: $emailAddress")
                        Toast.makeText(
                            requireContext(),
                            "Password reset email sent! Check your inbox.",
                            Toast.LENGTH_LONG
                        ).show()
                        // Optionally, navigate back to the login screen after sending the email
                        // findNavController().popBackStack()
                    } else {
                        Log.e(TAG, "Failed to send password reset email: ${task.exception?.message}")
                        Toast.makeText(
                            requireContext(),
                            "Failed to send reset email. Try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
