package com.example.freshbite.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.freshbite.data.Repository
import com.example.freshbite.data.pref.UserModel
import com.example.freshbite.di.StateResult
import com.example.freshbite.retrofit.response.Article

class MainViewModel(private val repository: Repository) : ViewModel() {

    fun getArticle() = repository.getArticle()
    fun searchArticle(query: String) = repository.searchArticle(query)

    private val _newsItem = MutableLiveData<Map<String, Article>>()
    private val _filteredArticles = MutableLiveData<Map<String, Article>>()

    val newsItem: LiveData<Map<String, Article>> get() = _filteredArticles

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        getArticle().observeForever { result ->
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
                articles.filter { it.value.tag == tag }
            }
            _filteredArticles.postValue(filteredArticles)
            Log.d("Filter", "Filtered articles size: ${filteredArticles.size}")
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}