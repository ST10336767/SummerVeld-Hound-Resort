package com.example.summerveldhoundresort

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.summerveldhoundresort.databinding.ActivityAdminBinding
import com.example.summerveldhoundresort.ui.admin.CreateDog
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

        // Navigate to Manage Events
        binding.MngEventsBtn.setOnClickListener {
            startActivity(Intent(this, ManageEventsActivity::class.java))
        }

        binding.MngDogsBtn.setOnClickListener {
            val intent = Intent(this, DogActivity::class.java)
            startActivity(intent)
            finish() // optional, if you don’t want to come back
        }




    }
}
