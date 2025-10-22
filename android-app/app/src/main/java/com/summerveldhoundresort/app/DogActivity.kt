package com.summerveldhoundresort.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.ui.admin.CreateDog

class DogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dog)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreateDog())
                .commit()
        }
    }
}
