package com.example.movieapp.data.remote.response

import android.os.Parcelable
import com.example.movieapp.domain.model.Movie
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movies(
    @SerializedName("id")
    val id : String?,

    @SerializedName("original_language")
    val language: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("release_date")
    val date: String?,

    @SerializedName("poster_path")
    val poster: String?,

    @SerializedName("popularity")
    val popularity: Double,

    @SerializedName("vote_average")
    val vote: String?,

    @SerializedName("overview")
    val overview: String?,


): Parcelable

fun Movies.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        releaseDate = date,
        posterPath = poster,
        overview = overview
    )
}