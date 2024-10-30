package com.udacity.main

import android.app.Application
import android.widget.CompoundButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.data.BaseViewModel
import com.udacity.util.Constants
import com.udacity.util.SingleLiveEvent
import timber.log.Timber

class MainViewModel(app: Application) : BaseViewModel(app) {

    private var _selectedDownloadMethodSingleLiveEvent = SingleLiveEvent<Int>(-1)
    val selectedDownloadMethodLiveData: LiveData<Int>
        get() = _selectedDownloadMethodSingleLiveEvent

    private var _onDownloadClickLiveData = SingleLiveEvent<Boolean>(false)
    val onDownloadClickLiveData: LiveData<Boolean>
        get() = _onDownloadClickLiveData

    init {
    }

    fun downloadClick() {
        _onDownloadClickLiveData.value = true
    }

    fun setOnDownloadClick(onDownloadClick: Boolean) {
        _onDownloadClickLiveData.value = onDownloadClick
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

}