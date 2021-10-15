package com.example.spaceflightnews

import com.example.spaceflightnews.model.Article

object UserData {
    var historyIds = mutableListOf<Int>()
    var favorites = mutableListOf<Article>()

    fun onFavoriteButtonClicked(article: Article) {
        if (article.isFavorite) {
            favorites.remove(article)
        } else {
            favorites.add(article)
        }
        article.isFavorite = article.isFavorite.not()
    }
}