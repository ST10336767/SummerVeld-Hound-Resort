package com.example.summerveldhoundresort.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.MainActivity
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentLoginBinding
import com.example.summerveldhoundresort.databinding.FragmentRegisterBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    //Followed viewbinding documentation for fragments for recommended approach
    // Site: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val fbUserImpl = firebaseUsersImpl()
        val factory = AuthViewModelFactory{AuthViewModel(fbUserImpl)}
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        val passwordInputLayout = binding.textInputPassword
        val confirmPasswordInputLayout = binding.textInputPasswordConfirm
        val passwordToggleIcon = ContextCompat.getDrawable(requireContext(), R.drawable.password_toggle_icons)


        //doOnTextChanged method found on stack overflow from answer submitted by Francis
        //Site: https://stackoverflow.com/questions/20824634/android-on-text-change-listener
        //Once password field is written to, remove error and change icon back
        passwordInputLayout.editText?.doOnTextChanged { text, start, before, count, ->
            passwordInputLayout.error = null
            passwordInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            passwordInputLayout.endIconDrawable = passwordToggleIcon
        }

        confirmPasswordInputLayout.editText?.doOnTextChanged { text, start, before, count, ->
            confirmPasswordInputLayout.error = null
            confirmPasswordInputLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
            confirmPasswordInputLayout.endIconDrawable = passwordToggleIcon
        }


        binding.buttonRegister.setOnClickListener{
            val name = binding.edtName.text.toString()
            val email  = binding.edtEmailAddress.text.toString()
            val password  = binding.textInputPassword.editText?.text.toString()
            val confirmPassword  = binding.textInputPasswordConfirm.editText?.text.toString()
            val phoneNumber  = binding.edtPhone.text.toString()
            val creationDate = System.currentTimeMillis().toString()

            // Clear previous errors
            clearAllErrors()
            
            // Comprehensive validation checks
            var isValid = true
            val validationErrors = mutableListOf<String>()
            
            // Name validation
            if (name.trim().isEmpty()) {
                showFieldError(binding.edtName, "Name is required")
                validationErrors.add("• Name is required")
                isValid = false
            } else if (name.trim().length < 2) {
                showFieldError(binding.edtName, "Name must be at least 2 characters")
                validationErrors.add("• Name must be at least 2 characters")
                isValid = false
            } else if (name.trim().length > 50) {
                showFieldError(binding.edtName, "Name must be less than 50 characters")
                validationErrors.add("• Name must be less than 50 characters")
                isValid = false
            }
            
            // Email validation
            if (email.trim().isEmpty()) {
                showFieldError(binding.edtEmailAddress, "Email is required")
                validationErrors.add("• Email is required")
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                showFieldError(binding.edtEmailAddress, "Please enter a valid email address")
                validationErrors.add("• Please enter a valid email address (e.g., user@example.com)")
                isValid = false
            } else if (email.trim().length > 100) {
                showFieldError(binding.edtEmailAddress, "Email must be less than 100 characters")
                validationErrors.add("• Email must be less than 100 characters")
                isValid = false
            }
            
            // Phone number validation
            if (phoneNumber.trim().isEmpty()) {
                showFieldError(binding.edtPhone, "Phone number is required")
                validationErrors.add("• Phone number is required")
                isValid = false
            } else if (!isValidPhoneNumber(phoneNumber.trim())) {
                showFieldError(binding.edtPhone, "Please enter a valid phone number")
                validationErrors.add("• Please enter a valid phone number (e.g., +1234567890)")
                isValid = false
            }
            
            // Password validation
            if (password.isEmpty()) {
                showPasswordError(passwordInputLayout, "Password is required")
                validationErrors.add("• Password is required")
                isValid = false
            } else {
                val passwordMessage = validatePassword(password)
                if (passwordMessage.isNotEmpty()) {
                    showPasswordError(passwordInputLayout, passwordMessage)
                    validationErrors.add("• $passwordMessage")
                    isValid = false
                }
            }
            
            // Confirm password validation
            if (confirmPassword.isEmpty()) {
                showPasswordError(confirmPasswordInputLayout, "Please confirm your password")
                validationErrors.add("• Please confirm your password")
                isValid = false
            } else if (password.isNotEmpty() && password != confirmPassword) {
                showPasswordError(confirmPasswordInputLayout, "Passwords do not match")
                validationErrors.add("• Passwords do not match")
                isValid = false
            }
            
            // Show validation summary if there are errors
            if (!isValid) {
                showValidationSummary(validationErrors)
                return@setOnClickListener
            }
            registerUser(name, email, password, confirmPassword, phoneNumber, creationDate)

        }

        binding.tvLoginHere.setOnClickListener{
            //Code from StackOverflow :https://stackoverflow.com/questions/70148577/how-to-go-from-one-fragment-to-another-fragment-in-kotlin
            //Author: Navin Kumar
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }

    private fun registerUser(name: String, email: String, password: String, confirmPassword: String, phoneNumber: String, creationDate: String) {
        // Show loading state
        showLoadingState(true)
        
        lifecycleScope.launch {
            try {
                val result = authViewModel.register(
                    username = name.trim(),
                    email = email.trim(),
                    password = password,
                    phoneNumber = phoneNumber.trim(),
                    creationDate = creationDate
                )
                
                Log.d(TAG, "Registration details: $name, $email")
                
                when(result) {
                    is AppResult.Success -> {
                        Log.d(TAG, "SUCCESS result: ${result}")
                        handleRegistrationSuccess()
                    }
                    is AppResult.Error -> {
                        Log.d(TAG, "Registration Failed: ${result.exception.message}")
                        handleRegistrationError(result.exception)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error during registration", e)
                handleRegistrationError(e)
            } finally {
                // Hide loading state
                showLoadingState(false)
            }
        }
    }
    
    private fun showLoadingState(isLoading: Boolean) {
        binding.buttonRegister.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        
        if (isLoading) {
            binding.buttonRegister.text = "Creating Account..."
        } else {
            binding.buttonRegister.text = "Create Account"
        }
    }
    
    private fun handleRegistrationSuccess() {
        Toast.makeText(requireContext(), "🎉 Registration successful! Welcome!", Toast.LENGTH_LONG).show()
        
        // Navigate to main activity after a short delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }, 1500)
    }



    private fun validatePassword(password: String): String {
        if (password.length < 8) return "Password must have minimum 8 characters"
        if (password.firstOrNull { it.isDigit() } == null) return "Password must have a digit"
        if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return "Password must have an uppercase character"
        if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return "Password must have a lowercase character"
        if (password.firstOrNull { !it.isLetterOrDigit() } == null) return "Password must have a special character"

        return ""
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        // Remove all non-digit characters for validation
        val digitsOnly = phone.replace(Regex("[^\\d]"), "")
        // Check if it's between 7-15 digits (international standard)
        return digitsOnly.length in 7..15
    }
    
    private fun clearAllErrors() {
        binding.edtName.error = null
        binding.edtEmailAddress.error = null
        binding.edtPhone.error = null
        binding.textInputPassword.error = null
        binding.textInputPasswordConfirm.error = null
        
        // Reset password toggle icons
        val passwordToggleIcon = ContextCompat.getDrawable(requireContext(), R.drawable.password_toggle_icons)
        binding.textInputPassword.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        binding.textInputPassword.endIconDrawable = passwordToggleIcon
        binding.textInputPasswordConfirm.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        binding.textInputPasswordConfirm.endIconDrawable = passwordToggleIcon
    }
    
    private fun showFieldError(editText: android.widget.EditText, message: String) {
        editText.error = message
        editText.requestFocus()
    }
    
    private fun showPasswordError(inputLayout: TextInputLayout, message: String) {
        inputLayout.error = message
        inputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        inputLayout.endIconDrawable = null
        inputLayout.editText?.requestFocus()
    }
    
    private fun showValidationSummary(errors: List<String>) {
        // Show first few errors to avoid cut-off
        val displayErrors = if (errors.size > 3) {
            errors.take(3) + "• And ${errors.size - 3} more issues..."
        } else {
            errors
        }
        val errorMessage = "Please fix these issues:\n${displayErrors.joinToString("\n")}"
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
    }
    
    private fun handleRegistrationError(exception: Exception) {
        val errorMessage = when {
            exception.message?.contains("email address is already in use", ignoreCase = true) == true -> {
                "Email already registered. Use a different email or try logging in."
            }
            exception.message?.contains("invalid email", ignoreCase = true) == true -> {
                "Please enter a valid email address."
            }
            exception.message?.contains("password", ignoreCase = true) == true -> {
                "Password requirements not met. Please check and try again."
            }
            exception.message?.contains("network", ignoreCase = true) == true -> {
                "Network error. Check your internet connection and try again."
            }
            exception.message?.contains("timeout", ignoreCase = true) == true -> {
                "Request timed out. Check your internet connection and try again."
            }
            else -> {
                "Registration failed. Please try again or contact support."
            }
        }
        
        // Show concise error message
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        
        // Log the actual exception for debugging
        Log.e(TAG, "Registration error: ${exception.message}", exception)
    }

    //Part of viewbinding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}