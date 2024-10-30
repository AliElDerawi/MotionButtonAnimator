package com.udacity.util

import android.app.Application
import com.udacity.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
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

        val myModule = module {
            single {
                MainViewModel(get())
            }
        }

        startKoin {
            androidContext(this@ProjectApp)
            modules(listOf(myModule))
        }
    }

}