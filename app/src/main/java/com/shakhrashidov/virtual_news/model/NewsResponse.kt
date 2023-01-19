package com.shakhrashidov.virtual_news.model

data class NewsResponse(
        var articles: MutableList<Article>,
        val status: String,
        val totalResults: Int
)