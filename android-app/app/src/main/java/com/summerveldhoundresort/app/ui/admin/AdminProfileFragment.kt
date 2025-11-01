package com.summerveldhoundresort.app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentAdminProfileBinding
import com.summerveldhoundresort.app.ui.auth.AuthActivity
import com.summerveldhoundresort.app.utils.ThemeManager

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
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

        binding.mbChangeTheme.setOnClickListener {
            showThemeDialog()
        }

        // Logout button
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
