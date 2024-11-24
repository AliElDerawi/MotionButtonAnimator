package com.udacity.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DownloadDataModel(
    val fileName: String,
    val fileStatus: String
):Parcelable{
}