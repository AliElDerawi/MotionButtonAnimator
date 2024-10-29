package com.udacity.util

import android.app.Application
import timber.log.Timber

class ProjectApp : Application() {

    companion object {
        @Volatile
        private var mAppInstance: ProjectApp? = null

        fun getApp(): ProjectApp {
            return mAppInstance ?: synchronized(this) {
                mAppInstance ?: ProjectApp().also { mAppInstance = it }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        mAppInstance = this
        Timber.plant(Timber.DebugTree())
    }

}