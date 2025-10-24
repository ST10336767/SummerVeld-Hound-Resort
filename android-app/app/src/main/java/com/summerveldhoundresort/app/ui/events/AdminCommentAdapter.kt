package com.summerveldhoundresort.app.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.db.entities.Comment

class AdminCommentAdapter(
    private val items: MutableList<Comment>,
    private val onDelete: (Comment) -> Unit
) : RecyclerView.Adapter<AdminCommentAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvUser: TextView = v.findViewById(R.id.tvUser)
        val tvText: TextView = v.findViewById(R.id.tvText)
        val tvTime: TextView = v.findViewById(R.id.tvTime)
        val btnDelete: Button = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_admin, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val c = items[position]
        h.tvUser.text = c.username.ifBlank { "User" }
        h.tvText.text = c.text
        h.tvTime.text = formatTime(c.timestamp)

        h.btnDelete.setOnClickListener { onDelete(c) }
    }

    override fun getItemCount() = items.size

    fun submit(newItems: List<Comment>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    private fun formatTime(ts: Long?): String {
        if (ts == null || ts == 0L) return ""
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        return fmt.format(java.util.Date(ts))
    }
}
