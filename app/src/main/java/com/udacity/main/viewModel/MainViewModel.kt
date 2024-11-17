package com.udacity.main.viewModel

import android.app.Application
import android.app.DownloadManager
import android.content.Intent
import android.database.Cursor
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.R
import com.udacity.data.BaseViewModel
import com.udacity.util.Constants
import com.udacity.util.SharedUtils.createNotificationToDetailScreenWithExtra
import com.udacity.util.SingleLiveEvent
import timber.log.Timber

class MainViewModel(val app: Application) : BaseViewModel(app) {

    private var _selectedDownloadMethodSingleLiveEvent = SingleLiveEvent<Int>(-1)
    val selectedDownloadMethodLiveData: LiveData<Int>
        get() = _selectedDownloadMethodSingleLiveEvent

    private var _onDownloadClickLiveData = SingleLiveEvent<Boolean>(false)
    val onDownloadClickLiveData: LiveData<Boolean>
        get() = _onDownloadClickLiveData

    private var _downloadIdLiveData = MutableLiveData<Long>()
    val downloadIdLiveData: LiveData<Long>
        get() = _downloadIdLiveData

    private var _onCompleteDownloadSingleLiveEvent = SingleLiveEvent<Boolean>(false)
    val onCompleteDownloadLiveData: LiveData<Boolean>
        get() = _onCompleteDownloadSingleLiveEvent

    private var _onStartDownloadLiveData = MutableLiveData<Boolean>(false)
    val onStartDownloadLiveData: LiveData<Boolean>
        get() = _onStartDownloadLiveData

    init {
    }

    fun downloadClick() {
        _onDownloadClickLiveData.value = true
    }

    fun setStartDownload(startDownload: Boolean) {
        _onStartDownloadLiveData.value = startDownload
    }

    fun setCompleteDownload(completeDownload: Boolean) {
        _onCompleteDownloadSingleLiveEvent.value = completeDownload
    }

    fun setDownloadId(downloadId: Long) {
        _downloadIdLiveData.value = downloadId
    }

    fun getDownloadUrl(): String {
        return when (_selectedDownloadMethodSingleLiveEvent.value) {
            Constants.DOWNLOAD_UDACITY_ID -> Constants.UDACITY_PROJECT_URL
            Constants.DOWNLOAD_GLIDE_ID -> Constants.GLIDE_URL
            Constants.DOWNLOAD_RETROFIT_ID -> Constants.RETROFIT_URL
            else -> _selectedDownloadMethodSingleLiveEvent.value.toString()
        }
    }

    fun onUdacityCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean?) {
        Timber.d("MainViewModel:onUdacityCheckedChanged")
        if (isChecked!!) _selectedDownloadMethodSingleLiveEvent.value =
            Constants.DOWNLOAD_UDACITY_ID
    }

    fun onGlideCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean?) {
        Timber.d("MainViewModel:onGlideCheckedChanged")
        if (isChecked!!) _selectedDownloadMethodSingleLiveEvent.value = Constants.DOWNLOAD_GLIDE_ID
    }

    fun onRetrofitCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean?) {
        Timber.d("MainViewModel:onRetrofitCheckedChanged")
        if (isChecked!!) _selectedDownloadMethodSingleLiveEvent.value =
            Constants.DOWNLOAD_RETROFIT_ID
    }

    fun onReceiveDownload(intent: Intent?, downloadManager: DownloadManager) {
        intent?.let {
            Timber.d("receiver:intent:notNull")

            val id = it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadIdLiveData.value) {
                Timber.d("receiver:downloadID:$downloadIdLiveData.value selectedDownload: ${selectedDownloadMethodLiveData.value}")

                val message = when (selectedDownloadMethodLiveData.value) {
                    Constants.DOWNLOAD_UDACITY_ID -> app.getString(R.string.notification_udacity_repo_description)
                    Constants.DOWNLOAD_GLIDE_ID -> app.getString(R.string.notification_glide_description)
                    Constants.DOWNLOAD_RETROFIT_ID -> app.getString(R.string.notification_retrofit_description)
                    else -> app.getString(R.string.text_app_description)
                }

                val query = DownloadManager.Query().setFilterById(downloadIdLiveData.value!!)
                val cursor: Cursor = downloadManager.query(query)

                if (cursor.moveToFirst() && cursor.count > 0) {
                    val statusOfTheDownload =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    Timber.d("receiver:statusOfTheDownload: $statusOfTheDownload")

                    when (statusOfTheDownload) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Timber.d("receiver:statusOfTheDownload:success")
                            app.createNotificationToDetailScreenWithExtra(
                                app.getString(R.string.notification_title),
                                message,
                                app.getString(R.string.text_status_success)
                            )
                            setCompleteDownload(true)
                        }

                        DownloadManager.STATUS_FAILED -> {
                            Timber.d("receiver:statusOfTheDownload:failed")
                            app.createNotificationToDetailScreenWithExtra(
                                app.getString(R.string.notification_title),
                                message,
                                app.getString(R.string.text_status_failed)
                            )
                            setCompleteDownload(true)
                        }

                        DownloadManager.STATUS_PAUSED -> Timber.d("receiver:statusOfTheDownload:paused")
                        DownloadManager.STATUS_PENDING -> Timber.d("receiver:statusOfTheDownload:pending")
                        DownloadManager.STATUS_RUNNING -> Timber.d("receiver:statusOfTheDownload:running")
                    }
                }
            }
        }

    }

}