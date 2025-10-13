package com.example.summerveldhoundresort.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.databinding.FragmentHomeBinding
import com.example.summerveldhoundresort.db.entities.Event
import com.example.summerveldhoundresort.ui.events.UserEventAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: UserEventAdapter
    private val events = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        binding.recyclerViewEvents.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserEventAdapter(events)
        binding.recyclerViewEvents.adapter = adapter

        loadUpcomingEvents()

        binding.btnViewAllEvents.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_eventsFragment)
        }

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_savedDogsFragment)
        }

    }

    private fun loadUpcomingEvents() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        db.collection("events")
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                events.clear()

                val upcomingEvents = snapshot?.documents
                    ?.mapNotNull { doc ->
                        val event = doc.toObject(Event::class.java)
                        event?.id = doc.id // critical for RSVP
                        event
                    }
                    ?.filter { it.date >= today }
                    ?.take(3) // only the 3 closest events
                    ?: emptyList()

                events.addAll(upcomingEvents)
                adapter.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
