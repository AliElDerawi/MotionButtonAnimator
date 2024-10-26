package com.udacity.util

import com.udacity.BuildConfig

object Constants {

    const val DOWNLOAD_UDACITY_ID = 1
    const val DOWNLOAD_GLIDE_ID = 2
    const val DOWNLOAD_RETROFIT_ID = 3

    const val UDACITY_PROJECT_URL =
        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

    const val GLIDE_URL =
        "https://github.com/bumptech/glide"

    const val RETROFIT_URL = "https://github.com/square/retrofit"


    const val EXTRA_FILE_NAME = BuildConfig.APPLICATION_ID + ".FILE_NAME"
    const val EXTRA_FILE_STATUS = BuildConfig.APPLICATION_ID + ".STATUS_NAME"
}