package com.udacity.features.main.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.udacity.R
import com.udacity.data.util.BaseFragment
import com.udacity.databinding.FragmentMainBinding
import com.udacity.features.main.viewModel.MainViewModel
import com.udacity.util.Constants
import com.udacity.util.SharedUtils.createNotificationChannel
import com.udacity.util.SharedUtils.download
import com.udacity.util.SharedUtils.isNetworkConnected
import com.udacity.util.SharedUtils.isReceiveNotificationPermissionGranted
import com.udacity.util.SharedUtils.isSupportsTiramisu
import com.udacity.util.SharedUtils.setDisplayHomeAsUpEnabled
import com.udacity.util.SharedUtils.setTitle
import com.udacity.util.SharedUtils.showToast
import org.koin.android.ext.android.inject
import timber.log.Timber


class MainFragment : BaseFragment() {

    override val mViewModel: MainViewModel by inject()
    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mActivity: FragmentActivity
    private val mDownloadManager: DownloadManager by inject()

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
        mBinding = FragmentMainBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            mainViewModel = mViewModel
        }
        setTitle(mActivity.getString(R.string.app_name))
        setDisplayHomeAsUpEnabled(false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isSupportsTiramisu() && !isReceiveNotificationPermissionGranted(mActivity)) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
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
        with(mViewModel) {
            onDownloadClickLiveData.observe(viewLifecycleOwner) { isClicked ->
                if (isClicked) {
                    when {
                        getDownloadUrl() == "-1" -> mActivity.showToast(R.string.text_msg_please_select_a_file_to_download)
                        !isNetworkConnected() -> mActivity.showToast(R.string.text_msg_check_your_internet_connection)
                        else -> {
                            setStartDownload(true)
                            download()
                        }
                    }
                }
            }

            onStartDownloadLiveData.observe(viewLifecycleOwner) { isStart ->
                if (isStart) {
                    mBinding.customButton.onClick()
                }
            }

            with(mBinding) {
                when (selectedDownloadMethodLiveData.value) {
                    Constants.DOWNLOAD_UDACITY_ID -> udacityRadioButton.isChecked = true
                    Constants.DOWNLOAD_GLIDE_ID -> glideRadioButton.isChecked = true
                    Constants.DOWNLOAD_RETROFIT_ID -> retrofitRadioButton.isChecked = true
                    else -> { /* Handle other cases if necessary */
                    }
                }
            }

            onCompleteDownloadLiveData.observe(viewLifecycleOwner) { isComplete ->
                if (isComplete) {
                    setStartDownload(false)
                    mBinding.customButton.onCompleteDone()
                }
            }
        }
    }

    private fun download() {
        val downloadUrl = mViewModel.getDownloadUrl()
        Timber.d("download:downloadUrl:$downloadUrl")
        mViewModel.setDownloadId(
            mDownloadManager.download(
                downloadUrl,
                getString(R.string.app_name),
                getString(R.string.text_app_description)
            )
        )
    }

}