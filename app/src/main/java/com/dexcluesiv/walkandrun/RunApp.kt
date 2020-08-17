package com.dexcluesiv.walkandrun

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RunApp: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}