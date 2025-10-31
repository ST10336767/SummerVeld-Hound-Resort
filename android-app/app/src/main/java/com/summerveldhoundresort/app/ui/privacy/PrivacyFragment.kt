package com.summerveldhoundresort.app.ui.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.summerveldhoundresort.app.R

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
            val uri = Uri.parse(privacyPolicyUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            
            // Check if there's an app that can handle this intent
            val packageManager = requireContext().packageManager
            val resolvedActivity = intent.resolveActivity(packageManager)
            
            if (resolvedActivity != null) {
                // Activity can be resolved, start it directly
                // Don't use FLAG_ACTIVITY_NEW_TASK when starting from a fragment
                startActivity(intent)
            } else {
                // Try with chooser as fallback
                val chooser = Intent.createChooser(intent, "Open Privacy Policy")
                val chooserResolved = chooser.resolveActivity(packageManager)
                if (chooserResolved != null) {
                    startActivity(chooser)
                } else {
                    Toast.makeText(requireContext(), "No browser app found. Please install a web browser.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No browser app found. Please install a web browser.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // Show error message if unable to open link
            Toast.makeText(requireContext(), "Unable to open privacy policy: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
