package com.example.spaceflightnews.states

import com.example.spaceflightnews.model.Article

sealed class MainViewState {
    data class Success(val data: List<Article>) : MainViewState()

    object Loading : MainViewState()

    data class Error(val message: String?) : MainViewState()
}
