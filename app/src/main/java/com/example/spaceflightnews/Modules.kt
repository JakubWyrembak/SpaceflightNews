package com.example.spaceflightnews

import com.example.spaceflightnews.data.ApiClient
import com.example.spaceflightnews.data.Repository
import com.example.spaceflightnews.utils.Preferences
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
                preferences = get()
            )
        }
    }
}
