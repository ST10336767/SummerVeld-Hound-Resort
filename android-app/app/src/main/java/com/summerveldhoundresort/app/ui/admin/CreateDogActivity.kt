package com.summerveldhoundresort.app.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.R

class CreateDogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_dog)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CreateDog())
                .commit()
        }
    }
}
