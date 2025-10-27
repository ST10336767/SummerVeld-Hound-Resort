package com.summerveldhoundresort.app.ui.events

import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summerveldhoundresort.app.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.summerveldhoundresort.app.db.entities.Comment
import android.widget.Toast
import android.util.Log

class EventCommentsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AdminCommentAdapter
    private lateinit var tvEventName: TextView
    private lateinit var btnBack: Button

    // ADD
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var commentsListener: ListenerRegistration? = null

    // ADD: keep a mirror of items and their doc IDs (same order as adapter)
    private val commentItems: MutableList<Comment> = mutableListOf()
    private val commentDocIds: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_event_comments)

        val eventId = intent.getStringExtra("EVENT_ID").orEmpty()
        val eventName = intent.getStringExtra("EVENT_NAME").orEmpty()

        tvEventName = findViewById(R.id.tvEventName)
        tvEventName.text = if (eventName.isNotBlank()) eventName else "Event"

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        recycler = findViewById(R.id.recyclerComments)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = AdminCommentAdapter(mutableListOf()) { c ->
            handleDelete(eventId, c)
        }
        recycler.adapter = adapter

        startCommentsListener(eventId)
    }

    private fun startCommentsListener(eventId: String) {
        if (eventId.isBlank()) return
        commentsListener?.remove()

        commentsListener = db.collection("events")
            .document(eventId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                // ADD: log snapshot origin and size
                Log.d("EventComments", "Comments snapshot (fromCache=${snapshot?.metadata?.isFromCache == true}) size=${snapshot?.size() ?: 0}")

                val docs = snapshot?.documents ?: emptyList()
                val list = docs.mapNotNull { it.toObject(Comment::class.java) }
                val ids = docs.map { it.id }

                commentItems.clear()
                commentItems.addAll(list)
                commentDocIds.clear()
                commentDocIds.addAll(ids)

                adapter.submit(list)
            }
    }

    private fun handleDelete(eventId: String, comment: Comment) {
        // ADD: helpful checks and toasts
        if (eventId.isBlank()) {
            Toast.makeText(this, "Missing eventId", Toast.LENGTH_SHORT).show()
            return
        }

        val idx = commentItems.indexOfFirst {
            it.userId == comment.userId &&
                    it.username == comment.username &&
                    it.text == comment.text &&
                    it.timestamp == comment.timestamp
        }
        if (idx < 0) {
            Toast.makeText(this, "Could not resolve comment id", Toast.LENGTH_SHORT).show()
            return
        }

        val docId = commentDocIds.getOrNull(idx)
        if (docId == null) {
            Toast.makeText(this, "Comment docId not found", Toast.LENGTH_SHORT).show()
            return
        }

        // log the exact path we are deleting
        Log.d("EventComments", "Deleting events/$eventId/comments/$docId")

        db.collection("events")
            .document(eventId)
            .collection("comments")
            .document(docId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("EventComments", "Delete failed", e)
                Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroy() {
        commentsListener?.remove()
        commentsListener = null
        super.onDestroy()
    }
}
