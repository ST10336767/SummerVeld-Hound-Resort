package com.summerveldhoundresort.app.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.summerveldhoundresort.app.AddEventActivity
import com.summerveldhoundresort.app.EditEventActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.FragmentManageEventsBinding
import com.summerveldhoundresort.app.db.entities.Event
import com.summerveldhoundresort.app.ui.events.AdminEventAdapter

class ManageEventsFragment : Fragment() {

    private var _binding: FragmentManageEventsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Log current user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("ManageEventsFragment", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("ManageEventsFragment", "No user is currently logged in.")
        }

        // Navigate to AddEventActivity
        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(requireContext(), AddEventActivity::class.java))
        }

        // Back button to go home/admin dashboard
//        binding.btnBack.setOnClickListener {
//            findNavController().navigate(R.id.adminHomeFragment)
//        }

        loadEvents()
    }

    private fun loadEvents() {
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())

        // Real-time listener
        db.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
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
                            val intent = Intent(requireContext(), EditEventActivity::class.java)
                            intent.putExtra("EVENT_ID", selectedEvent.id)
                            startActivity(intent)
                        }
                        binding.recyclerViewEvents.adapter = adapter
                    } else {
                        Toast.makeText(requireContext(), "No events found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}