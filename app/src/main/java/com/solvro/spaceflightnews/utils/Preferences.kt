package com.solvro.spaceflightnews.utils

import android.content.Context
import android.content.SharedPreferences

private const val PREFERENCES_KEY = "preferences"
const val FAVORITES_ID = "favorites"
const val HISTORY_ID = "history"

class Preferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE)

    fun getArticles(key: String): List<Int> =
        preferences.getString(key, "")?.let {
            getArticlesIds(it)
        } ?: run {
            listOf()
        }

    fun saveArticles(articles: List<Int>, key: String) {
        preferences.edit().apply {
            putString(key, articles.toString())
            apply()
        }
    }

    private fun getArticlesIds(articles: String): List<Int> =
        articles.removeSurrounding("[", "]")
            .split(", ")
            .mapNotNull {
                it.toIntOrNull()
            }

    companion object {
        private const val TAG = "Preferences"
    }
}
