package com.example.playlistmaker.presentation.ui.medialibrary

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medialibrary)
        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {finish()}
    }
}