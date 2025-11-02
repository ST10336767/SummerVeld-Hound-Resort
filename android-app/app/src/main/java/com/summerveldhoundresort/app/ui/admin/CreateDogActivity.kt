package com.summerveldhoundresort.app.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.utils.ThemeManager

class CreateDogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
//added
        // Apply saved theme on fragment creation
        val savedTheme = ThemeManager.getThemeMode(this)
        ThemeManager.applyTheme(savedTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_dog)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreateDog())
                .commit()
        }
    }
}
