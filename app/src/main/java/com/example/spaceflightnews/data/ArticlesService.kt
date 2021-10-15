package com.example.spaceflightnews.data

import com.example.spaceflightnews.model.Article
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesService {
    @GET("articles")
    suspend fun fetchArticles(@Query("_limit")limit: Int): List<Article>

    // TODO chyba bez tego bedzie
    @GET("articles")
    suspend fun fetchArticle(@Query("_id")articleId: Int): Article
}