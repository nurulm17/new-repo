package com.example.movieapp.data.remote.network

import com.example.movieapp.data.remote.response.MovieResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("movie/popular")
    suspend fun getMoviePopular(
        @Header("Authorization") apikey: String = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3MWE2NjhkZjc3MjBjNmE5NjI1MTkwODM5ZmU5ZjJhMyIsInN1YiI6IjY2NDQ5ZWIwZTY4YjdjNjhjYjc3ZmI4OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.Z_80eE4Q5v5Av35AarUFwakRKbSqr9Mm8WBHewPXU2k"
    ): MovieResponse
}