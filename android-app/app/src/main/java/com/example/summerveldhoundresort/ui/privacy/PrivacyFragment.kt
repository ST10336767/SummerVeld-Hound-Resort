package com.example.summerveldhoundresort.ui.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.R

class PrivacyFragment : Fragment(R.layout.fragment_privacy) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Set up back button click listener
        view.findViewById<View>(R.id.buttonBack)?.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Set up privacy policy link click listener
        view.findViewById<View>(R.id.linkPrivacy)?.setOnClickListener {
            openFullPrivacyPolicy()
        }
    }
    
    private fun openFullPrivacyPolicy() {
        try {
            // Open the full privacy policy hosted on GitHub Pages
            val privacyPolicyUrl = "https://ST10336767.github.io/Privacy-Policy-SummerveldHoundResort"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
            
            // Add flags to ensure the intent works properly
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        } catch (e: Exception) {
            // Silently fail if unable to open link
        }
    }
}
