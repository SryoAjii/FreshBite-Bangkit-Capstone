package com.example.freshbite.view.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultArticle(
    val title : String,
    val description: String,
    val imgUrl: String,
    val articleUrl: String
) : Parcelable
