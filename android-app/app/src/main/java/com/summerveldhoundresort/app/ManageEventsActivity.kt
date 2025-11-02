package com.summerveldhoundresort.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.summerveldhoundresort.app.db.entities.Event
import com.summerveldhoundresort.app.ui.events.AdminEventAdapter
import java.text.SimpleDateFormat
import java.util.*

class ManageEventsActivity : AppCompatActivity() {

    private val db by lazy { FirebaseFirestore.getInstance() }

    private lateinit var recycler: RecyclerView
    private lateinit var addBtn: TextView
    private lateinit var backBtn: Button

    private val items = mutableListOf<Event>()
    private lateinit var adapter: AdminEventAdapter

    // date+time helpers to match adapters
    private val dtf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply { isLenient = false }
    private fun parseDT(d: String?, t: String?): Date? = try {
        val dd = d?.trim().orEmpty()
        val tt = t?.trim().orEmpty()
        when {
            dd.isEmpty() -> null
            tt.isEmpty() -> dtf.parse("$dd 23:59")
            else -> dtf.parse("$dd $tt")
        }
    } catch (_: Exception) { null }

    private fun sort(list: List<Event>): List<Event> {
        val now = Date()
        val (known, unknown) = list.partition { parseDT(it.date, it.time) != null }
        val (past, upcoming) = known.partition { parseDT(it.date, it.time)!!.before(now) }
        return upcoming.sortedBy { parseDT(it.date, it.time) } +
                past.sortedByDescending { parseDT(it.date, it.time) } +
                unknown
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_events)

        recycler = findViewById(R.id.recyclerViewEvents)
        addBtn = findViewById(R.id.btnAddEvent)
        backBtn = findViewById(R.id.btnBack)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = AdminEventAdapter(items) { event ->
            val i = Intent(this, EditEventActivity::class.java)
            i.putExtra("EVENT_ID", event.id)
            startActivity(i)
        }
        recycler.adapter = adapter

        addBtn.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }
        backBtn.setOnClickListener { finish() }

        // live load from Firestore
        db.collection("events").addSnapshotListener { snap, err ->
            if (err != null) {
                items.clear()
                adapter.notifyDataSetChanged()
                return@addSnapshotListener
            }

            val loaded = snap?.documents?.mapNotNull { doc ->
                val e = doc.toObject(Event::class.java)
                if (e != null) e.id = doc.id
                e
            } ?: emptyList()

            items.clear()
            items.addAll(sort(loaded))
            adapter.notifyDataSetChanged()
        }
    }
}
