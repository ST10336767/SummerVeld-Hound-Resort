// CommentAdapter.kt
package com.summerveldhoundresort.app.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Comment

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameText: TextView = itemView.findViewById(R.id.textUsername)
        val commentText: TextView = itemView.findViewById(R.id.textComment)
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val nameCache = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val c = comments[position]
        holder.commentText.text = c.text

        // If username is present and not "User", use it
        if (!c.username.isNullOrBlank() && c.username != "User") {
            holder.usernameText.text = c.username
            return
        }

        // Otherwise try cache, then Firestore: users/{uid}
        val uid = c.userId
        val cached = nameCache[uid]
        if (!cached.isNullOrBlank()) {
            holder.usernameText.text = cached
            return
        }

        // temporary while we fetch
        holder.usernameText.text = "User"

        if (uid.isNotBlank()) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val resolved = doc.getString("username")
                        ?: doc.getString("name")
                        ?: "User"
                    nameCache[uid] = resolved
                    // guard: holder might have been recycled
                    if (holder.adapterPosition == position) {
                        holder.usernameText.text = resolved
                    }
                }
                .addOnFailureListener {
                    // keep "User" if lookup fails
                }
        }
    }

    override fun getItemCount() = comments.size
}
