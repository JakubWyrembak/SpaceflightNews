package com.example.spaceflightnews.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.states.ArticlesModes
import com.example.spaceflightnews.states.MainViewState
import com.example.spaceflightnews.states.UserData
import com.example.spaceflightnews.data.Repository
import com.example.spaceflightnews.model.Article
import com.example.spaceflightnews.utils.FAVORITES_ID
import com.example.spaceflightnews.utils.HISTORY_ID
import com.example.spaceflightnews.utils.Preferences
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

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchArticles()
        }
    }

    suspend fun loadUserDataArticles() {
        loadHistoryArticles()
        loadFavoriteArticles()
    }

    private suspend fun fetchArticles() {
        _articles.postValue(MainViewState.Loading)
        try {
            val data = repository.getArticles(25)
            _articles.postValue(MainViewState.Success(data))
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
                _articles.postValue(MainViewState.Error(e.message))
            }
        }

    }

    private suspend fun loadHistoryArticles() {
        _historyArticles.postValue(MainViewState.Loading)
        val articles = mutableListOf<Article>()

        UserData.history.forEach {
            loadSingleArticle(it, articles)
        }

        _historyArticles.postValue(MainViewState.Success(articles))
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

    // TODO jedna funkcja z tych dwóch
    // TODO żeby nie było za dużo
    suspend fun addToHistory(id: Int) {
        val currData: MutableList<Article>
        if (historyArticles.value is MainViewState.Success) {
            currData = (historyArticles.value as MainViewState.Success).data as MutableList<Article>

            _historyArticles.postValue(MainViewState.Success(currData))
        } else {
            currData = mutableListOf()
        }

        if (id in UserData.history) {
            UserData.history.remove(id)
            currData.removeIf {
                it.id == id
            }
        }

        UserData.history.add(id)
        loadSingleArticle(id, currData)
        _historyArticles.postValue(MainViewState.Success(currData))
    }

    suspend fun addOrRemoveFavorite(id: Int) {
        val currData: MutableList<Article> =
            if (favoriteArticles.value is MainViewState.Success) {
                (favoriteArticles.value as MainViewState.Success).data as MutableList<Article>
            } else {
                mutableListOf()
            }

        if (id in UserData.favorites) {
            UserData.favorites.remove(id)
            currData.removeIf {
                it.id == id
            }
            _favoriteArticles.postValue(MainViewState.Success(currData))
        } else {
            UserData.favorites.add(id)
            loadSingleArticle(id, currData)
            _favoriteArticles.postValue(MainViewState.Success(currData))
        }
    }

    private suspend fun loadFavoriteArticles() {
        _favoriteArticles.postValue(MainViewState.Loading)
        val articles = mutableListOf<Article>()
        UserData.favorites.forEach {
            loadSingleArticle(it, articles)
        }

        _favoriteArticles.postValue(MainViewState.Success(articles))
    }

    suspend fun onRefresh(currentMode: ArticlesModes) {
        when (currentMode) {
            ArticlesModes.MAIN -> fetchArticles()
            ArticlesModes.HISTORY -> loadHistoryArticles()
            ArticlesModes.FAVORITES -> loadFavoriteArticles()
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