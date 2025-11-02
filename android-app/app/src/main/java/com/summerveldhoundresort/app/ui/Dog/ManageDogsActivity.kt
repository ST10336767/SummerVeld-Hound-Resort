package com.summerveldhoundresort.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.ui.Dog.ManageDogsFragment

class ManageDogsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_dogs)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ManageDogsFragment())
                .commit()
        }

    }
}
