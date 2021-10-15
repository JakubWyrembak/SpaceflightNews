package com.example.spaceflightnews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.data.ApiClient
import com.example.spaceflightnews.data.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository = Repository(ApiClient.ARTICLES_SERVICE)) :
    ViewModel() {

    private var _articles = MutableLiveData<MainViewState>()
    val articles: LiveData<MainViewState>
        get() = _articles

    init {
        _articles.postValue(MainViewState.Loading)
        fetchArticles()
    }

    fun onRefresh() {
        fetchArticles()
    }

    private fun fetchArticles() {
        viewModelScope.launch(Dispatchers.IO) {
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
    }

    fun getHistoryArticles() = UserData.history
    fun getFavoriteArticles() = UserData.favorites

    /*fun getHistoryArticles(): MutableList<Article>? {
    val historyArticles = mutableListOf<Article>()
    viewModelScope.launch(Dispatchers.IO) {
        try {
            UserData.history.forEach {
                historyArticles.add(repository.getArticle(it))
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
                _articles.postValue(MainViewState.Error(e.message))
            }
        }
    }
    return historyArticles
}*/

    companion object {
        private const val TAG = "MainViewModel"
    }
}