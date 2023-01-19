package com.shakhrashidov.virtual_news.repository

import android.content.Context
import com.shakhrashidov.virtual_news.api.RetrofitInstance
import com.shakhrashidov.virtual_news.database.ArticleDatabase
import com.shakhrashidov.virtual_news.model.Article

class NewsRepository (val context: Context,private val db:ArticleDatabase){

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun getCategoryNews(countryCode: String, pageNumber: Int, category: String) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber,category = category)

    suspend fun searchNews(searchQuery:String,pageNumber:Int) =
            RetrofitInstance.api.searchNews(searchQuery,pageNumber)

    suspend fun saveArticle(article:Article) = db.getArticleDao().save(article)
    suspend fun deleteAricle(article:Article) = db.getArticleDao().deleteArticle(article)

    suspend fun isArticleSaved(article: Article) = db.getArticleDao().getArticleByTitle(article.title)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteAllArticles() = db.getArticleDao().deleteAllArticle()

    fun getDataStore() = DataStoreRepository(context)

}