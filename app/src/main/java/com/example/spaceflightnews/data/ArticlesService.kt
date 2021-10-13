package com.example.spaceflightnews.data

import com.example.spaceflightnews.model.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesService {
    @GET("articles")
    suspend fun fetchArticles(@Query("_limit")limit: Int): List<Article>
}