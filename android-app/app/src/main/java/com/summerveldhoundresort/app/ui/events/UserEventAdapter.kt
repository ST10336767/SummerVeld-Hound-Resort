package com.summerveldhoundresort.app.ui.events

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Comment
import com.summerveldhoundresort.app.db.entities.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

// ADD
import android.view.MotionEvent
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

        // Check if event passed
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance().time
        val eventDate = try {
            sdf.parse(event.date)
        } catch (e: Exception) {
            null
        }

        val isExpired = eventDate != null && eventDate.before(today)
        holder.rsvpButton.isEnabled = !isExpired
        holder.rsvpButton.text = when {
            isExpired -> "Event Passed"
            else -> "RSVP"
        }

        Log.d("UserEventAdapter", "Event ID: '${event.id}', Event name: '${event.name}'")

        if (event.id.isNotEmpty()) {
            val rsvpCollection = firestore.collection("events")
                .document(event.id)
                .collection("rsvps")

            // Listen for RSVP count and update button text
            rsvpCollection.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserEventAdapter", "Failed to load RSVPs: ${error.message}")
                    holder.memberCountText.text = "0 members going"
                    return@addSnapshotListener
                }
                val count = snapshot?.size() ?: 0
                holder.memberCountText.text = "$count members going"

                val userGoing = snapshot?.documents?.any { it.id == currentUser?.uid } ?: false
                if (!isExpired) {
                    holder.rsvpButton.text = if (userGoing) "Un-RSVP" else "RSVP"
                }
            }

            // RSVP button click
            holder.rsvpButton.setOnClickListener {
                if (currentUser == null) {
                    Toast.makeText(holder.itemView.context, "Sign in to RSVP", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val rsvpRef = rsvpCollection.document(currentUser.uid)
                rsvpRef.get().addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        rsvpRef.delete()
                    } else {
                        rsvpRef.set(mapOf(
                            "userId" to currentUser.uid,
                            "timestamp" to System.currentTimeMillis()
                        ))
                    }
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
                // if XML height is wrap_content/match_parent, gives it a viewport so it can scroll
                val lp = rv.layoutParams
                if (lp != null && (lp.height == AndroidViewGroup.LayoutParams.MATCH_PARENT ||
                            lp.height == AndroidViewGroup.LayoutParams.WRAP_CONTENT)) {
                    val px = (240 * rv.resources.displayMetrics.density).toInt()
                    lp.height = px
                    rv.layoutParams = lp
                }
                // prevent parent RecyclerView from stealing scroll gestures
                rv.setOnTouchListener { v, ev ->
                    if (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_MOVE) {
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    false
                }
            }

            Log.d("UserEventAdapter", "Comment input exists: ${holder.commentInput != null}")
            Log.d("UserEventAdapter", "Send button exists: ${holder.sendCommentButton != null}")
            Log.d("UserEventAdapter", "Comments recycler exists: ${holder.commentsRecycler != null}")

            // Listen to comments
            Log.d("UserEventAdapter", "Setting up comment listener for event: ${event.id}")
            commentsCollection.orderBy("timestamp").addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserEventAdapter", "Failed to load comments for event ${event.id}: ${error.message}")
                    return@addSnapshotListener
                }

                commentsList.clear()
                val comments = snapshot?.documents?.mapNotNull { doc ->
                    val comment = doc.toObject(Comment::class.java)
                    Log.d("UserEventAdapter", "Loaded comment: ${comment?.text} by ${comment?.username}")
                    comment
                } ?: emptyList()

                commentsList.addAll(comments)
                Log.d("UserEventAdapter", "Total comments loaded: ${commentsList.size}")
                commentAdapter.notifyDataSetChanged()

                holder.commentsRecycler?.post {
                    holder.commentsRecycler?.scrollToPosition(commentsList.size - 1)
                }

            }

            // Send comment
            holder.sendCommentButton?.setOnClickListener {
                Log.d("UserEventAdapter", "Send comment button clicked")

                // Check if user is logged in
                if (currentUser == null) {
                    Toast.makeText(holder.itemView.context, "Please sign in to comment", Toast.LENGTH_SHORT).show()
                    Log.e("UserEventAdapter", "User not logged in")
                    return@setOnClickListener
                }

                val text = holder.commentInput?.text.toString().trim()
                Log.d("UserEventAdapter", "Comment text: '$text'")

                if (text.isEmpty()) {
                    Toast.makeText(holder.itemView.context, "Please enter a comment", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val comment = Comment(
                    userId = currentUser.uid,
                    username = currentUser.displayName ?: "User",
                    text = text,
                    timestamp = System.currentTimeMillis()
                )

                Log.d("UserEventAdapter", "Adding comment to collection: ${commentsCollection.path}")
                commentsCollection.add(comment)
                    .addOnSuccessListener { docRef ->
                        Log.d("UserEventAdapter", "Comment added successfully with ID: ${docRef.id}")
                        holder.commentInput?.text?.clear()
                        Toast.makeText(holder.itemView.context, "Comment posted!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("UserEventAdapter", "Failed to add comment", e)
                        Toast.makeText(holder.itemView.context, "Failed to send comment: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }

        } else {
            holder.memberCountText.text = "0 members going"
            holder.rsvpButton.isEnabled = false
        }
    }

    override fun getItemCount() = events.size
}
