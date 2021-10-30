package com.solvro.spaceflightnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvro.spaceflightnews.data.Repository
import com.solvro.spaceflightnews.model.Article
import com.solvro.spaceflightnews.states.ArticlesMode
import com.solvro.spaceflightnews.states.MainViewState
import com.solvro.spaceflightnews.states.UserData
import com.solvro.spaceflightnews.utils.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val preferences: Preferences,
) :
    ViewModel() {

    // Main screen
    private var _articles = MutableLiveData<MainViewState>()
    val articles: LiveData<MainViewState>
        get() = _articles

    // History screen
    private var _historyArticles = MutableLiveData<MainViewState>()
    val historyArticles: LiveData<MainViewState>
        get() = _historyArticles

    // Favorite screen
    private var _favoriteArticles = MutableLiveData<MainViewState>()
    val favoriteArticles: LiveData<MainViewState>
        get() = _favoriteArticles

    private var timesLoaded = 0

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchArticles()
            loadUserDataArticles()
        }
    }

    suspend fun fetchArticles() {
        val data: MutableList<Article> = if(articles.value is MainViewState.Success) {
            (articles.value as MainViewState.Success).data.toMutableList()
        }  else{
            mutableListOf()
        }
        _articles.postValue(MainViewState.Loading)
        try {
            data += repository.getArticles(timesLoaded* ARTICLES_AT_START, ARTICLES_AT_START*(++timesLoaded))
            _articles.postValue(MainViewState.Success(data.distinct()))
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
                _articles.postValue(MainViewState.Error(e.message))
            }
        }
    }

    private suspend fun loadUserDataArticles() {
        loadHistoryArticles()
        loadFavoriteArticles()
    }


    private suspend fun loadHistoryArticles() {
        if (UserData.history.isEmpty()) {
            _historyArticles.postValue(MainViewState.Success(listOf()))
        } else {
            _historyArticles.postValue(MainViewState.Loading)
            val articles = fetchArticlesByIds(UserData.history
                .map { it.toString() })
                .sortedBy {
                    UserData.history.indexOf(it.id)
                }

            _historyArticles.postValue(MainViewState.Success(articles))
        }
    }

    private suspend fun loadFavoriteArticles() {
        if (UserData.favorites.isEmpty()) {
            _favoriteArticles.postValue(MainViewState.Success(listOf()))
        } else {
            _favoriteArticles.postValue(MainViewState.Loading)
            val articles = fetchArticlesByIds(UserData.favorites
                .map { it.toString() })
                .sortedBy {
                    UserData.favorites.indexOf(it.id)
                }

            _favoriteArticles.postValue(MainViewState.Success(articles))
        }
    }

    private suspend fun fetchArticlesByIds(ids: List<String>) =
        try {
            repository.getArticlesById(ids)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
            listOf()
        }

    private suspend fun loadSingleArticle(id: Int, data: MutableList<Article>) {
        try {
            val currArticle = repository.getArticle(id)
            data.add(0, currArticle)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
        }
    }


    suspend fun addToHistory(id: Int) {
        val currData = getCurrentData(historyArticles)

        if (id in UserData.history) {
            UserData.history.remove(id)
            currData.removeIf {
                it.id == id
            }
        }

        if(UserData.history.size > HISTORY_MAX_SIZE){
            UserData.history.removeLast()
        }

        UserData.history.add(0, id)
        loadSingleArticle(id, currData)
        _historyArticles.postValue(MainViewState.Success(currData))
    }


    suspend fun addOrRemoveFavorite(id: Int) {
        val currData = getCurrentData(favoriteArticles)

        if (id in UserData.favorites) {
            UserData.favorites.remove(id)
            currData.removeIf {
                it.id == id
            }
        } else {
            UserData.favorites.add(0, id)
            loadSingleArticle(id, currData)
        }

        _favoriteArticles.postValue(MainViewState.Success(currData))
    }

    private fun getCurrentData(articles: LiveData<MainViewState>) =
        if (articles.value is MainViewState.Success) {
            (articles.value as MainViewState.Success).data.toMutableList()
        } else {
            mutableListOf()
        }

    suspend fun onRefresh(currentMode: ArticlesMode) {
        when (currentMode) {
            ArticlesMode.MAIN -> {
                timesLoaded = 0
                fetchArticles()
            }
            ArticlesMode.HISTORY -> loadHistoryArticles()
            ArticlesMode.FAVORITES -> loadFavoriteArticles()
        }
    }

    suspend fun getSearchedArticles(query: String, currentMode: ArticlesMode): List<Article> {
        return try {
            when (currentMode) {
                ArticlesMode.MAIN -> repository.getSearchedArticles(query)
                ArticlesMode.FAVORITES -> getUserDataQueryArticles(favoriteArticles, query)
                ArticlesMode.HISTORY -> getUserDataQueryArticles(historyArticles, query)
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
            listOf()
        }
    }

    private fun getUserDataQueryArticles(
        articles: LiveData<MainViewState>, query: String
    ): List<Article> {
        return if (articles.value is MainViewState.Success) {
            (articles.value as MainViewState.Success).data.filter {
                it.title.contains(query, true)
            }
        } else {
            listOf()
        }
    }


    // PREFERENCES  ==============================================================
    fun savePreferences() {
        preferences.saveArticles(UserData.favorites, FAVORITES_ID)
        preferences.saveArticles(UserData.history, HISTORY_ID)
    }

    fun loadPreferences() {
        preferences.getArticles(HISTORY_ID).map {
            UserData.history.add(it)
        }

        preferences.getArticles(FAVORITES_ID).map {
            UserData.favorites.add(it)
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}