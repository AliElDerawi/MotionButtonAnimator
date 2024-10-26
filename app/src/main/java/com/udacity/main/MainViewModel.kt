package com.udacity.main

import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.util.Constants
import timber.log.Timber

class MainViewModel : ViewModel() {

    private var _selectedDownloadMethodLiveData = MutableLiveData<Int>()
    val selectedDownloadMethodLiveData: MutableLiveData<Int>
        get() = _selectedDownloadMethodLiveData

    private var _onDownloadClickLiveData = MutableLiveData<Boolean>()
    val onDownloadClickLiveData: MutableLiveData<Boolean>
        get() = _onDownloadClickLiveData


    init {
        _selectedDownloadMethodLiveData.value = -1
    }

    fun downloadClick() {
        _onDownloadClickLiveData.value = true
    }

    fun setOnDownloadClick(onDownloadClick: Boolean) {
        _onDownloadClickLiveData.value = onDownloadClick
    }

    fun getDownloadUrl(): String {
        return when (_selectedDownloadMethodLiveData.value) {
            Constants.DOWNLOAD_UDACITY_ID -> Constants.UDACITY_PROJECT_URL
            Constants.DOWNLOAD_GLIDE_ID -> Constants.GLIDE_URL
            Constants.DOWNLOAD_RETROFIT_ID -> Constants.RETROFIT_URL
            else -> _selectedDownloadMethodLiveData.value.toString()
        }
    }

    fun onUdacityCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean?) {
        Timber.d("MainViewModel:onUdacityCheckedChanged")
        if (isChecked!!)
            _selectedDownloadMethodLiveData.value = Constants.DOWNLOAD_UDACITY_ID
    }

    fun onGlideCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean?) {
        Timber.d("MainViewModel:onGlideCheckedChanged")
        if (isChecked!!)
            _selectedDownloadMethodLiveData.value = Constants.DOWNLOAD_GLIDE_ID
    }

    fun onRetrofitCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean?) {
        Timber.d("MainViewModel:onRetrofitCheckedChanged")
        if (isChecked!!)
            _selectedDownloadMethodLiveData.value = Constants.DOWNLOAD_RETROFIT_ID
    }

}