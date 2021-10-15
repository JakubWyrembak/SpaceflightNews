package com.example.spaceflightnews

import com.example.spaceflightnews.model.Article

// TODO sprobować jeszcze na mutableListOf<Int>, bo to jednak lepsze
object UserData {
    var history = mutableListOf<Article>()
    var favorites = mutableListOf<Article>()

    fun addToHistory(article: Article) {
        if (article in history) {
            history.remove(article)
        } else {
            history.add(0, article)
        }
    }

    fun onFavoriteButtonClicked(article: Article) {
        if (article.isFavorite) {
            favorites.remove(article)
        } else {
            favorites.add(article)
        }
        article.isFavorite = article.isFavorite.not()
    }
}