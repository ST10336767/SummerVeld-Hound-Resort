package com.example.summerveldhoundresort

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
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
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
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

        // 🔙 Back button to go home/admin dashboard
        binding.btnBack.setOnClickListener {
            finish() // closes this activity and returns to previous
        }

        loadEvents()



    }

    private fun loadEvents() {
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(this)

        // Real-time listener
        db.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val events = snapshot.documents.mapNotNull { doc ->
                        val event = doc.toObject(Event::class.java)
                        event?.id = doc.id  // save Firestore doc ID
                        event
                    }

                    if (events.isNotEmpty()) {
                        val adapter = AdminEventAdapter(events) { selectedEvent ->
                            val intent = Intent(this, EditEventActivity::class.java)
                            intent.putExtra("EVENT_ID", selectedEvent.id)
                            startActivity(intent)
                        }
                        binding.recyclerViewEvents.adapter = adapter
                    } else {
                        Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }


}
