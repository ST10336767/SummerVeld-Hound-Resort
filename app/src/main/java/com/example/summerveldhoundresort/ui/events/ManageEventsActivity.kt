package com.example.summerveldhoundresort

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.summerveldhoundresort.databinding.ActivityManageEventsBinding
import com.example.summerveldhoundresort.ui.events.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ManageEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageEventsBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("AdminActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("AdminActivity", "No user is currently logged in.")
        }

        // Navigate to AddEventActivity
        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        loadEvents()
    }

    private fun loadEvents() {
        db.collection("events").get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
                // TODO: show in RecyclerView/ListView
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
