package com.example.moviesmvvm.ui.single_movie_details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.moviesmvvm.R
import com.example.moviesmvvm.data.api.POSTER_BASE_URL
import com.example.moviesmvvm.data.api.TheMovieDBClient
import com.example.moviesmvvm.data.api.TheMovieDBInterface
import com.example.moviesmvvm.data.repository.NetworkState
import com.example.moviesmvvm.data.vo.MovieDetails
import com.example.moviesmvvm.databinding.ActivitySingleMovieBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_single_movie.*
import kotlinx.android.synthetic.main.activity_single_movie.view.*
import java.text.NumberFormat
import java.util.*

class SingleMovie : AppCompatActivity() {

    private lateinit var viewModel: SingleMovieViewModel
    private lateinit var movieDetailsRepository: MovieDetailsRepository
    private lateinit var binding: ActivitySingleMovieBinding
    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleMovieBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val movieId: Int = intent.getIntExtra("id", 1)

        val apiService: TheMovieDBInterface = TheMovieDBClient.getClient()
        movieDetailsRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(
            this,
            Observer {
                bindUI(it)
            }
        )

        viewModel.movieDetails.observe(
            this,
            Observer {
                binding.progressBar.visibility = if (it.equals(NetworkState.LOADED)) View.VISIBLE else View.GONE
                binding.txtError.visibility = if (it.equals(NetworkState.ERROR)) View.VISIBLE else View.GONE
            }
        )

        showTrailerPlayer()
    }

    fun bindUI(it: MovieDetails) {
        binding.apply {
            movieTitle.text = it.title
            movieTagline.text = it.tagline
            movieReleaseDate.text = it.releaseDate
            movieRating.text = it.rating.toString()
            movieRuntime.text = it.runtime.toString() + " minutes"
            movieOverview.text = it.overview

            val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
            movieBudget.text = formatCurrency.format(it.budget)
            movieRevenue.text = formatCurrency.format(it.revenue)

            val moviePosterURL = POSTER_BASE_URL + it.posterPath
            Glide.with(this@SingleMovie)
                .load(moviePosterURL)
                .into(ivMoviePoster)
        }
    }

    private fun getViewModel(movieId: Int): SingleMovieViewModel {
        return ViewModelProviders.of(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return SingleMovieViewModel(movieDetailsRepository, movieId) as T
                }
            }
        )[SingleMovieViewModel::class.java]
    }

    fun TrailerPlayer() {
        youTubePlayerView = findViewById(R.id.youtubePlayerView)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "x5DhuDSArTI"
                youTubePlayer.loadVideo(videoId, 0f)
//                youTubePlayer.loadVideo(videoId, 0f)
            }
        })
    }

    fun showTrailerPlayer() {
        binding.apply {
            ivMoviePoster.setOnClickListener {
                if (ivMoviePoster.isVisible) {
                    ivMoviePoster.visibility = View.GONE
                    youtubePlayerView.visibility = View.VISIBLE
                }
                TrailerPlayer()
            }
        }
    }
}
