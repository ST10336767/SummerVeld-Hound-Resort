package com.summerveldhoundresort.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.summerveldhoundresort.app.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.summerveldhoundresort.app.ui.admin.AdminHomeFragment
import com.summerveldhoundresort.app.ui.admin.ManageDogsFragment
import com.summerveldhoundresort.app.ui.profile.ProfileViewFragment

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //added for admin navbar
        val navView: BottomNavigationView = binding.adminNavView
// wont work, because it only works with fragments, and activities were made unnecessarily
        val navController = findNavController(R.id.nav_host_fragment_activity_admin)
//         Passing each menu ID as a set of Ids because each
//         menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.adminHomeFragment, R.id.manageDogsFragment, R.id.manageEventsFragment, R.id.profileViewFragment
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)




//        supportFragmentManager.commit {
//            replace(R.id.admin_fragment_container, AdminHomeFragment())
//        }
//
//        navView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.nav_admin_home -> {
//                    supportFragmentManager.commit {
//                        replace(R.id.admin_fragment_container, AdminHomeFragment())
//                    }
//                    true
//                }
//
//                R.id.nav_admin_dogs -> {
//                    supportFragmentManager.commit {
//                        replace(R.id.admin_fragment_container, ManageDogsFragment())
//                    }
//                    true
//                }
//
//                R.id.nav_admin_events -> {
//                    startActivity(Intent(this, ManageEventsActivity::class.java))
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                    true
//                }
//
//                R.id.nav_admin_profile -> {
//                    supportFragmentManager.commit {
//                        replace(R.id.admin_fragment_container, ProfileViewFragment())
//                    }
//                    true
//                }
//
//                else -> false
//            }
//        }

//        // Log current Firebase user
//        val user = FirebaseAuth.getInstance().currentUser
//        if (user != null) {
//            Log.d("AdminActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
//        } else {
//            Log.d("AdminActivity", "No user is currently logged in.")
//        }
//
//        // Navigate to Manage Events
//        binding.MngEventsBtn.setOnClickListener {
//            startActivity(Intent(this, ManageEventsActivity::class.java))
//        }
//
//        binding.MngDogsBtn.setOnClickListener {
//            val intent = Intent(this, ManageDogsActivity::class.java)
//            startActivity(intent)
//        }





    }
}
