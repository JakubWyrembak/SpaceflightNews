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
import java.lang.Exception

class MainViewModel(private val repository: Repository = Repository(ApiClient.ARTICLES_SERVICE)) : ViewModel() {

    private var _articles = MutableLiveData<MainViewState>()
    val articles: LiveData<MainViewState>
        get() = _articles

    init {
        _articles.postValue(MainViewState.Loading)
        fetchArticles()
    }

    fun onRefresh(){
        fetchArticles()
    }

    private fun fetchArticles() {
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val data = repository.getArticles(100)
                _articles.postValue(MainViewState.Success(data))
            }catch (e: Exception){
                Log.e(TAG, e.message.toString())
                _articles.postValue(MainViewState.Error(e.message.toString()))
            }
        }
    }

    companion object{
        private const val TAG = "MainViewModel"
    }
}