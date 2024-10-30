package com.udacity.main.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.udacity.R
import com.udacity.data.BaseFragment
import com.udacity.databinding.FragmentMainBinding
import com.udacity.main.viewModel.MainViewModel
import com.udacity.util.Constants
import com.udacity.util.SharedUtils.createNotificationChannel
import com.udacity.util.SharedUtils.isNetworkConnected
import com.udacity.util.SharedUtils.isReceiveNotificationPermissionGranted
import com.udacity.util.SharedUtils.isSupportsTiramisu
import com.udacity.util.SharedUtils.showToast
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainFragment : BaseFragment() {

    override val mViewModel: MainViewModel by inject()
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mActivity: FragmentActivity
    private val downloadManager: DownloadManager by inject()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivity) {
            mActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMainBinding.inflate(inflater, container, false)
        mBinding.lifecycleOwner = this
        mBinding.mainViewModel = mViewModel
        (mActivity as AppCompatActivity).supportActionBar?.apply {
            title = mActivity.getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isSupportsTiramisu {
                if (!isReceiveNotificationPermissionGranted(mActivity)) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
            mActivity.createNotificationChannel()
        }
        initViewModelObserver()
    }


    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                mActivity.createNotificationChannel()
                Timber.d("Permission granted")
            } else {
                Timber.d("Permission denied")
            }
        }


    private fun initViewModelObserver() {
        mViewModel.onDownloadClickLiveData.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                when {
                    mViewModel.getDownloadUrl() == "-1" -> showToast(R.string.text_msg_please_select_a_file_to_download)
                    !isNetworkConnected() -> showToast(R.string.text_msg_check_your_internet_connection)
                    else -> {
                        mViewModel.setStartDownload(true)
                        download()
                    }
                }
            }
        }

        mViewModel.onStartDownloadLiveData.observe(viewLifecycleOwner) { isStart ->
            if (isStart) {
                mBinding.customButton.onClick()
            }
        }

        when (mViewModel.selectedDownloadMethodLiveData.value) {
            Constants.DOWNLOAD_UDACITY_ID -> mBinding.udacityRadioButton.isChecked = true
            Constants.DOWNLOAD_GLIDE_ID -> mBinding.glideRadioButton.isChecked = true
            Constants.DOWNLOAD_RETROFIT_ID -> mBinding.retrofitRadioButton.isChecked = true
            else -> { /* Handle other cases if necessary */
            }
        }

        mViewModel.onCompleteDownloadLiveData.observe(viewLifecycleOwner) { isComplete ->
            if (isComplete) {
                mViewModel.setStartDownload(false)
                mBinding.customButton.onCompleteDone()
            }
        }
    }


    private fun download() {
        val downloadUrl = mViewModel.getDownloadUrl()
        Timber.d("download:downloadUrl:$downloadUrl")
        val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
            setTitle(getString(R.string.app_name))
            setDescription(getString(R.string.app_description))
            setRequiresCharging(false)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        mViewModel.setDownloadId(downloadManager.enqueue(request))
    }


}