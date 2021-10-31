package com.solvro.spaceflightnews.ui.di

import com.solvro.spaceflightnews.data.ApiClient
import com.solvro.spaceflightnews.data.Repository
import com.solvro.spaceflightnews.ui.viewmodel.MainViewModel
import com.solvro.spaceflightnews.utils.Preferences
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object MainModule {
    fun create() = module {
        single {
            Preferences(androidContext())
        }

        single {
            MainViewModel(
                repository = Repository(ApiClient.ARTICLES_SERVICE),
                preferences = get(),
                dispatcherIO = Dispatchers.IO
            )
        }
    }
}
