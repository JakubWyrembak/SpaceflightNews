package com.solvro.spaceflightnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solvro.spaceflightnews.data.Repository
import com.solvro.spaceflightnews.model.Article
import com.solvro.spaceflightnews.states.ArticlesMode
import com.solvro.spaceflightnews.states.ArticlesMode.*
import com.solvro.spaceflightnews.states.MainViewState
import com.solvro.spaceflightnews.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val preferences: Preferences,
    dispatcherIO: CoroutineDispatcher
) : ViewModel() {

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
        viewModelScope.launch(dispatcherIO) {
            fetchArticles()
        }
    }

    suspend fun fetchArticles() {
        val data: MutableList<Article> = getCurrentData(articles)

        _articles.postValue(MainViewState.Loading)
        try {
            data += repository.getArticles(
                getFirstFetchingIndex(),
                getLastFetchingIndex()
            )

            _articles.postValue(MainViewState.Success(data.distinct()))
        } catch (e: Exception) {
            e.log()
            _articles.postValue(MainViewState.Error(e.message))
        }
    }

    suspend fun loadBothUserDataArticles() {
        loadUserDataArticles(UserData.favorites, _favoriteArticles)
        loadUserDataArticles(UserData.history, _historyArticles)
    }

    private suspend fun loadUserDataArticles(
        userDataList: MutableList<Int>,
        viewState: MutableLiveData<MainViewState>
    ) {
        if (userDataList.isEmpty()) {
            viewState.postValue(MainViewState.Success(listOf()))
        } else {
            viewState.postValue(MainViewState.Loading)

            val articles = getArticlesByIds(userDataList
                .map { it.toString() })
                .sortedBy {
                    userDataList.indexOf(it.id)
                }

            viewState.postValue(MainViewState.Success(articles))
        }
    }

    private suspend fun getArticlesByIds(ids: List<String>) =
        try {
            repository.getArticlesById(ids)
        } catch (e: Exception) {
            e.log()
            listOf()
        }

    private suspend fun getSingleArticle(id: Int): Article {
        try {
            return repository.getArticle(id)
        } catch (e: Exception) {
            e.log()
            throw e
        }
    }

    suspend fun addToHistory(id: Int) {
        val currData = getCurrentData(historyArticles)

        if (id in UserData.history) {
            UserData.history.remove(id)
            currData.removeIf { article ->
                article.id == id
            }
        }

        if (UserData.history.size > HISTORY_MAX_SIZE) {
            UserData.history.removeLast()
        }

        UserData.history.add(0, id)
        currData.add(0, getSingleArticle(id))
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
            currData.add(0, getSingleArticle(id))
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
            MAIN -> {
                timesLoaded = 0
                fetchArticles()
            }
            HISTORY -> loadUserDataArticles(UserData.history, _historyArticles)
            FAVORITES -> loadUserDataArticles(UserData.favorites, _favoriteArticles)
        }
    }

    suspend fun getSearchedArticles(query: String, currentMode: ArticlesMode): List<Article> {
        return try {
            when (currentMode) {
                MAIN -> repository.getSearchedArticles(query)
                FAVORITES -> getUserDataQueryArticles(favoriteArticles, query)
                HISTORY -> getUserDataQueryArticles(historyArticles, query)
            }
        } catch (e: Exception) {
            e.log()
            listOf()
        }
    }

    private fun getUserDataQueryArticles(
        articles: LiveData<MainViewState>,
        query: String
    ): List<Article> {
        return if (articles.value is MainViewState.Success) {
            (articles.value as MainViewState.Success).data.filter {
                it.title.contains(query, true)
            }
        } else {
            listOf()
        }
    }

    fun savePreferences() {
        with(preferences) {
            saveArticles(UserData.favorites, FAVORITES_ID)
            saveArticles(UserData.history, HISTORY_ID)
        }
    }

    fun loadPreferences() {
        with(preferences) {
            getArticles(HISTORY_ID).map {
                UserData.history.add(it)
            }

            getArticles(FAVORITES_ID).map {
                UserData.favorites.add(it)
            }
        }
    }

    private fun getLastFetchingIndex() = ARTICLES_AT_START * (++timesLoaded)

    private fun getFirstFetchingIndex() = timesLoaded * ARTICLES_AT_START

    private fun Exception.log() {
        this.message?.let {
            Log.e(TAG, it)
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}