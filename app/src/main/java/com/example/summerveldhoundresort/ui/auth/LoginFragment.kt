package com.example.summerveldhoundresort.ui.auth

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.MainActivity
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentLoginBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
class LoginFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // NEW: Google Sign-In client and Firebase Auth instance
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    // NEW: Request code for Google Sign-In
    companion object {
        private const val RC_GOOGLE_SIGN_IN = 9001 // Renamed to avoid confusion with ContentValues.TAG
        private const val TAG = "LoginFragment" // Use a more specific TAG for this class
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root // Return the root directly here
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fbUserImpl = firebaseUsersImpl()
        val factory = AuthViewModelFactory { AuthViewModel(fbUserImpl) }
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Your server's client ID
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        // Set click listener for the traditional email/password Login button
        binding.buttonLogin.setOnClickListener {
            val emailAddress = binding.edtEmailAddress.text.toString().trim() // Trim whitespace
            val password =
                binding.textInputPassword.editText?.text.toString().trim() // Trim whitespace

            // Basic validation (you can add more robust validation)
            if (emailAddress.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Email and password cannot be empty.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = authViewModel.login(
                    userLoginField = emailAddress,
                    password = password
                )
                Log.d(TAG, "Login details: $emailAddress, password: $password ")
                when (result) {
                    is AppResult.Success -> {
                        Log.d(TAG, "SUCCESS result: $result")
                        Toast.makeText(
                            requireContext(),
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Navigate to MainActivity after successful login
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish() // Finish the activity hosting this fragment
                    }

                    is AppResult.Error -> {
                        Log.e(TAG, "Login Failed: ${result.exception?.message}") // Use e for errors
                        Toast.makeText(
                            requireContext(),
                            "Login Failed: ${result.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }



        }
        val signInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }
        // Set click listener for the Google Sign-In button
        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        }

        // Set click listener for Sign Up text
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPassawordFragment)
        }

    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle sign-in failure
            Toast.makeText(requireContext(), "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Firebase sign-in successful.
                    Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show()
                    // Now, trigger the biometric authentication flow.
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    // Sign in failed.
                    Toast.makeText(requireContext(), "Firebase authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    // Function to start the Google Sign-In flow
    private fun signInWithGoogle() {
        Log.d(TAG, "Starting Google Sign-In flow.")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    // Handles the result from the Google Sign-In activity


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}