package com.udacity.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.Constants
import com.udacity.util.Constants.EXTRA_FILE_NAME

class DetailActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        validateViews()
        initListeners()
    }

    private fun validateViews() {

        if (intent != null && intent.hasExtra(Constants.EXTRA_FILE_NAME)) {
            mBinding.fileNameTextView.text =
                intent.getStringExtra(Constants.EXTRA_FILE_NAME)
            mBinding.fileStatusTextView.text =
                intent.getStringExtra(Constants.EXTRA_FILE_STATUS)
        }
    }

    private fun initListeners() {
        mBinding.okButton.setOnClickListener {
            onBackPressed()
        }
    }

}
