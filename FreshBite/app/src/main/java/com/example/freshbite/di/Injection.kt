package com.example.freshbite.di

import android.content.Context
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserPreference
import com.example.freshbite.data.pref.dataStore
import com.example.freshbite.retrofit.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return Repository.getInstance(pref, apiService)
    }
}