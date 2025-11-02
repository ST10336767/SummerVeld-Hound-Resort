package com.summerveldhoundresort.app.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.summerveldhoundresort.app.R
import com.summerveldhoundresort.app.databinding.ActivityAuthBinding
import com.summerveldhoundresort.app.utils.ThemeManager

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        //added -> apply saved themes straight from login/signup of app
        // Apply saved theme on fragment creation
        val savedTheme = ThemeManager.getThemeMode(this)
        ThemeManager.applyTheme(savedTheme)

        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}