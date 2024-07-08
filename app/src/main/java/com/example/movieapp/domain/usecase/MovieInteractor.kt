package com.example.movieapp.domain.usecase

import com.example.movieapp.domain.repository.IMovieRepository


class MovieInteractor(private val movieRepository: IMovieRepository) : MovieUseCase{
    override fun getAllMovie() = movieRepository.getAllMovie()
}