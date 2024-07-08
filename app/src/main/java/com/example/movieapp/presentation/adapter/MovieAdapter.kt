package com.example.movieapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapp.data.remote.response.Movies
import com.example.movieapp.databinding.ItemListBinding
import com.example.movieapp.presentation.utils.MovieDiffCallback
import com.example.movieapp.domain.model.Movie


class MovieAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    private val moviesList = ArrayList<Movie>()

    interface OnItemClickListener {
        fun onItemClick(movies: Movie)
    }

    fun setMovieList(moviesList: List<Movie>) {
        val diffCallback = MovieDiffCallback(this.moviesList, moviesList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.moviesList.clear()
        this.moviesList.addAll(moviesList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = moviesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(moviesList[position])
    }

    inner class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val IMAGE_BASE = "https://image.tmdb.org/t/p/w500/"

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(moviesList[position])
                }
            }
        }

        fun bind(movies: Movie) {
            binding.tvMovieTitle.text = movies.title
            binding.tvMovieYear.text = movies.releaseDate
            binding.tvMovieDesc.text = movies.overview
            Glide.with(binding.root.context).load(IMAGE_BASE + movies.posterPath).into(binding.listImg)
        }
    }
}
