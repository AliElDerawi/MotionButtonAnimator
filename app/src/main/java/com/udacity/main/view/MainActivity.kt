package com.udacity.main.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.udacity.R
import com.udacity.databinding.ActivityMainBinding
import com.udacity.main.viewModel.MainViewModel
import com.udacity.util.Constants
import com.udacity.util.SharedUtils.createNotificationToDetailScreenWithExtra
import com.udacity.util.SharedUtils.isSupportsTiramisu
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private val mMainViewModel: MainViewModel by inject()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val downloadManager: DownloadManager by inject()

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (it.hasExtra(Constants.EXTRA_FILE_NAME)) {
                val bundle = Bundle().apply {
                    putString(
                        Constants.EXTRA_FILE_NAME,
                        it.getStringExtra(Constants.EXTRA_FILE_NAME)
                    )
                    putString(
                        Constants.EXTRA_FILE_STATUS,
                        it.getStringExtra(Constants.EXTRA_FILE_STATUS)
                    )
                }
                navController.navigate(R.id.detailFragment, bundle)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy:called")
        unregisterReceiver(receiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        initListener()

    }

    private fun initListener() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        NavigationUI.setupActionBarWithNavController(this, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)

        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (!isSupportsTiramisu {
                registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
            }) {
            registerReceiver(receiver, intentFilter)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("receiver:called")
            mMainViewModel.onReceiveDownload(intent, downloadManager)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

}
