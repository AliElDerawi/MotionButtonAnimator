package com.udacity.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.udacity.R
import com.udacity.data.BaseFragment
import com.udacity.databinding.FragmentDetailBinding
import com.udacity.main.viewModel.MainViewModel
import org.koin.android.ext.android.inject

class DetailFragment : BaseFragment() {

    override val mViewModel: MainViewModel by inject()
    private lateinit var mBinding: FragmentDetailBinding
    private lateinit var mActivity: FragmentActivity

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
        mBinding = FragmentDetailBinding.inflate(inflater, container, false)
        (mActivity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_download_status)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(mBinding){
            arguments?.let {
                DetailFragmentArgs.fromBundle(it).apply {
                    fileNameTextView.text = fileName
                    fileStatusTextView.text = fileStatus
                }
            }
        }

    }
}