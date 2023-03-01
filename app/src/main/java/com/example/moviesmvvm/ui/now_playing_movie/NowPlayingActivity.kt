package com.example.moviesmvvm.ui.now_playing_movie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.moviesmvvm.R
import com.example.moviesmvvm.databinding.ActivityNowPlayingBinding

class NowPlayingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNowPlayingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNowPlayingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}
