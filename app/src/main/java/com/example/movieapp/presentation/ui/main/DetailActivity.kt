package com.example.movieapp.presentation.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movieapp.data.remote.response.Movies
import com.example.movieapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var _activityDetailBinding: ActivityDetailBinding
    private val binding get() = _activityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ambil data dari intent
        val movies = intent.getParcelableExtra<Movies>("EXTRA_MOVIE")

        movies?.let {
            binding.tvDetailTitle.text = it.title
            binding.tvYearContent.text = it.date
            binding.tvDescContent.text = it.overview
            Glide.with(this).load("https://image.tmdb.org/t/p/w500/" + it.poster)
                .into(binding.imgDetail)
        }

        binding.btnBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

    }
}