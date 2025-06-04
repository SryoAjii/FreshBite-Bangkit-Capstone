package com.example.freshbite.di

import com.example.freshbite.data.Repository
import com.example.freshbite.retrofit.api.ApiConfig

object Injection {
    fun provideRepository(): Repository {
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(apiService)
    }
}