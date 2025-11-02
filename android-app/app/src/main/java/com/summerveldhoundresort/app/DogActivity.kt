package com.summerveldhoundresort.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.ui.admin.CreateDog
import com.summerveldhoundresort.app.utils.ThemeManager

class DogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        //added to apply themes
        // Apply saved theme on fragment creation
        val savedTheme = ThemeManager.getThemeMode(this)
        ThemeManager.applyTheme(savedTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreateDog())
                .commit()
        }
    }
}
