package com.summerveldhoundresort.app.ui.events

import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.summerveldhoundresort.app.R

class EventCommentsActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AdminCommentAdapter
    private lateinit var tvEventName: TextView
    private lateinit var btnBack: Button

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
        adapter = AdminCommentAdapter(mutableListOf()) { /* onDelete tapped - backend later */ }
        recycler.adapter = adapter

        // Frontend only atm

    }
}
