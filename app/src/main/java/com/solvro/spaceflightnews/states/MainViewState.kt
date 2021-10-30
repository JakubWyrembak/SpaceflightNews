package com.solvro.spaceflightnews.states

import com.solvro.spaceflightnews.model.Article

sealed class MainViewState {
    data class Success(val data: List<Article>) : MainViewState()

    object Loading : MainViewState()

    data class Error(val message: String?) : MainViewState()
}
