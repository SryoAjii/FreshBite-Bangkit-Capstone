package com.example.freshbite.data

import androidx.lifecycle.liveData
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.api.ApiService
import retrofit2.HttpException
import retrofit2.awaitResponse

class Repository private constructor(
    private var apiService: ApiService,
) {

    fun getArticle() = liveData {
        emit(StateResult.Loading)
        try {
            val response = apiService.getArticle().awaitResponse()
            val body = response.body() ?: emptyMap()
            emit(StateResult.Success(body))
        } catch (e: HttpException) {
            emit(StateResult.Error("error"))
        }
    }

    fun searchArticle(query: String) = liveData {
        emit(StateResult.Loading)
        try {
            val response = apiService.getArticle().awaitResponse()
            val body = response.body() ?: emptyMap()
            val search = body.filter {
                it.value.title.contains(query, ignoreCase = true)
            }
            emit(StateResult.Success(search))
        } catch (e: HttpException) {
            emit(StateResult.Error("error"))
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(apiService)
            }.also { instance = it }
    }
}