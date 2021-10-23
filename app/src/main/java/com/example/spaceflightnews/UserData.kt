package com.example.spaceflightnews

object UserData {
    var history = mutableListOf<Int>()
    var favorites = mutableListOf<Int>()

    /*fun addOrRemoveFavorite(articleId: Int) {
        if (articleId in favorites) {
            favorites.remove(articleId)
        } else {
            favorites.add(articleId)
        }
    }*/
}