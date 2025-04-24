package com.trevin.newsapp.data

import androidx.annotation.DrawableRes

data class NewsArticle(
    @DrawableRes val image: Int,
    val title: String,
    val author: String,
    val content: String,
)
