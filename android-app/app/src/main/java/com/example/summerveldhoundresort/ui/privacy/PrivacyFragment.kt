package com.example.summerveldhoundresort.ui.privacy

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
    }
}
