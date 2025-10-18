package com.example.summerveldhoundresort.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.summerveldhoundresort.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_THEME = "theme_mode"
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
        private const val THEME_SYSTEM = "system"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        setupThemeToggle()
        
        return binding.root
    }

    private fun setupThemeToggle() {
        // Get current theme mode
        val currentTheme = sharedPreferences.getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
        
        // Set initial switch state based on current theme
        binding.switchDarkMode.isChecked = currentTheme == THEME_DARK
        
        // Set up switch listener
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val themeMode = if (isChecked) THEME_DARK else THEME_LIGHT
            saveThemePreference(themeMode)
            applyTheme(themeMode)
        }
    }

    private fun saveThemePreference(themeMode: String) {
        sharedPreferences.edit()
            .putString(KEY_THEME, themeMode)
            .apply()
    }

    private fun applyTheme(themeMode: String) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
