package com.example.freshbite.retrofit.response

import com.google.gson.annotations.SerializedName

data class ArticlesResponseItem(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("link")
    val link: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("tag")
    val tag: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("content")
    val content: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)