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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.summerveldhoundresort.app.utils.ThemeManager
import java.util.Calendar

class AddEventActivity : AppCompatActivity() {

    private lateinit var eventNameInput: EditText
    private lateinit var eventDescInput: EditText
    private lateinit var eventDateInput: EditText
    private lateinit var eventTimeInput: EditText
    private lateinit var eventLocationInput: EditText
    private lateinit var saveEventBtn: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        // Apply saved theme on fragment creation
        val savedTheme = ThemeManager.getThemeMode(this)
        ThemeManager.applyTheme(savedTheme)

        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_add_event)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.d("AddEventActivity", "Logged in user UID: ${user.uid}, Email: ${user.email}")
        } else {
            Log.d("AddEventActivity", "No user is currently logged in.")
        }

        eventNameInput = findViewById(R.id.eventNameInput)
        eventDescInput = findViewById(R.id.eventDescInput)
        eventDateInput = findViewById(R.id.eventDateInput)
        eventTimeInput = findViewById(R.id.eventTimeInput)
        eventLocationInput = findViewById(R.id.eventLocationInput)
        saveEventBtn = findViewById(R.id.saveEventBtn)

        // Make date and time EditTexts clickable, not editable
        eventDateInput.isFocusable = false
        eventDateInput.isClickable = true
        eventTimeInput.isFocusable = false
        eventTimeInput.isClickable = true

        // Date Picker
        eventDateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                eventDateInput.setText(formattedDate)
            }, year, month, day)

            // Optional: prevent past dates
            datePicker.datePicker.minDate = calendar.timeInMillis
            datePicker.show()
        }

        // Time Picker
        eventTimeInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val formattedTime = "%02d:%02d".format(selectedHour, selectedMinute)
                eventTimeInput.setText(formattedTime)
            }, hour, minute, true) // 24-hour format

            timePicker.show()
        }

        val backBtn: Button = findViewById(R.id.btnBack)
        backBtn.setOnClickListener {
            finish() // simply finishes this activity and goes back
        }

        // Save Event
        saveEventBtn.setOnClickListener {
            val eventName = eventNameInput.text.toString().trim()
            val eventDesc = eventDescInput.text.toString().trim()
            val eventDate = eventDateInput.text.toString().trim()
            val eventLocation = eventLocationInput.text.toString().trim()
            val eventTime = eventTimeInput.text.toString().trim()

            if (eventName.isNotEmpty() && eventDate.isNotEmpty() && eventTime.isNotEmpty() && eventLocation.isNotEmpty() && eventDesc.isNotEmpty() && user != null) {
                val event = hashMapOf(
                    "name" to eventName,
                    "description" to eventDesc,
                    "date" to eventDate,
                    "location" to eventLocation,
                    "time" to eventTime
                )

                db.collection("events")
                    .add(event)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error saving event. Try again.", Toast.LENGTH_LONG).show()
                        Log.e("AddEventActivity", "Firestore write failed", e)
                    }
            } else {
                Toast.makeText(this, "Please enter name and date", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
