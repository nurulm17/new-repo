package com.example.movieapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.movieapp.domain.usecase.MovieUseCase

class MovieViewModel(movieUseCase: MovieUseCase) : ViewModel() {
    val movie = movieUseCase.getAllMovie().asLiveData()

//    private var _movieResponse = MutableLiveData<MovieResponse?>()
//    val movieResponse: LiveData<MovieResponse?> get() = _movieResponse
//
////
////    fun getMoviePopular() = liveData(Dispatchers.IO) {
////        emit(repository.moviePopular())
////    }
//
//    fun getMoviePopular() {
//        ApiClient.instance.getMoviePopular().enqueue(object : Callback<MovieResponse> {
//            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
//                val data = response.body()
//                _movieResponse.postValue(data)
//            }
//
//            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
//                _movieResponse.postValue(null)
//            }
//
//        })
//    }

//    fun setIncrement(){
//        viewModelScope.launch {
//            pref.incrementCounter()
//        }
//    }
//
//    fun getIncrement() = pref.getCounter().asLiveData()
//
//    fun setLogin(isLogin: Boolean){
//        viewModelScope.launch {
//            pref.setLogin(isLogin)
//        }
//    }
//
//    fun getLogin() = pref.getLogin().asLiveData()
}

