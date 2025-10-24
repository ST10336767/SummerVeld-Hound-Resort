package com.summerveldhoundresort.app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.summerveldhoundresort.app.ManageDogsActivity
import com.summerveldhoundresort.app.ManageEventsActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.ActivityAdminBinding
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
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_admin_home, container, false)
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)


        // Log current Firebase user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("AdminActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("AdminActivity", "No user is currently logged in.")
        }

        // Navigate to Manage Events
        binding.MngEventsBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ManageEventsActivity::class.java))
        }

        binding.MngDogsBtn.setOnClickListener {
            val intent = Intent(requireContext(), ManageDogsActivity::class.java)
            startActivity(intent)
        }

        return binding.root

    }


}