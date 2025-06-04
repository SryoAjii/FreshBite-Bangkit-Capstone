package com.example.freshbite.retrofit.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val content: String,
    val image: String,
    val link: String,
    val tag: String,
    val title: String
) : Parcelable
