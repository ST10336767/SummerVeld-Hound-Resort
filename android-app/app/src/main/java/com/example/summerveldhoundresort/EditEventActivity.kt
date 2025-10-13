package com.example.summerveldhoundresort

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditEventActivity : AppCompatActivity() {

    private lateinit var eventNameInput: EditText
    private lateinit var eventDescInput: EditText
    private lateinit var eventDateInput: EditText
    private lateinit var eventTimeInput: EditText
    private lateinit var eventLocationInput: EditText
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var backBtn: Button

    private val db = FirebaseFirestore.getInstance()
    private var eventId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_edit_event)

        // Bind views
        eventNameInput = findViewById(R.id.eventNameInput)
        eventDescInput = findViewById(R.id.eventDescInput)
        eventDateInput = findViewById(R.id.eventDateInput)
        eventTimeInput = findViewById(R.id.eventTimeInput)
        eventLocationInput = findViewById(R.id.eventLocationInput)
        saveBtn = findViewById(R.id.saveEventBtn)
        deleteBtn = findViewById(R.id.deleteEventBtn)
        backBtn = findViewById(R.id.backBtn)

        eventId = intent.getStringExtra("EVENT_ID")

        if (eventId == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadEventDetails(eventId!!)

        // Date picker
        eventDateInput.isFocusable = false
        eventDateInput.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, y, m, d ->
                    eventDateInput.setText("%04d-%02d-%02d".format(y, m + 1, d))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Time picker
        eventTimeInput.isFocusable = false
        eventTimeInput.setOnClickListener {
            val cal = Calendar.getInstance()
            val timePicker = TimePickerDialog(this,
                { _, h, min ->
                    eventTimeInput.setText("%02d:%02d".format(h, min))
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        // Save changes
        saveBtn.setOnClickListener {
            val updates = mapOf(
                "name" to eventNameInput.text.toString(),
                "description" to eventDescInput.text.toString(),
                "date" to eventDateInput.text.toString(),
                "time" to eventTimeInput.text.toString(),
                "location" to eventLocationInput.text.toString()
            )
            db.collection("events").document(eventId!!)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Delete event
        deleteBtn.setOnClickListener {
            db.collection("events").document(eventId!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Back button
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadEventDetails(eventId: String) {
        db.collection("events").document(eventId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    eventNameInput.setText(doc.getString("name"))
                    eventDescInput.setText(doc.getString("description"))
                    eventDateInput.setText(doc.getString("date"))
                    eventTimeInput.setText(doc.getString("time"))
                    eventLocationInput.setText(doc.getString("location"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditEventActivity", "Error loading event", e)
                Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
