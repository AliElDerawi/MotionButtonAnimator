package com.udacity.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.Constants
import com.udacity.util.SharedUtils.createNotificationChannel
import com.udacity.util.SharedUtils.createNotificationToDetailScreenWithExtra
import com.udacity.util.SharedUtils.isNetworkConnected
import com.udacity.util.SharedUtils.isReceiveNotificationPermissionGranted
import com.udacity.util.SharedUtils.isSupportsTiramisu
import com.udacity.util.SharedUtils.showToast
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var downloadID: Long = 0
    private lateinit var downloadManager: DownloadManager
    private val mMainViewModel: MainViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.mainViewModel = mMainViewModel

        setSupportActionBar(mBinding.toolbar)

        if (!isSupportsTiramisu {
                if (!isReceiveNotificationPermissionGranted(this)) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
            createNotificationChannel()
        }
        initViewModelObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (!isSupportsTiramisu {
                registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
            }) {
            registerReceiver(receiver, intentFilter)
        }
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                createNotificationChannel()
                Timber.d("Permission granted")
            } else {
                Timber.d("Permission denied")
            }
        }


    private fun initViewModelObserver() {
        mMainViewModel.onDownloadClickLiveData.observe(this) { isClicked ->
            if (isClicked) {
                when {
                    mMainViewModel.getDownloadUrl() == "-1" -> showToast(R.string.text_msg_please_select_a_file_to_download)
                    !isNetworkConnected() -> showToast(R.string.text_msg_check_your_internet_connection)
                    else -> {
                        mBinding.customButton.onClick()
                        download()
                        mMainViewModel.setOnDownloadClick(false)
                    }
                }
            }
        }

        when (mMainViewModel.selectedDownloadMethodLiveData.value) {
            Constants.DOWNLOAD_UDACITY_ID -> mBinding.udacityRadioButton.isChecked = true
            Constants.DOWNLOAD_GLIDE_ID -> mBinding.glideRadioButton.isChecked = true
            Constants.DOWNLOAD_RETROFIT_ID -> mBinding.retrofitRadioButton.isChecked = true
            else -> { /* Handle other cases if necessary */
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("receiver:called")

            intent?.let {
                Timber.d("receiver:intent:notNull")

                val id = it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadID) {
                    Timber.d("receiver:downloadID:$downloadID selectedDownload: ${mMainViewModel.selectedDownloadMethodLiveData.value}")

                    val message = when (mMainViewModel.selectedDownloadMethodLiveData.value) {
                        Constants.DOWNLOAD_UDACITY_ID -> getString(R.string.notification_udacity_repo_description)
                        Constants.DOWNLOAD_GLIDE_ID -> getString(R.string.notification_glide_description)
                        Constants.DOWNLOAD_RETROFIT_ID -> getString(R.string.notification_retrofit_description)
                        else -> getString(R.string.app_description)
                    }

                    val query = DownloadManager.Query().setFilterById(downloadID)
                    val cursor: Cursor = downloadManager.query(query)

                    if (cursor.moveToFirst() && cursor.count > 0) {
                        val statusOfTheDownload =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        Timber.d("receiver:statusOfTheDownload: $statusOfTheDownload")

                        when (statusOfTheDownload) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Timber.d("receiver:statusOfTheDownload:success")
                                createNotificationToDetailScreenWithExtra(
                                    getString(R.string.notification_title),
                                    message,
                                    getString(R.string.text_status_success)
                                )
                                mBinding.customButton.onCompleteDone()
                            }

                            DownloadManager.STATUS_FAILED -> {
                                Timber.d("receiver:statusOfTheDownload:failed")
                                createNotificationToDetailScreenWithExtra(
                                    getString(R.string.notification_title),
                                    message,
                                    getString(R.string.text_status_failed)
                                )
                                mBinding.customButton.onCompleteDone()
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

    private fun download() {
        val downloadUrl = mMainViewModel.getDownloadUrl()
        Timber.d("download:downloadUrl:$downloadUrl")

        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle(getString(R.string.app_name))
            setDescription(getString(R.string.app_description))
            setRequiresCharging(false)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
    }

}
