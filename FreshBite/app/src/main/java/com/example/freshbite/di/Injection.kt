package com.example.freshbite.di

import android.content.Context
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserPreference
import com.example.freshbite.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        return Repository.getInstance(pref)
    }
}