package com.example.summerveldhoundresort.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


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
        val factory = AuthViewModelFactory{AuthViewModel(fbUserImpl)}
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In options
        // Use requestIdToken with R.string.default_web_client_id from google-services.json
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // This is crucial for Firebase Auth
            .requestEmail() // Request user's email address
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Set click listener for the traditional email/password Login button
        binding.buttonLogin.setOnClickListener{
            val emailAddress = binding.edtEmailAddress.text.toString().trim() // Trim whitespace
            val password = binding.textInputPassword.editText?.text.toString().trim() // Trim whitespace

            // Basic validation (you can add more robust validation)
            if (emailAddress.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = authViewModel.login(
                    userLoginField = emailAddress,
                    password = password
                )
                Log.d(TAG, "Login details: $emailAddress, password: $password ")
                when(result){
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
                    is AppResult.Error<*> -> {
                        Log.e(TAG, "Login Failed: ${result.exception?.message}") // Use e for errors
                        Toast.makeText(requireContext(), "Login Failed: ${result.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Set click listener for the Google Sign-In button
        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Set click listener for Sign Up text
        binding.tvSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonForgotPassword.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_forgotPassawordFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) from a previous session and update UI.
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    // Function to start the Google Sign-In flow
    private fun signInWithGoogle() {
        Log.d(TAG, "Starting Google Sign-In flow.")
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
    }

    // Handles the result from the Google Sign-In activity
    @Deprecated("Deprecated in Java") // This annotation indicates it's deprecated in Java, but still widely used for ActivityResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, get the account and authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "Google sign in successful: id=${account.id}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI and show a message
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Google Sign In Failed: ${e.statusCode} - ${e.message}", Toast.LENGTH_LONG).show()
                updateUI(null)
            }
        }
    }

    // Authenticates with Firebase using the Google ID token
    private fun firebaseAuthWithGoogle(idToken: String) {
        Log.d(TAG, "firebaseAuthWithGoogle: idToken=$idToken")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "Firebase signInWithCredential success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)

                    Toast.makeText(requireContext(), "Google Login Successful!", Toast.LENGTH_SHORT).show()
                    // Navigate to MainActivity after successful login
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish() // Finish the activity hosting this fragment
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "Firebase signInWithCredential failed", task.exception)
                    Toast.makeText(requireContext(), "Authentication Failed via Google: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    // Updates the UI based on the authentication state
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in. You might want to update a status TextView.
            binding.statusTextView.text = "Signed in as: ${user.displayName ?: user.email}"
            binding.googleSignInButton.visibility = View.GONE // Hide the button if already signed in
            binding.buttonLogin.visibility = View.GONE // Hide traditional login if signed in via Google
            binding.edtEmailAddress.visibility = View.GONE
            binding.textInputPassword.visibility = View.GONE
            binding.tvLogin.visibility = View.GONE
            binding.tvOrSeparator.visibility = View.GONE
            binding.dividerLeft.visibility = View.GONE
            binding.dividerRight.visibility = View.GONE
            binding.tvNoAccount.visibility = View.GONE
            binding.tvSignUp.visibility = View.GONE
            binding.buttonForgotPassword.visibility = View.GONE
            binding.divider.visibility = View.GONE
            binding.divider2.visibility = View.GONE

            // You can also add navigation here if the user is auto-logged in on start
            if (findNavController().currentDestination?.id == R.id.loginFragment) {
                // Check if it's not already on MainActivity
                // This prevents navigating back to login fragment after main activity is loaded
                if (!requireActivity().isFinishing) {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }


        } else {
            // User is signed out or not signed in. Show login options.
            binding.statusTextView.text = "Not signed in via Google"
            binding.googleSignInButton.visibility = View.VISIBLE
            binding.buttonLogin.visibility = View.VISIBLE
            binding.edtEmailAddress.visibility = View.VISIBLE
            binding.textInputPassword.visibility = View.VISIBLE
            binding.tvLogin.visibility = View.VISIBLE
            binding.tvOrSeparator.visibility = View.VISIBLE
            binding.dividerLeft.visibility = View.VISIBLE
            binding.dividerRight.visibility = View.VISIBLE
            binding.tvNoAccount.visibility = View.VISIBLE
            binding.tvSignUp.visibility = View.VISIBLE
            binding.buttonForgotPassword.visibility = View.VISIBLE
            binding.divider.visibility = View.VISIBLE
            binding.divider2.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}