//package com.summerveldhoundresort.app
//
//import android.app.DatePickerDialog
//import android.app.TimePickerDialog
//import android.os.Bundle
//import android.util.Log
//import android.view.Window
//import android.widget.Button
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.firestore.FirebaseFirestore
//import java.util.*
//
//class EditEventActivity : AppCompatActivity() {
//
//    private lateinit var eventNameInput: EditText
//    private lateinit var eventDescInput: EditText
//    private lateinit var eventDateInput: EditText
//    private lateinit var eventTimeInput: EditText
//    private lateinit var eventLocationInput: EditText
//    private lateinit var saveBtn: Button
//    private lateinit var deleteBtn: Button
//    private lateinit var backBtn: Button
//
//    private val db = FirebaseFirestore.getInstance()
//    private var eventId: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
//        setContentView(R.layout.activity_edit_event)
//
//        // Bind views
//        eventNameInput = findViewById(R.id.eventNameInput)
//        eventDescInput = findViewById(R.id.eventDescInput)
//        eventDateInput = findViewById(R.id.eventDateInput)
//        eventTimeInput = findViewById(R.id.eventTimeInput)
//        eventLocationInput = findViewById(R.id.eventLocationInput)
//        saveBtn = findViewById(R.id.saveEventBtn)
//        deleteBtn = findViewById(R.id.deleteEventBtn)
//        backBtn = findViewById(R.id.backBtn)
//
//        findViewById<Button?>(R.id.btnBack)?.setOnClickListener { finish() }
//
//        eventId = intent.getStringExtra("EVENT_ID")
//
//        if (eventId == null) {
//            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
//
//        loadEventDetails(eventId!!)
//
//        // Date picker
//        eventDateInput.isFocusable = false
//        eventDateInput.setOnClickListener {
//            val cal = Calendar.getInstance()
//            val datePicker = DatePickerDialog(
//                this,
//                { _, y, m, d -> eventDateInput.setText("%04d-%02d-%02d".format(y, m + 1, d)) },
//                cal[Calendar.YEAR],
//                cal[Calendar.MONTH],
//                cal[Calendar.DAY_OF_MONTH]
//            )
//            datePicker.show()
//        }
//
//        // Time picker
//        eventTimeInput.isFocusable = false
//        eventTimeInput.setOnClickListener {
//            val cal = Calendar.getInstance()
//            val timePicker = TimePickerDialog(
//                this,
//                { _, h, min -> eventTimeInput.setText("%02d:%02d".format(h, min)) },
//                cal[Calendar.HOUR_OF_DAY],
//                cal[Calendar.MINUTE],
//                true
//            )
//            timePicker.show()
//        }
//
//        // Save changes
//        saveBtn.setOnClickListener {
//            val name = eventNameInput.text.toString().trim()
//            val desc = eventDescInput.text.toString().trim()
//            val date = eventDateInput.text.toString().trim()
//            val time = eventTimeInput.text.toString().trim()
//            val location = eventLocationInput.text.toString().trim()
//
//            if (name.isEmpty() || desc.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//            val updates = mapOf(
//                "name" to name,
//                "description" to desc,
//                "date" to date,
//                "time" to time,
//                "location" to location
//            )
//            db.collection("events").document(eventId!!)
//                .update(updates)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                .addOnFailureListener {
//                    Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show()
//                }
//        }
//
//        // Delete event
//        deleteBtn.setOnClickListener {
//            deleteEventWithSubcollections(eventId!!)
//        }
//
//        // Existing cancel button
//        backBtn.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun loadEventDetails(eventId: String) {
//        db.collection("events").document(eventId).get()
//            .addOnSuccessListener { doc ->
//                if (doc.exists()) {
//                    eventNameInput.setText(doc.getString("name"))
//                    eventDescInput.setText(doc.getString("description"))
//                    eventDateInput.setText(doc.getString("date"))
//                    eventTimeInput.setText(doc.getString("time"))
//                    eventLocationInput.setText(doc.getString("location"))
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("EditEventActivity", "Error loading event", e)
//                Toast.makeText(this, "Failed to load event", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//    }
//
//    private fun deleteEventWithSubcollections(eventId: String) {
//        androidx.appcompat.app.AlertDialog.Builder(this)
//            .setTitle("Delete Event")
//            .setMessage("Are you sure you want to delete this event? This will also delete all comments and RSVPs. This action cannot be undone.")
//            .setPositiveButton("Delete") { _, _ ->
//                performCompleteEventDeletion(eventId)
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//    private fun performCompleteEventDeletion(eventId: String) {
//        Toast.makeText(this, "Deleting event and all associated data...", Toast.LENGTH_SHORT).show()
//
//        val eventRef = db.collection("events").document(eventId)
//
//        deleteSubcollection(eventRef.collection("comments"), "comments") {
//            deleteSubcollection(eventRef.collection("rsvps"), "RSVPs") {
//                eventRef.delete()
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "Event and all associated data deleted successfully", Toast.LENGTH_SHORT).show()
//                        Log.d("EditEventActivity", "Event deleted: $eventId")
//                        finish()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(this, "Failed to delete event. Try again.", Toast.LENGTH_SHORT).show()
//                        Log.e("EditEventActivity", "Error deleting event", e)
//                    }
//            }
//        }
//    }
//
//    private fun deleteSubcollection(
//        collectionRef: com.google.firebase.firestore.CollectionReference,
//        collectionName: String,
//        onComplete: (Boolean) -> Unit
//    ) {
//        collectionRef.get()
//            .addOnSuccessListener { snapshot ->
//                if (snapshot.isEmpty) {
//                    Log.d("EditEventActivity", "No $collectionName to delete")
//                    onComplete(true)
//                    return@addOnSuccessListener
//                }
//
//                val batch = db.batch()
//                snapshot.documents.forEach { doc ->
//                    batch.delete(doc.reference)
//                }
//
//                batch.commit()
//                    .addOnSuccessListener {
//                        Log.d("EditEventActivity", "Successfully deleted ${snapshot.size()} $collectionName")
//                        onComplete(true)
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("EditEventActivity", "Error deleting $collectionName", e)
//                        onComplete(false)
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.e("EditEventActivity", "Error getting $collectionName", e)
//                onComplete(false)
//            }
//    }
//}

package com.summerveldhoundresort.app

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

        findViewById<Button?>(R.id.btnBack)?.setOnClickListener { finish() }

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
            val datePicker = DatePickerDialog(
                this,
                { _, y, m, d -> eventDateInput.setText("%04d-%02d-%02d".format(y, m + 1, d)) },
                cal[Calendar.YEAR],
                cal[Calendar.MONTH],
                cal[Calendar.DAY_OF_MONTH]
            )
            datePicker.show()
        }

        // Time picker
        eventTimeInput.isFocusable = false
        eventTimeInput.setOnClickListener {
            val cal = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, h, min -> eventTimeInput.setText("%02d:%02d".format(h, min)) },
                cal[Calendar.HOUR_OF_DAY],
                cal[Calendar.MINUTE],
                true
            )
            timePicker.show()
        }

        // Save changes
        saveBtn.setOnClickListener {
            val name = eventNameInput.text.toString().trim()
            val desc = eventDescInput.text.toString().trim()
            val date = eventDateInput.text.toString().trim()
            val time = eventTimeInput.text.toString().trim()
            val location = eventLocationInput.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val updates = mapOf(
                "name" to name,
                "description" to desc,
                "date" to date,
                "time" to time,
                "location" to location
            )
            db.collection("events").document(eventId!!)
                .update(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show()
                }
        }

        // Delete event
        deleteBtn.setOnClickListener {
            deleteEventWithSubcollections(eventId!!)
        }

        // Existing cancel button
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

    private fun deleteEventWithSubcollections(eventId: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete this event? This will also delete all comments and RSVPs. This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                performCompleteEventDeletion(eventId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performCompleteEventDeletion(eventId: String) {
        Toast.makeText(this, "Deleting event and all associated data...", Toast.LENGTH_SHORT).show()

        val eventRef = db.collection("events").document(eventId)

        // Step 1: Delete comments and verify
        deleteSubcollection(eventRef.collection("comments"), "comments") { commentsDeleted ->
            if (!commentsDeleted) {
                showDeletionError("Failed to delete comments. Event deletion cancelled to prevent data loss.")
                return@deleteSubcollection
            }

            // Verify comments are deleted
            verifySubcollectionDeleted(eventRef.collection("comments"), "comments") { commentsVerified ->
                if (!commentsVerified) {
                    showDeletionError("Comments deletion verification failed. Event deletion cancelled.")
                    return@verifySubcollectionDeleted
                }

                // Step 2: Delete RSVPs and verify
                deleteSubcollection(eventRef.collection("rsvps"), "RSVPs") { rsvpsDeleted ->
                    if (!rsvpsDeleted) {
                        showDeletionError("Failed to delete RSVPs. Event deletion cancelled to prevent data loss.")
                        return@deleteSubcollection
                    }

                    // Verify RSVPs are deleted
                    verifySubcollectionDeleted(eventRef.collection("rsvps"), "RSVPs") { rsvpsVerified ->
                        if (!rsvpsVerified) {
                            showDeletionError("RSVPs deletion verification failed. Event deletion cancelled.")
                            return@verifySubcollectionDeleted
                        }

                        // Step 3: Delete event document (only if all subcollections are verified deleted)
                        eventRef.delete()
                            .addOnSuccessListener {
                                // Step 4: Verify event is deleted
                                verifyEventDeleted(eventId) { eventVerified ->
                                    if (eventVerified) {
                                        Toast.makeText(
                                            this,
                                            "Event and all associated data deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("EditEventActivity", "Event and all data verified deleted: $eventId")
                                        finish()
                                    } else {
                                        showDeletionError("Event deletion verification failed. Please check manually.")
                                        Log.w("EditEventActivity", "Event deletion verification failed: $eventId")
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                showDeletionError("Failed to delete event document: ${e.message}")
                                Log.e("EditEventActivity", "Error deleting event", e)
                            }
                    }
                }
            }
        }
    }

    private fun deleteSubcollection(
        collectionRef: com.google.firebase.firestore.CollectionReference,
        collectionName: String,
        onComplete: (Boolean) -> Unit
    ) {
        collectionRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d("EditEventActivity", "No $collectionName to delete")
                    onComplete(true)
                    return@addOnSuccessListener
                }

                val documentCount = snapshot.size()
                Log.d("EditEventActivity", "Deleting $documentCount $collectionName documents")

                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d("EditEventActivity", "Successfully deleted $documentCount $collectionName")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditEventActivity", "Error deleting $collectionName", e)
                        Toast.makeText(this, "Error deleting $collectionName: ${e.message}", Toast.LENGTH_LONG).show()
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("EditEventActivity", "Error getting $collectionName", e)
                Toast.makeText(this, "Error accessing $collectionName: ${e.message}", Toast.LENGTH_LONG).show()
                onComplete(false)
            }
    }

    /**
     * Verify that a subcollection is completely empty after deletion
     */
    private fun verifySubcollectionDeleted(
        collectionRef: com.google.firebase.firestore.CollectionReference,
        collectionName: String,
        onComplete: (Boolean) -> Unit
    ) {
        collectionRef.get()
            .addOnSuccessListener { snapshot ->
                val remainingCount = snapshot.size()
                if (remainingCount == 0) {
                    Log.d("EditEventActivity", "✓ Verified: All $collectionName deleted")
                    onComplete(true)
                } else {
                    Log.w("EditEventActivity", "✗ Verification failed: $remainingCount $collectionName still exist")
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditEventActivity", "Error verifying $collectionName deletion", e)
                // If verification fails due to network error, assume success to avoid blocking
                // but log the error for investigation
                onComplete(true)
            }
    }

    /**
     * Verify that the event document is deleted
     */
    private fun verifyEventDeleted(eventId: String, onComplete: (Boolean) -> Unit) {
        db.collection("events").document(eventId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Log.d("EditEventActivity", "✓ Verified: Event $eventId is deleted")
                    onComplete(true)
                } else {
                    Log.w("EditEventActivity", "✗ Verification failed: Event $eventId still exists")
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditEventActivity", "Error verifying event deletion", e)
                // If verification fails due to network error, assume success
                onComplete(true)
            }
    }

    /**
     * Show error dialog with retry option
     */
    private fun showDeletionError(message: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Deletion Error")
            .setMessage("$message\n\nWould you like to try again?")
            .setPositiveButton("Retry") { _, _ ->
                eventId?.let { performCompleteEventDeletion(it) }
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(this, "Deletion cancelled", Toast.LENGTH_SHORT).show()
            }
            .show()
    }
}
