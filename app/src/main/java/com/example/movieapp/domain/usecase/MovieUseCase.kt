package com.example.movieapp.domain.usecase

import com.example.movieapp.common.Resource
import com.example.movieapp.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieUseCase {
    fun getAllMovie(): Flow<Resource<List<Movie>>>
}