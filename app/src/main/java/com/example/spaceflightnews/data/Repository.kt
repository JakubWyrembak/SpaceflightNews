package com.example.spaceflightnews.data

class Repository(private val service: ArticlesService) {

    suspend fun getArticles(limit: Int) = service.fetchArticles(limit)

    suspend fun getArticle(articleId: Int) = service.fetchArticle(articleId)

}