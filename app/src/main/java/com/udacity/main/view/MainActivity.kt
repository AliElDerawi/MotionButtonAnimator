package com.udacity.main.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.udacity.R
import com.udacity.data.NavigationCommand
import com.udacity.databinding.ActivityMainBinding
import com.udacity.main.viewModel.MainViewModel
import com.udacity.util.Constants
import com.udacity.util.SharedUtils.isSupportsTiramisu
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private val mMainViewModel: MainViewModel by inject()
    private lateinit var mNavController: NavController
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private val mDownloadManager: DownloadManager by inject()

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.let {
            if (it.hasExtra(Constants.EXTRA_FILE_NAME)) {
                mNavController.navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(  DownloadDataModel(
                    it.getStringExtra(Constants.EXTRA_FILE_NAME) ?: "",
                    it.getStringExtra(Constants.EXTRA_FILE_STATUS) ?: ""
                )))
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
        setSupportActionBar(mBinding.toolbar).apply {
            title = getString(R.string.app_name)
        }
        initListener()
        initViewModelObserver()
    }

    private fun initListener() {
        mNavController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        NavigationUI.setupActionBarWithNavController(this, mNavController)
        mAppBarConfiguration = AppBarConfiguration(mNavController.graph)

        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (isSupportsTiramisu()){
            registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
        }else {
            registerReceiver(receiver, intentFilter)
        }
    }

    private fun initViewModelObserver() {
        mMainViewModel.navigationCommandSingleLiveEvent.observe(this) { command ->
            Timber.d("initViewModelObserver:command: $command")
            when (command) {
                is NavigationCommand.To -> mNavController.navigate(command.directions)
                is NavigationCommand.Back -> mNavController.popBackStack()
                is NavigationCommand.BackTo -> mNavController.popBackStack(
                    command.destinationId, false
                )
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.d("receiver:called")
            mMainViewModel.onReceiveDownload(intent, mDownloadManager)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
    }

}
