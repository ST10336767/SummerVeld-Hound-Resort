package com.summerveldhoundresort.app.ui.events

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Event
import java.text.SimpleDateFormat
import java.util.*

class AdminEventAdapter(
    private val events: List<Event>,
    private val onEventClick: (Event) -> Unit
) : RecyclerView.Adapter<AdminEventAdapter.EventViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val listeners: MutableMap<String, ListenerRegistration> = mutableMapOf()

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val locationTextView: TextView = itemView.findViewById(R.id.locationTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val editButton: Button = itemView.findViewById(R.id.btnEdit)
        val rsvpCountTextView: TextView? = itemView.findViewById(R.id.tvAttendees)
        val viewCommentsButton: Button? = itemView.findViewById(R.id.btnViewComments)
    }

    // ---- date helpers (DATE + TIME) ----
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
        isLenient = false
    }
    private fun parseDateTime(date: String?, time: String?): Date? = try {
        val d = date?.trim().orEmpty()
        val t = time?.trim().orEmpty()
        when {
            d.isEmpty() -> null
            t.isEmpty() -> dateTimeFormat.parse("$d 23:59")
            else -> dateTimeFormat.parse("$d $t")
        }
    } catch (_: Exception) { null }

    private val sortedEvents: List<Event>
        get() {
            val now = Date()
            val (known, unknown) = events.partition { parseDateTime(it.date, it.time) != null }
            val (past, upcoming) = known.partition { parseDateTime(it.date, it.time)!!.before(now) }
            val upcomingSorted = upcoming.sortedBy { parseDateTime(it.date, it.time) }
            val pastSorted = past.sortedByDescending { parseDateTime(it.date, it.time) }
            return upcomingSorted + pastSorted + unknown
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = sortedEvents[position]

        holder.titleTextView.text = event.name
        holder.dateTextView.text = event.date
        holder.timeTextView.text = event.time
        holder.locationTextView.text = event.location
        holder.descriptionTextView.text = event.description

        holder.editButton.visibility = View.VISIBLE
        holder.editButton.setOnClickListener { onEventClick(event) }

        // RSVP Count
        holder.rsvpCountTextView?.text = "0 members going"
        val eventId = event.id
        if (eventId.isNotEmpty()) {
            listeners[eventId]?.remove()
            val reg = firestore.collection("events")
                .document(eventId)
                .collection("rsvps")
                .addSnapshotListener { snapshot, _ ->
                    val count = snapshot?.size() ?: 0
                    holder.rsvpCountTextView?.text = "$count members going"
                }
            listeners[eventId] = reg
        }

        // View Comments
        holder.viewCommentsButton?.setOnClickListener {
            val ctx = holder.itemView.context
            val i = Intent(ctx, EventCommentsActivity::class.java)
            i.putExtra("EVENT_ID", event.id)
            i.putExtra("EVENT_NAME", event.name)
            ctx.startActivity(i)
        }
    }

    override fun onViewRecycled(holder: EventViewHolder) {
        super.onViewRecycled(holder)
        holder.rsvpCountTextView?.text = "0 going"
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        listeners.values.forEach { it.remove() }
        listeners.clear()
    }

    override fun getItemCount() = sortedEvents.size
}
