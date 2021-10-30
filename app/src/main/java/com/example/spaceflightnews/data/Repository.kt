package com.example.spaceflightnews.data

class Repository(private val service: ArticlesService) {

    suspend fun getArticles(start: Int, limit: Int) = service.fetchArticles(start, limit)

    suspend fun getArticle(articleId: Int) = service.fetchArticle(articleId)

    suspend fun getArticlesById(ids: List<String>) = service.fetchArticlesById(ids)

    suspend fun getSearchedArticles(searchValue: String) =
        service.fetchSearchedArticles(searchValue)
}