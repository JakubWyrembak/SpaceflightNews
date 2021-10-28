package com.example.spaceflightnews.ui.start

import android.app.Application
import com.example.spaceflightnews.MainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication: Application() {

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