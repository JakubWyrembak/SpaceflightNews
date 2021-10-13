package com.example.spaceflightnews.data

class Repository(private val service: ArticlesService) {

    suspend fun getArticles(limit: Int) = service.fetchArticles(limit)

}