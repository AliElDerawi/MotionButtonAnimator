package com.udacity.util

import android.app.Application
import timber.log.Timber

class ProjectApp : Application() {

    companion object {
        @Volatile
        private var mAppInstance: ProjectApp? = null

        fun getInstance(): ProjectApp? {
            if (mAppInstance == null) {
                synchronized(ProjectApp::class.java) {
                    if (mAppInstance == null) mAppInstance = ProjectApp()
                }
            }
            return mAppInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        mAppInstance = this
        Timber.plant(Timber.DebugTree())
    }



}