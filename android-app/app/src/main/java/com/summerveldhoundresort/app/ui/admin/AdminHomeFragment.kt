package com.summerveldhoundresort.app.ui.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.summerveldhoundresort.app.AdminActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentAdminHomeBinding


class AdminHomeFragment : Fragment() {

//    Made so easier for navbar traversal --> overlay issue where adminactivity displays over actual fragments
    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)

        // Log current Firebase user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("AdminActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("AdminActivity", "No user is currently logged in.")
        }

        // ogMik -> rewrote button actions because there was
        // an issue with naving back to home page if using these two buttons
        binding.MngDogsBtn.setOnClickListener {
            (activity as? AdminActivity)?.getBottomNavView()?.selectedItemId = R.id.manageDogsFragment
        }
        binding.MngEventsBtn.setOnClickListener {
            (activity as? AdminActivity)?.getBottomNavView()?.selectedItemId = R.id.manageEventsFragment
        }


        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}