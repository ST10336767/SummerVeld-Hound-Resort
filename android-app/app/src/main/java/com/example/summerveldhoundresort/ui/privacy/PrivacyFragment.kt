package com.example.summerveldhoundresort.ui.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
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
        // This would open the full privacy policy URL
        // For now, we'll create an intent to open the URL
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://your-website.com/privacy-policy"))
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}
