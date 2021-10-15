package com.example.spaceflightnews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceflightnews.data.ApiClient
import com.example.spaceflightnews.data.Repository
import com.example.spaceflightnews.model.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository = Repository(ApiClient.ARTICLES_SERVICE)) :
    ViewModel() {

    private var _articles = MutableLiveData<MainViewState>()
    val articles: LiveData<MainViewState>
        get() = _articles

    private var _historyArticles = MutableLiveData<ArrayList<Article>>()
    val historyArticles: LiveData<ArrayList<Article>>
        get() = _historyArticles

    init {
        _articles.postValue(MainViewState.Loading)
        fetchArticles()

        _historyArticles.postValue(arrayListOf())
        loadHistoryArticles()
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

    fun getFavoriteArticles() = UserData.favorites

    private fun loadHistoryArticles() {
        UserData.historyIds.forEach {
            viewModelScope.launch {
                loadSingleArticle(it)
            }
        }
    }

    private suspend fun loadSingleArticle(id: Int) {
        try {
            val currArticle = repository.getArticle(id)
            _historyArticles.value?.add(0, currArticle)
        } catch (e: Exception) {
            e.message?.let {
                Log.e(TAG, it)
            }
        }
    }

    suspend fun addToHistory(id: Int) {
        if (id in UserData.historyIds) {
            UserData.historyIds.remove(id)
            _historyArticles.value?.removeIf { it.id == id }
        }
        UserData.historyIds.add(id)
        loadSingleArticle(id)
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}