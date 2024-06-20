package com.example.freshbite.retrofit.api

import com.example.freshbite.retrofit.response.ArticlesResponseItem
import com.example.freshbite.retrofit.response.LoginResponse
import com.example.freshbite.retrofit.response.LogoutResponse
import com.example.freshbite.retrofit.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("api/auth/signin")
    suspend fun login (
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @FormUrlEncoded
    @POST("api/auth/signup")
    suspend fun register (
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @GET("api/articles")
    suspend fun getArticles(): List<ArticlesResponseItem>

    @FormUrlEncoded
    @POST("api/articles/search")
    suspend fun searchArticle(
        @Field("title") title: String
    ): List<ArticlesResponseItem>

    @POST("api/auth/signout")
    suspend fun Logout(): LogoutResponse
}