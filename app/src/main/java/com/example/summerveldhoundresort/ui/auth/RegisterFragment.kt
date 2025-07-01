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

            //Validation checks
            var isValid = true
            if(name.isEmpty()){
                binding.edtName.error = "Please fill in name"
                isValid = false
            }
            if(email.isEmpty()){
                binding.edtEmailAddress.error = "Please fill in email"
                isValid = false
            }

            //Code for icon switching upon error  adapted from ChatGPT: https://chatgpt.com/share/6814e20c-d918-800a-86d7-31dd9d5594bf
            if(password.isEmpty()){
                //Setting error seems to get error icon anyways
                binding.textInputPassword.editText?.error= "Please fill in password"
                //Replace password icon --> error icon -- removes toggle icon instead.  error icon seems to come by default
                passwordInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                passwordInputLayout.endIconDrawable = null

                isValid = false
            }
            if(confirmPassword.isEmpty()){
                binding.textInputPasswordConfirm.editText?.error= "Please fill in confirm  password"
                isValid = false

                confirmPasswordInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                confirmPasswordInputLayout.endIconDrawable = null
            }
            if(phoneNumber.isEmpty()){
                binding.edtPhone.error= "Please fill in phone number"
                isValid = false
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailAddress.error= "Please enter a valid email address: user@example.com"
                isValid = false
            }
            if (password.isNotEmpty() && confirmPassword.isNotEmpty()){
                if (password != confirmPassword){
                    binding.textInputPasswordConfirm.editText?.error= "Please confirm password does not match password"
                    confirmPasswordInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    confirmPasswordInputLayout.endIconDrawable = null

                    isValid = false
                }
            }


            val passwordMessage = validatePassword(password)
            if(passwordMessage.isNotEmpty() ){
                binding.textInputPassword.editText?.error = passwordMessage
                passwordInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
                passwordInputLayout.endIconDrawable = null
                isValid = false
            }
            if (!isValid){
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

        lifecycleScope.launch {
            val result = authViewModel.register(
                username = name,
                email = email,
                password = password,
                phoneNumber = phoneNumber,
                creationDate=creationDate
            )
            Log.d(TAG, "Registration details:$name, $email ")
            when(result){
                is AppResult.Success -> {
                    Log.d(TAG, "SUCCESS result: ${result}")


                    Toast.makeText(requireContext(), "Registration Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()


//                    Handler(Looper.getMainLooper()).postDelayed({
//                        val intent = Intent(requireContext(), MainActivity::class.java)
//                        startActivity(intent)
//                        requireActivity().finish()
//
//                    }, 1000)

//                    findNavController().navigate(R.id.action_registerFragment_to_navigation_home)
                }
                is AppResult.Error<*> -> {
                    Log.d(TAG, "Registration Failed: ${result.exception.message}")
                }
            }
        }
    }



    private fun validatePassword(password: String): String {
        if (password.length < 8) return "Password must have minimum 8 characters"
        if (password.firstOrNull { it.isDigit() } == null) return "Password must have a digit"
        if (password.filter { it.isLetter() }.firstOrNull { it.isUpperCase() } == null) return "Password must have an uppercase character"
        if (password.filter { it.isLetter() }.firstOrNull { it.isLowerCase() } == null) return "Password must have a lowercase character"
        if (password.firstOrNull { !it.isLetterOrDigit() } == null) return "Password must have a special character character"

        return ""
    }

    //Part of viewbinding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}