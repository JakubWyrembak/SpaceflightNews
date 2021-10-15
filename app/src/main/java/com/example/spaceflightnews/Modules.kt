package com.example.spaceflightnews

import org.koin.dsl.module

object MainModule {
    fun create() = module {
        single {
            MainViewModel()
        }
    }
}
