package com.summerveldhoundresort.app.ui.auth

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
import com.summerveldhoundresort.app.AdminActivity
import com.summerveldhoundresort.app.MainActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentLoginBinding
import com.summerveldhoundresort.app.db.AppResult
import com.summerveldhoundresort.app.db.implementations.firebaseUsersImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.CoroutineScope


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
                        val user = result.data
                        Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()

                        if (user.role == "admin") {
                            // Navigate to AdminActivity or AdminFragment
                            val intent = Intent(requireContext(), AdminActivity::class.java)
                            startActivity(intent)
                        } else{
                            // Normal user
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }

                        requireActivity().finish()

                      
                    }

                    is AppResult.Error -> {
                        Log.e(TAG, "Login Failed: ${result.exception?.message}") // Use e for errors
                        Toast.makeText(
                            requireContext(),
                            "Login failed. Please try again.",
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
            if (isGooglePlayServicesAvailable()) {
                val signInIntent = googleSignInClient.signInIntent
                signInLauncher.launch(signInIntent)
            } else {
                Toast.makeText(requireContext(), "Google Play Services is not available", Toast.LENGTH_SHORT).show()
            }
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
                    Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to MainActivity immediately
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    
                    // Handle user profile creation asynchronously without blocking
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        createGoogleUserProfileSafely(user)
                    }
                } else {
                    // Sign in failed.
                    Toast.makeText(requireContext(), "Firebase authentication failed.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Firebase authentication failed - ${task.exception?.message}", task.exception)
                }
            }
    }
    
    private fun createGoogleUserProfileSafely(user: FirebaseUser) {
        // Create a supervisor job to avoid cancelling
        val supervisorJob = SupervisorJob()
        val scope = CoroutineScope(Dispatchers.IO + supervisorJob)
        
        scope.launch {
            try {
                createOrUpdateUserProfile(user)
                Log.d(TAG, "Google user profile created successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create Google user profile: ${e.message}")
                // Profile creation will be retried when user accesses profile
            }
        }
    }
    
    private suspend fun createOrUpdateUserProfile(user: FirebaseUser) {
        try {
            val firestore = Firebase.firestore
            
            // Check if user document already exists
            val userDocSnapshot = firestore.collection("users").document(user.uid).get().await()
            
            if (!userDocSnapshot.exists()) {
                // Create new user profile for Google Sign-In
                val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                val userProfile = com.summerveldhoundresort.app.db.entities.User(
                    userID = user.uid,
                    username = user.displayName ?: user.email?.substringBefore("@") ?: "User",
                    email = user.email ?: "",
                    phoneNumber = "", // Google Sign-In doesn't provide phone number
                    creationDate = currentDate,
                    role = "user"
                )
                
                // Save to Firestore
                firestore.collection("users").document(user.uid).set(userProfile).await()
                Log.d(TAG, "Google user profile created in Firestore")
            } else {
                Log.d(TAG, "Google user profile already exists in Firestore")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create Google user profile", e)
        }
    }


    // Function to start the Google Sign-In flow
    private fun signInWithGoogle() {
        Log.d(TAG, "Starting Google Sign-In flow.")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    // Check if Google Play Services is available
    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(requireContext())
        
        return when (resultCode) {
            ConnectionResult.SUCCESS -> true
            else -> {
                Log.w(TAG, "Google Play Services not available: $resultCode")
                false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
