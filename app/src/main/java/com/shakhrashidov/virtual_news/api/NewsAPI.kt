package com.shakhrashidov.virtual_news.api

import android.provider.Contacts.SettingsColumns.KEY
import com.shakhrashidov.virtual_news.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query



interface NewsAPI {

    @GET("v2/top-headlines")

    @Headers(
        "Accept: application/json",
        "Authorization: 6910d9434a2440329e86f27f9d4d35e2"
    )
    suspend fun getBreakingNews(


            @Query("country")
            countryCode:String = "india",
            @Query("page")
            pageCount:Int = 1,
            @Query("X-Api-Key")
            key: String = KEY,
            @Query("category")
            category:String? = null
    ):Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchNews(
            @Query("q")
            query:String ,
            @Query("page")
            pageCount:Int = 1,
            @Query("X-Api-Key")
            key: String = KEY
    ):Response<NewsResponse>
}