package com.example.movieapp.data.remote.repository

import com.example.movieapp.common.Resource
import com.example.movieapp.data.remote.RemoteDataSource
import com.example.movieapp.data.remote.network.ApiResponse
import com.example.movieapp.data.remote.response.toMovie
import com.example.movieapp.domain.model.Movie
import com.example.movieapp.domain.repository.IMovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map


class MovieRepository( private val remoteDataSource: RemoteDataSource): IMovieRepository {


    override fun getAllMovie(): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())
            remoteDataSource.getAllMovie().map { apiResponse ->
                when(apiResponse){
                    is ApiResponse.Success -> {
                        val movieList = apiResponse.data.map { it.toMovie() }
                        Resource.Success(movieList)
                    }
                    is ApiResponse.Empty ->{
                        Resource.Success(emptyList())
                    }
                    is ApiResponse.Error -> {
                        Resource.Error(apiResponse.errorMessage)
                    }
                }
            }.collect{resource ->
                emit(resource)
            }
            }.catch { e ->
                emit(Resource.Error(e.message ?: "An unknown error"))
        }.flowOn(Dispatchers.IO)
        }
    }
//    override fun getAllMovie(): Flow<Resource<List<Movie>>> {
//        val result = MutableLiveData<Resource<List<Movie>>>()
//        remoteDataSource.getAllMovie().observeForever{apiResponse ->
//            when(apiResponse) {
//                is ApiResponse.Success -> {
//                    val movieList = apiResponse.data.map { it.toMovie() }
//                    result.postValue(Resource.Success(movieList))
//                }
//                is ApiResponse.Empty -> {
//                    result.postValue(Resource.Success(emptyList()))
//                }
//                is ApiResponse.Error -> {
//                    result.postValue(Resource.Error(apiResponse.errorMessage, null))
//                }
//            }
//        }
//        return  result
//    }
//
//
//
//}
//class MovieRepository private constructor(private val remoteDataSource: RemoteDataSource): IMovieRepository {
//     override suspend fun moviePopular():List<MovieResponse>{
//        return remoteDataSource.moviePopular()
//    }
    /*
    return
    * ubah jadi moviePopular(): domainnya apa = .....moviePopuplar() di emit diubah jadi kekk yg di CoinApp di getCoinUseCase
    * */
  //  override suspend fun moviePopular() = remoteDataSource.moviePopular()
//}