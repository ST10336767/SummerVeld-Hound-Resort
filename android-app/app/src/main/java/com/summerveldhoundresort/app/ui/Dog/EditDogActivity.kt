package com.summerveldhoundresort.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.ui.Dog.EditDogFragment
import com.summerveldhoundresort.app.db.entities.Dog

class EditDogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dog)

        if (savedInstanceState == null) {
            val dog = intent.getSerializableExtra("dog") as? Dog

            val fragment = EditDogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("dog", dog)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }
}
