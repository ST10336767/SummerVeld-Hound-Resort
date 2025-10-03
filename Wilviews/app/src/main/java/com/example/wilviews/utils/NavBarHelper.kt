package com.example.wilviews.utils

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import com.example.wilviews.R

object NavBarHelper {

    // User Nav Bar
    fun setupUserNavBar(activity: Activity, rootView: View) {
        val home = rootView.findViewById<LinearLayout>(R.id.navHome)
        val resort = rootView.findViewById<LinearLayout>(R.id.navDogs)
        val donate = rootView.findViewById<LinearLayout>(R.id.navDonate)
        val profile = rootView.findViewById<LinearLayout>(R.id.navProfile)

        home.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, HomeActivity::class.java))
        }

        resort.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, ResortActivity::class.java))
        }

        donate.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, DonateActivity::class.java))
        }

        profile.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, ProfileActivity::class.java))
        }
    }

    // Admin Nav Bar
    fun setupAdminNavBar(activity: Activity, rootView: View) {
        val dashboard = rootView.findViewById<LinearLayout>(R.id.navHome)
        val manageDogs = rootView.findViewById<LinearLayout>(R.id.navEvents)
        val bookings = rootView.findViewById<LinearLayout>(R.id.navDogs)
        val settings = rootView.findViewById<LinearLayout>(R.id.navProfile)

        dashboard.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, DashboardActivity::class.java))
        }

        manageDogs.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, ManageDogsActivity::class.java))
        }

        bookings.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, BookingsActivity::class.java))
        }

        settings.setOnClickListener {
            // Example: activity.startActivity(Intent(activity, SettingsActivity::class.java))
        }
    }
}