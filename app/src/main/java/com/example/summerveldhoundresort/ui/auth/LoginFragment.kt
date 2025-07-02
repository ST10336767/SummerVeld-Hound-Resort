package com.example.summerveldhoundresort.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.MainActivity
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentLoginBinding
import com.example.summerveldhoundresort.db.AppResult
import com.example.summerveldhoundresort.db.implementations.firebaseUsersImpl
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    //Followed viewbinding documentation for fragments for recommended approach
    // Site: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val fbUserImpl = firebaseUsersImpl()
        val factory = AuthViewModelFactory{AuthViewModel(fbUserImpl)}
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.buttonLogin.setOnClickListener{
            val emailAddress = binding.edtEmailAddress.text.toString()
            val password = binding.textInputPassword.editText?.text.toString()
            lifecycleScope.launch {
                val result = authViewModel.login(
                    userLoginField = emailAddress,
                    password = password
                )
                Log.d(TAG, "Login details:$emailAddress, password: $password ")
                when(result){
                    is AppResult.Success -> {
                        Log.d(TAG, "SUCCESS result: ${result}")
                        Toast.makeText(
                            requireContext(),
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is AppResult.Error -> {
                        Log.d(TAG, "Login Failed: ${result.exception.message}")
                    }
                }
            }
        }




        binding.tvSignUp.setOnClickListener{
            //Code from StackOverflow :https://stackoverflow.com/questions/70148577/how-to-go-from-one-fragment-to-another-fragment-in-kotlin
            //Author: Navin Kumar
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    //Part of viewbinding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}