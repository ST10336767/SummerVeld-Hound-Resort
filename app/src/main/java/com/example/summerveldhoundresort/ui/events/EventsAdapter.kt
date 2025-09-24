package com.example.summerveldhoundresort.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.summerveldhoundresort.R
//Data class Should be moved else where (Put here for testing)
data class Event(
    val id: String,
    val title: String,
    val name: String,
    val date: String,
    val time: String,
    val description: String,
    val comments: List<String> = emptyList()
)

class EventsAdapter(
    private val onRsvp: (Event) -> Unit,
    private val onSend: (Event, String) -> Unit
) : ListAdapter<Event, EventsAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        val liveComments: LinearLayout = view.findViewById(R.id.liveComments)
        val etComment: EditText = view.findViewById(R.id.etComment)
        val btnSend: AppCompatButton = view.findViewById(R.id.btnSend)
        val btnRsvp: AppCompatButton = view.findViewById(R.id.btnRsvp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = getItem(position)

        // top block
        h.tvTitle.text = item.title
        h.tvName.text = item.name
        h.tvDate.text = item.date
        h.tvTime.text = item.time
        h.tvDesc.text = item.description

        // comments list
        h.liveComments.removeAllViews()
        val ctx = h.itemView.context
        item.comments.forEach { msg ->
            val tv = TextView(ctx).apply {
                text = "• $msg"
                setTextColor(0xFF000000.toInt())
            }
            h.liveComments.addView(tv)
        }

        // actions
        h.btnRsvp.setOnClickListener { onRsvp(item) }
        h.btnSend.setOnClickListener {
            val text = h.etComment.text?.toString()?.trim().orEmpty()
            if (text.isNotEmpty()) {
                onSend(item, text)
                h.etComment.setText("")
            }
        }
    }
}
