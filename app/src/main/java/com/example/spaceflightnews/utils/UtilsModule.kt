package com.example.spaceflightnews.utils

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object UtilsModule{
    fun create() = module{
        single{
            Preferences(androidContext())
        }
    }
}
