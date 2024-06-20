package com.example.freshbite.data

import androidx.lifecycle.liveData
import com.example.freshbite.data.pref.UserModel
import com.example.freshbite.data.pref.UserPreference
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.api.ApiService
import com.example.freshbite.retrofit.response.LoginResponse
import com.example.freshbite.retrofit.response.LogoutResponse
import com.example.freshbite.retrofit.response.PredictResponse
import com.example.freshbite.retrofit.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class Repository private constructor(
    private val userPreference: UserPreference, private var apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun userLogout() = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.Logout()
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, LogoutResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun userLogin(email: String, password: String) = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.login(email, password)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, LoginResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun userRegister(name: String, email: String, password: String) = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.register(name, email, password)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, RegisterResponse::class.java)
            emit(responseError.message?.let { StateResult.Error(it) })
        }
    }

    fun searchArticles(title: String) = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.searchArticle(title)
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            emit(StateResult.Error("error"))
        }
    }

    fun getArticles() = liveData {
        emit(StateResult.Loading)
        try {
            val responseSuccess = apiService.getArticles()
            emit(StateResult.Success(responseSuccess))
        } catch (e: HttpException) {
            emit(StateResult.Error("error"))
        }
    }

    fun getToken(apiService: ApiService) {
        this.apiService = apiService
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}