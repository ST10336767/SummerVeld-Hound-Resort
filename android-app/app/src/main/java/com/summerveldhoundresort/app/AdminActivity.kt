package com.summerveldhoundresort.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Log current Firebase user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("AdminActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("AdminActivity", "No user is currently logged in.")
        }

        // Navigate to Manage Dogs
        binding.MngDogsBtn.setOnClickListener {
            val fragment = com.summerveldhoundresort.app.ui.Dog.ManageDogsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
            // Show the fragment container (do not hide the root layout)
            binding.adminFragmentContainer.visibility = View.VISIBLE
        }

        // Navigate to Manage Events (open fragment in same container)
        binding.MngEventsBtn.setOnClickListener {
            val fragment = com.summerveldhoundresort.app.ui.admin.ManageEventsFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .addToBackStack(null)
                .commit()
            binding.adminFragmentContainer.visibility = View.VISIBLE
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.admin_nav_view)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.admin_dashboard -> {
                    // Return to dashboard
                    binding.adminFragmentContainer.visibility = View.GONE
                    supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    true
                }
                R.id.admin_manage_dogs -> {
                    val fragment = com.summerveldhoundresort.app.ui.Dog.ManageDogsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.admin_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                    binding.adminFragmentContainer.visibility = View.VISIBLE
                    true
                }
                R.id.admin_manage_events -> {
                    val fragment = com.summerveldhoundresort.app.ui.admin.ManageEventsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.admin_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                    binding.adminFragmentContainer.visibility = View.VISIBLE
                    true
                }
                R.id.admin_logout -> {
                    // Navigate to admin profile fragment
                    val fragment = com.summerveldhoundresort.app.ui.admin.AdminProfileFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.admin_fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                    binding.adminFragmentContainer.visibility = View.VISIBLE
                    true
                }
                else -> false
            }
        }
    }

    fun hideFragmentContainer() {
        binding.adminFragmentContainer.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            // If there's a fragment in the back stack, pop it
            supportFragmentManager.popBackStack()
            // Hide the container after a short delay to ensure fragment is removed
            binding.adminFragmentContainer.post {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    binding.adminFragmentContainer.visibility = View.GONE
                }
            }
        } else {
            // If no fragments, proceed with normal back press
            super.onBackPressed()
        }
    }
}