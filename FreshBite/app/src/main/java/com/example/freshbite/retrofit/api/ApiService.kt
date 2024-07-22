package com.example.freshbite.retrofit.api

import com.example.freshbite.retrofit.response.Article
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("article.json")
    fun getArticle(): Call<Map<String, Article>>
}