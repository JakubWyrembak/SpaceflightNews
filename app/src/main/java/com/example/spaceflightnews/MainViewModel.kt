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
        _articles.postValue(MainViewState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            fetchArticles()
        }
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchArticles()
            loadPreferences()
        }
    }

    suspend fun loadUserDataArticles(){
        loadHistoryArticles()
        loadFavoriteArticles()
    }

    private suspend fun fetchArticles() {
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

    suspend fun loadHistoryArticles() {
        _historyArticles.postValue(MainViewState.Loading)
        val articles = mutableListOf<Article>()

        UserData.history.forEach {
            Log.v(TAG, "Historia laduje dane -> $it")
            loadSingleArticle(it, articles)
        }

        _historyArticles.postValue(MainViewState.Success(articles))
    }

    private suspend fun loadSingleArticle(id: Int, data: MutableList<Article>) {
        try {
            Log.v(TAG, "Probuje dodac $id")
            val currArticle = repository.getArticle(id)
            data.add(0, currArticle)
        } catch (ex: CancellationException) {
            Log.e(TAG, "CANCELLATION ${ex.message}")
            throw ex // Must let the CancellationException propagate
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
        val currData: MutableList<Article>
        if (favoriteArticles.value is MainViewState.Success) {
            currData =
                (favoriteArticles.value as MainViewState.Success).data as MutableList<Article>
        } else {
            currData = mutableListOf()
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
            Log.v(TAG, "Ulubione laduje dane -> $it")
            loadSingleArticle(it, articles)
        }

        _favoriteArticles.postValue(MainViewState.Success(articles))
    }

    /*fun onFavoritesClick(){
        _favoriteArticles.postValue(arrayListOf())
        Log.v(TAG, "onFavoritesClick")
        viewModelScope.launch {
            Log.v(TAG, "Wczytuje")
            loadFavorites()
        }
    }*/

    fun addToUserDataHistory(articleId: Int) {
        if (articleId in UserData.history) {
            UserData.history.removeIf {
                it == articleId
            }
        }
        UserData.history.add(articleId)
    }


    // PREFERENCES  ==============================================================
    fun savePreferences() {
        Log.v(TAG, "SAVING ${UserData.favorites} ${UserData.history}")
        preferences.saveArticles(UserData.favorites, FAVORITES_ID)
        preferences.saveArticles(UserData.history, HISTORY_ID)
    }

    suspend fun loadPreferences() {
        preferences.getArticles(HISTORY_ID).map {
            UserData.history.add(it)
        //addToHistory(it)
        }

        preferences.getArticles(FAVORITES_ID).map {
            UserData.favorites.add(it)
            //addOrRemoveFavorite(it)
        }

        Log.v(TAG, "Preferences loaded ${UserData.favorites}, ${UserData.history}")
    }


    companion object {
        private const val TAG = "MainViewModel"
    }
}