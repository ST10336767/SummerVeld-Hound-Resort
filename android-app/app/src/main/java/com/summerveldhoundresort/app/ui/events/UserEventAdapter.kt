package com.summerveldhoundresort.app.ui.events

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Comment
import com.summerveldhoundresort.app.db.entities.Event
import java.text.SimpleDateFormat
import java.util.*
import android.view.ViewGroup as AndroidViewGroup

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

        // Comments
        val commentInput: EditText? = itemView.findViewById(R.id.editTextComment)
        val sendCommentButton: Button? = itemView.findViewById(R.id.buttonSendComment)
        val commentsRecycler: RecyclerView? = itemView.findViewById(R.id.recyclerComments)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // ---- date helpers (DATE + TIME) ----
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
        isLenient = false
    }
    private fun parseDateTime(date: String?, time: String?): Date? = try {
        val d = date?.trim().orEmpty()
        val t = time?.trim().orEmpty()
        when {
            d.isEmpty() -> null
            t.isEmpty() -> dateTimeFormat.parse("$d 23:59") // no time -> end of day
            else -> dateTimeFormat.parse("$d $t")
        }
    } catch (_: Exception) { null }

    // dynamically sorted each bind so async updates show
    private val sortedEvents: List<Event>
        get() {
            val now = Date()
            val (known, unknown) = events.partition { parseDateTime(it.date, it.time) != null }
            val (past, upcoming) = known.partition { parseDateTime(it.date, it.time)!!.before(now) }
            val upcomingSorted = upcoming.sortedBy { parseDateTime(it.date, it.time) }          // soonest first
            val pastSorted = past.sortedByDescending { parseDateTime(it.date, it.time) }        // most recent past first
            return upcomingSorted + pastSorted + unknown
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card_user, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = sortedEvents[position]

        // Bind
        holder.titleTextView.text = event.name
        holder.dateTextView.text = event.date
        holder.timeTextView.text = event.time
        holder.locationTextView.text = event.location
        holder.descriptionTextView.text = event.description

        // Passed? (uses date + time)
        val eventMoment = parseDateTime(event.date, event.time)
        val now = Date()
        val isExpired = eventMoment?.before(now) == true

        holder.rsvpButton.isEnabled = !isExpired
        holder.rsvpButton.text = if (isExpired) "Event Passed" else "RSVP"

        Log.d("UserEventAdapter", "Event ID: '${event.id}', name: '${event.name}'")

        if (event.id.isNotEmpty()) {
            val rsvpCollection = firestore.collection("events")
                .document(event.id)
                .collection("rsvps")

            // RSVP count + state
            rsvpCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserEventAdapter", "Failed RSVPs: ${error.message}")
                    holder.memberCountText.text = "0 members going"
                    return@addSnapshotListener
                }
                val count = snapshot?.size() ?: 0
                holder.memberCountText.text = "$count members going"
                val userGoing = snapshot?.documents?.any { it.id == currentUser?.uid } ?: false
                if (!isExpired) holder.rsvpButton.text = if (userGoing) "Un-RSVP" else "RSVP"
            }

            // RSVP click
            holder.rsvpButton.setOnClickListener {
                if (currentUser == null) {
                    Toast.makeText(holder.itemView.context, "Sign in to RSVP", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val rsvpRef = rsvpCollection.document(currentUser.uid)
                rsvpRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) rsvpRef.delete()
                    else rsvpRef.set(mapOf("userId" to currentUser.uid, "timestamp" to System.currentTimeMillis()))
                }.addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "RSVP failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

            // Comments setup
            val commentsCollection = firestore.collection("events")
                .document(event.id)
                .collection("comments")

            val commentsList = mutableListOf<Comment>()
            val commentAdapter = CommentAdapter(commentsList)
            holder.commentsRecycler?.adapter = commentAdapter
            holder.commentsRecycler?.layoutManager = LinearLayoutManager(holder.itemView.context)
            holder.commentsRecycler?.setHasFixedSize(true)

            holder.commentsRecycler?.isNestedScrollingEnabled = true
            holder.commentsRecycler?.let { rv ->
                val lp = rv.layoutParams
                if (lp != null && (lp.height == AndroidViewGroup.LayoutParams.MATCH_PARENT ||
                            lp.height == AndroidViewGroup.LayoutParams.WRAP_CONTENT)) {
                    val px = (240 * rv.resources.displayMetrics.density).toInt()
                    lp.height = px
                    rv.layoutParams = lp
                }
                rv.setOnTouchListener { v, ev ->
                    if (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE) {
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    false
                }
            }

            // Listen comments
            commentsCollection.orderBy("timestamp").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserEventAdapter", "Failed comments for ${event.id}: ${error.message}")
                    return@addSnapshotListener
                }
                commentsList.clear()
                val comments = snapshot?.documents?.mapNotNull { it.toObject(Comment::class.java) } ?: emptyList()
                commentsList.addAll(comments)
                commentAdapter.notifyDataSetChanged()
                holder.commentsRecycler?.post { holder.commentsRecycler?.scrollToPosition(commentsList.size - 1) }
            }

            // Send comment — fetch username from users/{uid} then fallback
            holder.sendCommentButton?.setOnClickListener {
                if (currentUser == null) {
                    Toast.makeText(holder.itemView.context, "Please sign in to comment", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val text = holder.commentInput?.text.toString().trim()
                if (text.isEmpty()) {
                    Toast.makeText(holder.itemView.context, "Please enter a comment", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val uid = currentUser.uid
                val usersDoc = firestore.collection("users").document(uid)

                usersDoc.get()
                    .addOnSuccessListener { doc ->
                        val name = doc.getString("username")
                            ?: doc.getString("name")
                            ?: currentUser.displayName
                            ?: "User"

                        val comment = Comment(
                            userId = uid,
                            username = name,
                            text = text,
                            timestamp = System.currentTimeMillis()
                        )

                        commentsCollection.add(comment)
                            .addOnSuccessListener {
                                holder.commentInput?.text?.clear()
                                Toast.makeText(holder.itemView.context, "Comment posted!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(holder.itemView.context, "Failed to send comment: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener {
                        val fallback = currentUser.displayName ?: "User"
                        val comment = Comment(
                            userId = uid,
                            username = fallback,
                            text = text,
                            timestamp = System.currentTimeMillis()
                        )
                        commentsCollection.add(comment)
                            .addOnSuccessListener {
                                holder.commentInput?.text?.clear()
                                Toast.makeText(holder.itemView.context, "Comment posted!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(holder.itemView.context, "Failed to send comment: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
            }

        } else {
            holder.memberCountText.text = "0 members going"
            holder.rsvpButton.isEnabled = false
        }
    }

    override fun getItemCount() = sortedEvents.size
}
