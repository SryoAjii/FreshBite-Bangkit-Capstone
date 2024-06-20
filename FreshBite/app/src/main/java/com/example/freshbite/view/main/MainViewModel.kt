package com.example.freshbite.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserModel
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.response.ArticlesResponseItem

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _newsItem = MutableLiveData<List<ArticlesResponseItem>>()
    private val _filteredArticles = MutableLiveData<List<ArticlesResponseItem>>()

    val newsItem: LiveData<List<ArticlesResponseItem>> get() = _filteredArticles

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        getArticles().observeForever { result ->
            if (result is StateResult.Success) {
                _newsItem.postValue(result.data)
                _filteredArticles.postValue(result.data)
                Log.d("Fetch", "Fetched articles size: ${result.data.size}")
            }
        }
    }

    fun filterArticlesByTag(tag: String) {
        _newsItem.value?.let { articles ->
            val filteredArticles = if (tag == "tidak ada") {
                articles
            } else {
                articles.filter { it.tag == tag }
            }
            _filteredArticles.postValue(filteredArticles)
            Log.d("Filter", "Filtered articles size: ${filteredArticles.size}")
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun searchArticles(title: String) = repository.searchArticles(title)

    fun getArticles() = repository.getArticles()
}