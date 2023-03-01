package com.example.moviesmvvm.ui.popular_movie

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviesmvvm.data.api.TheMovieDBClient
import com.example.moviesmvvm.data.api.TheMovieDBInterface
import com.example.moviesmvvm.data.repository.NetworkState
import com.example.moviesmvvm.databinding.ActivityMainBinding

class PopularMovieActivity : AppCompatActivity() {

    private lateinit var viewModel: PopularMovieActivityViewModel

    lateinit var movieRepository: MoviePagedListRepository

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val apiService: TheMovieDBInterface = TheMovieDBClient.getClient()

        movieRepository = MoviePagedListRepository(apiService)

        viewModel = getViewModel()

        val movieAdapter = PopularMoviePagedListAdapter(this)

        val gridLayoutManager = GridLayoutManager(this, 3)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = movieAdapter.getItemViewType(position)
                if (viewType == movieAdapter.MOVIE_VIEW_TYPE) return 1 // Movie_VIEW_TYPE will occupy 1 out of 3 span
                else return 3 // NETWORK_VIEW_TYPE will occupy all 3 span
            }
        }

        binding.apply {
            rvMovieList.layoutManager = gridLayoutManager
            rvMovieList.setHasFixedSize(true)
            rvMovieList.adapter = movieAdapter
        }

        viewModel.moviePagedList.observe(
            this,
            Observer {
                movieAdapter.submitList(it)
            }
        )

        viewModel.networkState.observe(
            this,
            Observer {
                binding.apply {
                    progressBarPopular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
                    txtErrorPopular.visibility = if (viewModel.listIsEmpty() && it == NetworkState.ERROR) View.VISIBLE else View.GONE
                }

                if (!viewModel.listIsEmpty()) {
                    movieAdapter.setNetworkState(it)
                }
            }
        )
    }

    private fun getViewModel(): PopularMovieActivityViewModel {
        return ViewModelProviders.of(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return PopularMovieActivityViewModel(movieRepository) as T
                }
            }
        )[PopularMovieActivityViewModel::class.java]
    }
}
