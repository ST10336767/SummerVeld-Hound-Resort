package com.summerveldhoundresort.app.ui.profile

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentProfileViewBinding
import com.summerveldhoundresort.app.databinding.FragmentRegisterBinding
import com.summerveldhoundresort.app.db.implementations.firebaseUsersImpl
import com.summerveldhoundresort.app.ui.auth.AuthActivity
import com.summerveldhoundresort.app.ui.auth.AuthViewModel
import com.summerveldhoundresort.app.ui.auth.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.summerveldhoundresort.app.utils.ThemeManager

class ProfileViewFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel

    //Followed viewbinding documentation for fragments for recommended approach
    // Site: https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: FragmentProfileViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved theme on fragment creation
        val savedTheme = ThemeManager.getThemeMode(requireContext())
        ThemeManager.applyTheme(savedTheme)
    }

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

        //adeed
        val navController = findNavController()
        val graphId = navController.graph.id
        val isAdminGraph = graphId == R.id.admin_nav_graph


        binding.mbEditProfile.setOnClickListener{
//            findNavController().navigate(R.id.action_profileViewFragment_to_editProfileFragment)
            if (isAdminGraph) {
                navController.navigate(R.id.action_profileViewFragment_to_editProfileFragment_admin)
            } else {
                navController.navigate(R.id.action_profileViewFragment_to_editProfileFragment)
            }
        }
        binding.mbDataPrivacy.setOnClickListener {
//            findNavController().navigate(R.id.action_global_privacyFragment)
            if (isAdminGraph) {
                navController.navigate(R.id.action_global_privacyFragment_admin)
            } else {
                navController.navigate(R.id.action_global_privacyFragment)
            }
        }
        binding.mbChangePassword.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val userEmail = auth.currentUser?.email

            if (userEmail != null) {
                auth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Password reset email sent!", Toast.LENGTH_SHORT).show()
                            // Navigate to change password fragment using global action
//                            findNavController().navigate(R.id.action_global_changePasswordFragment)
                            if (isAdminGraph) {
                                navController.navigate(R.id.action_global_changePasswordFragment_admin)
                            } else {
                                navController.navigate(R.id.action_global_changePasswordFragment)
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "No email associated with this account", Toast.LENGTH_SHORT).show()
            }
        }
        binding.mbSettings.setOnClickListener{
//            findNavController().navigate(R.id.action_ProfileFragment_to_EditProfileFragment)
        }
        binding.mbAccountDeletion.setOnClickListener {
//            findNavController().navigate(R.id.action_profileViewFragment_to_accountDeletionFragment)
            if (isAdminGraph) {
                navController.navigate(R.id.action_profileViewFragment_to_accountDeletionFragment_admin)
            } else {
                navController.navigate(R.id.action_profileViewFragment_to_accountDeletionFragment)
            }
        }
        binding.mbLogout.setOnClickListener{
            // ogMik - this does acc logout the user, it just never redirected them - my bad
            profileViewModel.logout()
            //added -- for redirect
            //isAdded -- > checks if this fragment is still connected to its activity - > prevents crashes
                if(isAdded){
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }


        }

        binding.mbChangeTheme.setOnClickListener {
            showThemeDialog()
        }

        return binding.root
    }

    private fun showThemeDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_dark_mode_settings, null)

        val lightOption = dialogView.findViewById<View>(R.id.lightThemeOption)
        val darkOption = dialogView.findViewById<View>(R.id.darkThemeOption)
        val systemOption = dialogView.findViewById<View>(R.id.systemThemeOption)

        val ivLightSelected = dialogView.findViewById<View>(R.id.ivLightSelected)
        val ivDarkSelected = dialogView.findViewById<View>(R.id.ivDarkSelected)
        val ivSystemSelected = dialogView.findViewById<View>(R.id.ivSystemSelected)

        // Show current selection
        val currentTheme = ThemeManager.getThemeMode(requireContext())
        when (currentTheme) {
            ThemeManager.THEME_LIGHT -> ivLightSelected.visibility = View.VISIBLE
            ThemeManager.THEME_DARK -> ivDarkSelected.visibility = View.VISIBLE
            ThemeManager.THEME_SYSTEM -> ivSystemSelected.visibility = View.VISIBLE
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Theme Settings")
            .setPositiveButton("Apply") { _, _ -> }
            .setNegativeButton("Cancel", null)
            .create()

        lightOption.setOnClickListener {
            ThemeManager.setThemeMode(requireContext(), ThemeManager.THEME_LIGHT)
            ivLightSelected.visibility = View.VISIBLE
            ivDarkSelected.visibility = View.GONE
            ivSystemSelected.visibility = View.GONE
            Toast.makeText(requireContext(), "Light theme applied", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        darkOption.setOnClickListener {
            ThemeManager.setThemeMode(requireContext(), ThemeManager.THEME_DARK)
            ivLightSelected.visibility = View.GONE
            ivDarkSelected.visibility = View.VISIBLE
            ivSystemSelected.visibility = View.GONE
            Toast.makeText(requireContext(), "Dark theme applied", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        systemOption.setOnClickListener {
            ThemeManager.setThemeMode(requireContext(), ThemeManager.THEME_SYSTEM)
            ivLightSelected.visibility = View.GONE
            ivDarkSelected.visibility = View.GONE
            ivSystemSelected.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "System theme applied", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    //Part of viewbinding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}