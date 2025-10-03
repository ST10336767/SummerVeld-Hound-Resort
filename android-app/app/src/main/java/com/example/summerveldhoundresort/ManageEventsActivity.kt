package com.example.summerveldhoundresort

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summerveldhoundresort.databinding.ActivityManageEventsBinding
import com.example.summerveldhoundresort.ui.events.AdminEventAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.summerveldhoundresort.db.entities.Event

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

        // Back button click listener
        binding.btnBack.setOnClickListener {
            finish() // simply finishes this activity and goes back
        }


        loadEvents()
    }

    private fun loadEvents() {
        // Set the layout manager first
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this)

        db.collection("events").get()
            .addOnSuccessListener { snapshot ->
                // Convert Firestore documents into Event objects
                val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }

                if (events.isNotEmpty()) {
                    // Set up RecyclerView adapter
                    val adapter = AdminEventAdapter(events)
                    binding.recyclerViewEvents.adapter = adapter
                } else {
                    Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
