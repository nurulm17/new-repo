package com.example.movieapp.data.remote

import android.util.Log
import com.example.movieapp.data.remote.network.ApiResponse
import com.example.movieapp.data.remote.network.ApiService
import com.example.movieapp.data.remote.response.Movies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoteDataSource(private val apiService: ApiService) {

     suspend fun getAllMovie(): Flow<ApiResponse<List<Movies>>> {
       return flow {
           try {
               val response = apiService.getMoviePopular()
               val dataArray = response.movies
               if(dataArray.isNotEmpty()){
                   emit(ApiResponse.Success(response.movies))
               } else {
                   emit(ApiResponse.Empty)
               }
           } catch (e: Exception) {
               emit(ApiResponse.Error(e.toString()))
               Log.e("RemoteDataSource", e.toString())
           }
       } .flowOn(Dispatchers.IO)
     }}
//        val resultData = MutableLiveData<ApiResponse<List<Movies>>>()
//
//        //get data from remote api
//        val client = apiService.getMoviePopular()
//
//        client.enqueue(object : Callback<MovieResponse> {
//            override fun onResponse(
//                call: Call<MovieResponse>,
//                response: Response<MovieResponse>
//            ) {
//                val dataArray = response.body()?.movies
//                resultData.value = if (dataArray != null) ApiResponse.Success(dataArray) else ApiResponse.Empty
//            }
//
//            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
//                resultData.value = ApiResponse.Error(t.message.toString())
//                Log.e("RemoteDataSource", t.message.toString())
//            }
//        })
//
//        return resultData
//    }
//}


//class RemoteDataSource(private val apiService: ApiService) {
//    companion object {
//        @Volatile
//        private var instance: RemoteDataSource? = null
//
//        fun getInstance(service: ApiService): RemoteDataSource = instance ?: synchronized(this){
//            instance ?: RemoteDataSource(service)
//        }
//    }
//
//    fun getAllMovie(): LiveData<ApiResponse<List<Movies>>> {
//        val resultData = MutableLiveData<ApiResponse<List<Movies>>>()
//
//        //get data from remote api
//        val client = apiService.getMoviePopular()
//
//        client.enqueue(object : Callback<MovieResponse> {
//            override fun onResponse(
//                call: Call<MovieResponse>,
//                response: Response<MovieResponse>
//            ) {
//                val dataArray = response.body()?.movies
//                resultData.value = if (dataArray != null) ApiResponse.Success(dataArray) else ApiResponse.Empty
//            }
//
//            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
//                resultData.value = ApiResponse.Error(t.message.toString())
//                Log.e("RemoteDataSource", t.message.toString())
//            }
//        })
//
//        return resultData
//    }
//}



//    fun getMovie(): LiveData<ApiResponse<List<MovieResponse>>>{
//        val resultData = MutableLiveData<ApiResponse<List<MovieResponse>>>()
//
//
//        val client = apiService.getMoviePopular()
//
//        client.enqueue(object : Callback<MovieResponse>{
//            override fun onResponse(call: Call<MovieResponse>, response: Response<MovieResponse>) {
//                val data = response.body()
//                resultData.value  = if (data != null) ApiResponse.Success(data) else ApiResponse.Empty
//
//            }
//
//            override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }




//old RemoteDataSource
//class RemoteDataSource(private val apiService: ApiService) {
//    suspend fun moviePopular() = apiService.getMoviePopular()
//}
