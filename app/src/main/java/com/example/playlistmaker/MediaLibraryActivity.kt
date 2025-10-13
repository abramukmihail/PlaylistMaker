package com.example.playlistmaker


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MediaLibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medialibrary)
        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {finish()}
    }
}