package com.udacity.main

import android.Manifest
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
import com.udacity.util.SharedUtils.showToast
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private var downloadID: Long = 0

    private lateinit var action: NotificationCompat.Action

    private lateinit var downloadManager: DownloadManager

    private val mMainViewModel: MainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.mainViewModel = mMainViewModel

        setSupportActionBar(mBinding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {


            if (!isReceiveNotificationPermissionGranted(this)) {
                createNotificationChannel()
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

//        initListener()
        initViewModelObserver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {

            if (it) {
                Timber.d("Permission granted")
            } else {
                Timber.d("Permission denied")
            }
        }

    private fun initListener() {
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private fun initViewModelObserver() {
        mMainViewModel.onDownloadClickLiveData.observe(this) {
            if (it) {
                if (!mMainViewModel.getDownloadUrl().equals("-1")) {
                    if (isNetworkConnected()) {
                        mBinding.customButton.onClick()
                        download()
                        mMainViewModel.setOnDownloadClick(false)
                    } else {
                        showToast(R.string.text_msg_check_your_internet_connection)
                    }
                } else {
                    showToast(R.string.text_msg_please_select_a_file_to_download)
                }
            }
        }
        if (mMainViewModel.selectedDownloadMethodLiveData.value != -1) {
            when (mMainViewModel.selectedDownloadMethodLiveData.value) {
                Constants.DOWNLOAD_UDACITY_ID -> mBinding.udacityRadioButton.isChecked = true
                Constants.DOWNLOAD_GLIDE_ID -> mBinding.glideRadioButton.isChecked = true
                Constants.DOWNLOAD_RETROFIT_ID -> mBinding.retrofitRadioButton.isChecked = true
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            Timber.d("receiver:called")


            if (intent != null) {

                Timber.d("receiver:intent:notNull")

                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

                if (id == downloadID) {

                    Timber.d("receiver:downloadID:$downloadID" + " selectedDownload: " + mMainViewModel.selectedDownloadMethodLiveData.value)


                    val message = when (mMainViewModel.selectedDownloadMethodLiveData.value) {
                        Constants.DOWNLOAD_UDACITY_ID -> getString(R.string.notification_udacity_repo_description)
                        Constants.DOWNLOAD_GLIDE_ID -> getString(R.string.notification_glide_description)
                        Constants.DOWNLOAD_RETROFIT_ID -> getString(R.string.notification_retrofit_description)
                        else -> getString(R.string.app_description)
                    }

                    var status = ""

                    val query = DownloadManager.Query()
                    query.setFilterById(downloadID)
                    val cursor: Cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {

                            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)

                            val statusOfTheDownload = cursor.getInt(columnIndex)

                            Timber.d("receiver:statusOfTheDownload: $statusOfTheDownload")

                            when (statusOfTheDownload) {

                                DownloadManager.STATUS_SUCCESSFUL -> {
                                    status = getString(R.string.text_status_success)
                                    Timber.d("receiver:statusOfTheDownload:success")
                                    createNotificationToDetailScreenWithExtra(
                                        getString(R.string.notification_title),
                                        message,
                                        status
                                    )
                                    mBinding.customButton.onCompleteDone()
                                }

                                DownloadManager.STATUS_FAILED -> {
                                    status = getString(R.string.text_status_failed)
                                    Timber.d("receiver:statusOfTheDownload:failed")
                                    createNotificationToDetailScreenWithExtra(
                                        getString(R.string.notification_title),
                                        message,
                                        status
                                    )
                                    mBinding.customButton.onCompleteDone()
                                }

                                DownloadManager.STATUS_PAUSED -> {
                                    status = getString(R.string.text_status_paused)
                                    Timber.d("receiver:statusOfTheDownload:paused")
                                }

                                DownloadManager.STATUS_PENDING -> {
                                    status = getString(R.string.text_status_pending)
                                    Timber.d("receiver:statusOfTheDownload:pending")
                                }

                                DownloadManager.STATUS_RUNNING -> {
                                    status = getString(R.string.text_status_running)
                                    Timber.d("receiver:statusOfTheDownload:running")
                                }

                            }
                        }
                    }
                }


            }
        }
    }

    private fun download() {

        val downloadUrl = mMainViewModel.getDownloadUrl()

        Timber.d("download:downloadUrl:$downloadUrl")

        val request =
            DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

}