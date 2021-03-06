package com.solvro.spaceflightnews.app

import android.app.Application
import com.solvro.spaceflightnews.ui.di.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(
                listOf(
                    MainModule.create()
                )
            )
        }
    }

}