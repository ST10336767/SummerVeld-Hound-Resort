package com.example.summerveldhoundresort.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.summerveldhoundresort.R
import com.example.summerveldhoundresort.db.entities.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserEventAdapter(private val events: List<Event>) :
    RecyclerView.Adapter<UserEventAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val rsvpButton: Button = itemView.findViewById(R.id.buttonRsvp)
        val memberCountText: TextView = itemView.findViewById(R.id.textViewMemberCount)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card_user, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        // Bind event data
        holder.titleTextView.text = event.name
        holder.dateTextView.text = event.date
        holder.timeTextView.text = event.time
        holder.locationTextView.text = event.location
        holder.descriptionTextView.text = event.description

        // Sanitize event name for Firestore document ID
        val eventDocId = event.name.replace("/", "_")

        // Live member count listener
        firestore.collection("events")
            .document(eventDocId)
            .collection("rsvps")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(holder.itemView.context, "Failed to load RSVPs.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                val count = snapshot?.size() ?: 0
                holder.memberCountText.text = "$count members going"
            }

        // RSVP toggle button
        holder.rsvpButton.setOnClickListener {
            currentUser?.let { user ->
                val rsvpRef = firestore.collection("events")
                    .document(eventDocId)
                    .collection("rsvps")
                    .document(user.uid)

                rsvpRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        rsvpRef.delete()
                    } else {
                        rsvpRef.set(mapOf(
                            "userId" to user.uid,
                            "timestamp" to System.currentTimeMillis()
                        ))
                    }
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "RSVP failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(holder.itemView.context, "Please sign in to RSVP.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = events.size
}
