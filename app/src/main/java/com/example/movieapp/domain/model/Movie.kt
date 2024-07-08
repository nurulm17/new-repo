package com.example.movieapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val id: String?,
    val title: String?,
    val releaseDate: String?,
    val posterPath: String?,
    val overview: String?
):Parcelable