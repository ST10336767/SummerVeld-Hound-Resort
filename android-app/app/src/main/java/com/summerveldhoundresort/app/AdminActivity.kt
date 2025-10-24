package com.summerveldhoundresort.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth

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
        }

        // Navigate to Manage Events
        binding.MngEventsBtn.setOnClickListener {
            startActivity(Intent(this, ManageEventsActivity::class.java))
        }
    }
}