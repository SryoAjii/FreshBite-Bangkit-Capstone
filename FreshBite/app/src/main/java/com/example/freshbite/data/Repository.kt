package com.example.freshbite.data

import androidx.lifecycle.liveData
import com.example.freshbite.data.pref.UserModel
import com.example.freshbite.data.pref.UserPreference
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.api.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import retrofit2.awaitResponse

class Repository private constructor(
    private val userPreference: UserPreference,
    private var apiService: ApiService,
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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

    fun firebaseRegister(username: String, email: String, password: String) = liveData {
        emit(StateResult.Loading)
        try {
            val registerResult = firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }
                        user?.updateProfile(profileUpdates)
                    }
                }
            emit(StateResult.Success(registerResult))
        } catch (e: Exception){
            emit(StateResult.Error("Registrasi Gagal"))
        }
    }

    fun firebaseLogin(email: String, password: String) = liveData {
        emit(StateResult.Loading)
        try {
            val loginResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val token = firebaseAuth.currentUser?.uid
            emit(StateResult.Success(Pair(loginResult, token)))
        } catch (e: Exception) {
            emit(StateResult.Error("Login gagal, periksa kembali username dan password!"))
        }
    }

    fun firebaseLoginAnonymously() = liveData {
        emit(StateResult.Loading)
        try {
            val loginResult = firebaseAuth.signInAnonymously().await()
            val token = firebaseAuth.currentUser?.uid
            emit(StateResult.Success(Pair(loginResult, token)))
        } catch (e: Exception) {
            emit(StateResult.Error("Login sebagai Guest gagal!"))
        }
    }

    fun getUserDetail() = liveData {
        emit(StateResult.Loading)
        try {
            val currentUser = firebaseAuth.currentUser
            val username = currentUser?.displayName.toString()
            val email = currentUser?.email.toString()
            emit(StateResult.Success(Pair(email, username)))
        } catch (e: Exception) {
            emit(StateResult.Error("Gagal Memuat Informasi User"))
        }
    }

    fun firebaseLogout() = liveData {
        emit(StateResult.Loading)
        try {
            val logoutResponse = firebaseAuth.signOut()
            emit(StateResult.Success(logoutResponse))
        } catch (e: Exception) {
            emit(StateResult.Error("Logout gagal"))
        }
    }

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
            userPreference: UserPreference, apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService)
            }.also { instance = it }
    }
}