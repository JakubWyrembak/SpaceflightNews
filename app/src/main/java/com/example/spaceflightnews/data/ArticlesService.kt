package com.example.spaceflightnews.data

import com.example.spaceflightnews.model.Article
import retrofit2.http.GET

interface ArticlesService {
    @GET("articles")
    suspend fun fetchArticles(): List<Article>
}