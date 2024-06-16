package com.example.freshbite.di

import android.content.Context
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserPreference
import com.example.freshbite.data.pref.dataStore
import com.example.freshbite.retrofit.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(pref, apiService)
    }
}