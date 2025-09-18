package com.example.summerveldhoundresort.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.summerveldhoundresort.R

class EventsFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var emptyView: TextView

    private val adapter by lazy {
        EventsAdapter(
            onRsvp = { /* TODO: show toast/dialoq or navigate */ },
            onSend = { event, text -> addComment(event.id, text) }
        )
    }

    // demo in-memory data (no backend)
    private var events = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_events, container, false)
        recycler = v.findViewById(R.id.rvEvents)
        emptyView = v.findViewById(R.id.emptyView)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)
        recycler.adapter = adapter

        // demo data — replace later
        events = mutableListOf(
            Event(
                id = "1",
                title = "(Event Name)",
                name = "Puppy Playday",
                date = "2025-10-02",
                time = "10:00",
                description = "Bring your pup for social fun!",
                comments = listOf("Bella: Can’t wait for this!", "Max: Is there a fee?")
            ),
            Event(
                id = "2",
                title = "(Event Name)",
                name = "Grooming 101",
                date = "2025-10-05",
                time = "14:00",
                description = "Learn basic grooming at home."
            )
        )
        pushList()
        return v
    }

    private fun pushList() {
        adapter.submitList(events.toList()) // pass a copy for DiffUtil
        emptyView.visibility = if (events.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun addComment(eventId: String, text: String) {
        val i = events.indexOfFirst { it.id == eventId }
        if (i != -1) {
            val e = events[i]
            events[i] = e.copy(comments = e.comments + text)
            pushList()
        }
    }
}
