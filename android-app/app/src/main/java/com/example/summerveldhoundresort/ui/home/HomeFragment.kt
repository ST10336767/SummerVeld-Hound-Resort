package com.example.summerveldhoundresort.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summerveldhoundresort.databinding.FragmentHomeBinding
import com.example.summerveldhoundresort.db.entities.Event
import com.example.summerveldhoundresort.ui.events.UserEventAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Welcome text
        binding.textViewWelcome.text = "Welcome, ${currentUser?.displayName ?: "User"}"

        // Horizontal scrolling RecyclerView for mini event window
        binding.recyclerViewEvents.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        loadEvents()
    }

    private fun loadEvents() {
        firestore.collection("events")
            .orderBy("date") // optional: sort by date
            .get()
            .addOnSuccessListener { snapshot ->
                val events = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)
                }

                if (events.isNotEmpty()) {
                    val adapter = UserEventAdapter(events)
                    binding.recyclerViewEvents.adapter = adapter
                }
            }
            .addOnFailureListener {
                // Handle errors
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
