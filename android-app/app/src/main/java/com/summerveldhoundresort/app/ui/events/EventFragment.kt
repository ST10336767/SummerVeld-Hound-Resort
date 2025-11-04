package com.summerveldhoundresort.app.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.summerveldhoundresort.app.databinding.FragmentEventsBinding
import com.summerveldhoundresort.app.db.entities.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class EventsFragment : Fragment() {

    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserEventAdapter
    private val events = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewAllEvents.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserEventAdapter(events)
        binding.recyclerViewAllEvents.adapter = adapter
        
        // Enable padding to allow scrolling past bottom items
        binding.recyclerViewAllEvents.clipToPadding = false
        
        // Handle system navigation bars and ensure proper padding
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val minPaddingDp = 100
            val minPaddingPx = (minPaddingDp * resources.displayMetrics.density).toInt()
            val bottomPadding = maxOf(systemBars.bottom, imeInsets.bottom, minPaddingPx)
            val extraPaddingPx = (100 * resources.displayMetrics.density).toInt() // Extra 100dp for comment boxes
            
            // Add extra padding to RecyclerView for bottom content visibility
            binding.recyclerViewAllEvents.setPadding(
                binding.recyclerViewAllEvents.paddingLeft,
                binding.recyclerViewAllEvents.paddingTop,
                binding.recyclerViewAllEvents.paddingRight,
                bottomPadding + extraPaddingPx
            )
            
            insets
        }

        // Set up back button click listener
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }

        loadAllEvents()
    }

    private fun loadAllEvents() {
        db.collection("events")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                events.clear()

                val allEvents = snapshot?.documents
                    ?.mapNotNull { doc ->
                        val event = doc.toObject(Event::class.java)
                        event?.id = doc.id
                        event
                    } ?: emptyList()

                events.addAll(allEvents)
                adapter.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}