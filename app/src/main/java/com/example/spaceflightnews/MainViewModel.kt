package com.example.spaceflightnews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.data.Repository
import com.example.spaceflightnews.model.Article
import com.example.spaceflightnews.utils.FAVORITES_ID
import com.example.spaceflightnews.utils.HISTORY_ID
import com.example.spaceflightnews.utils.Preferences
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
    private var _historyArticles = MutableLiveData<ArrayList<Article>>()
    val historyArticles: LiveData<ArrayList<Article>>
        get() = _historyArticles

    // Favorite screen
    private var _favoriteArticles = MutableLiveData<ArrayList<Article>>()
    val favoriteArticles: LiveData<ArrayList<Article>>
        get() = _favoriteArticles

    init {
        _articles.postValue(MainViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            fetchArticles()

            _historyArticles.postValue(arrayListOf())
            loadHistoryArticles()

            _favoriteArticles.postValue(arrayListOf())
            loadFavorites()
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchArticles()
        }
    }

    private suspend fun fetchArticles() {
        try {
            val data = repository.getArticles(100)
            _articles.postValue(MainViewState.Success(data))
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
                _articles.postValue(MainViewState.Error(e.message))
            }
        }

    }

    private suspend fun loadHistoryArticles() {
        UserData.history.forEach {
            Log.v(TAG, "Wstepnie historia")
            Log.v(TAG, "historia -> $it")
            loadSingleArticle(it, historyArticles.value)
        }
    }

    private suspend fun loadSingleArticle(id: Int, data: ArrayList<Article>?) {
        try {
            val currArticle = repository.getArticle(id)
            data?.add(0, currArticle)
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
        }
    }

    // TODO jedna funkcja z tych dwóch
    suspend fun addToHistory(id: Int) {
        if (id in UserData.history) {
            UserData.history.remove(id)
            _historyArticles.value?.removeIf { it.id == id }
        }
        UserData.history.add(id)
        loadSingleArticle(id, historyArticles.value)
    }

    suspend fun addOrRemoveFavorite(id: Int) {
        if (id in UserData.favorites) {
            Log.v(TAG, "USUWAM")
            _favoriteArticles.value?.removeIf { it.id == id }
        } else {
            Log.v(TAG, "DODAJE")
            loadSingleArticle(id, favoriteArticles.value)
        }
        UserData.addOrRemoveFavorite(id)
    }

    suspend fun loadFavorites() {
        UserData.favorites.forEach {
            Log.v(TAG, "wstepnie ulubione $it")

            Log.v(TAG, "fav -> $it")
            loadSingleArticle(it, favoriteArticles.value)

        }
    }

    suspend fun loadPreferences() {
        val articlesFav = preferences.getArticles(FAVORITES_ID)
        Log.v(TAG, "$articlesFav")
        articlesFav.map {
            Log.v(TAG, "Mapuje fav -> $it")
            addOrRemoveFavorite(it)
        }
        preferences.getArticles(HISTORY_ID).map {
            addToHistory(it)
        }
        Log.v(TAG, "Preferences loaded ${UserData.favorites}, ")
    }

    fun savePreferences() {
        Log.v(TAG, "SAVING ${UserData.favorites} ${UserData.history}")
        preferences.saveArticles(UserData.favorites, FAVORITES_ID)
        preferences.saveArticles(UserData.history, HISTORY_ID)
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}