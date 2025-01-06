package com.udacity.features.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.udacity.R
import com.udacity.data.util.BaseFragment
import com.udacity.data.util.NavigationCommand
import com.udacity.databinding.FragmentDetailBinding
import com.udacity.features.main.viewModel.MainViewModel
import com.udacity.util.SharedUtils.setDisplayHomeAsUpEnabled
import com.udacity.util.SharedUtils.setTitle
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
        mBinding = FragmentDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            detailFragment = this@DetailFragment
            downloadDataModel = DetailFragmentArgs.fromBundle(requireArguments())
                .takeIf { arguments != null }?.downloadDataModel
        }
        setTitle(getString(R.string.text_download_status))
        setDisplayHomeAsUpEnabled(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    fun onConfirmButtonClick() {
        mViewModel.navigationCommandSingleLiveEvent.value = NavigationCommand.Back
    }
}