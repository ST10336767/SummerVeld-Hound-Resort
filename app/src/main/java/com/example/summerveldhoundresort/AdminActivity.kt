package com.example.summerveldhoundresort

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.summerveldhoundresort.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Log current Firebase user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("AdminActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("AdminActivity", "No user is currently logged in.")
        }

        // Navigate to Manage Events
        binding.MngEventsBtn.setOnClickListener {
            startActivity(Intent(this, ManageEventsActivity::class.java))
        }
    }
}
