package com.githubusers.android.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.githubusers.android.R
import com.githubusers.android.databinding.FragmentHomeBinding
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.presentation.AppActivity
import com.githubusers.android.presentation.details.UserDetailsFragment
import com.githubusers.android.presentation.list.UserListFragment
import com.githubusers.android.presentation.util.UiUtil
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment: Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by  viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var snackbar: Snackbar

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribeObservers()
        viewModel.setStateEvent(MainIntent.InitHomeIntent)
    }

    private fun subscribeObservers() {
        viewModel.registerConnectionObserver(viewLifecycleOwner)
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.SUCCESS<MainDataState> -> {
                    dataState.data?.isHomeLoaded?.let {
                        updateAppBar(requireContext().getString(R.string.app_name), null)
                        goToUserListScreen()
                    }
                    dataState.data?.hasNetwork?.let {
                        showNoInternet(!it)
                    }
                }

                is DataState.LOADING -> {
                }

                is DataState.ERROR -> {
                    dataState.stateMessage?.message?.let {
                        UiUtil.displayToast(requireContext(), it)
                    }
                }
            }
        })
    }

    private fun initViews() {
        updateAppBar(requireContext().getString(R.string.app_name), null)
        parentFragmentManager.addOnBackStackChangedListener {
            val fgmt = parentFragmentManager.fragments.lastOrNull()
            when(fgmt?.tag) {
                UserListFragment::class.simpleName -> {
                    binding.topAppbarLayout.topAppBar.menu.setGroupVisible(R.id.group_home_menu, true)
                    UiUtil.initTopAppbar(UiUtil.Screen.HOME, binding.topAppbarLayout.topAppBar, null)
                }
                UserDetailsFragment::class.simpleName -> {
                    binding.topAppbarLayout.topAppBar.menu.setGroupVisible(R.id.group_home_menu, false)
                    UiUtil.initTopAppbar(UiUtil.Screen.USER_DETAILS, binding.topAppbarLayout.topAppBar) {
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
        snackbar = Snackbar.make(
            binding.containerHome,
            R.string.msg_no_internet,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.msg_btn_ok) {
            snackbar.dismiss()
        }
        (requireActivity() as AppActivity).setSupportActionBar(binding.topAppbarLayout.topAppBar)
    }

    private fun updateAppBar(title: String?, logo: String?) {
        UiUtil.initTopAppbar(UiUtil.Screen.HOME, binding.topAppbarLayout.topAppBar, title, logo, null)
    }

    private fun showNoInternet(isShow: Boolean) {
        if (isShow) {
            snackbar.show()
        }
        else {
            snackbar.dismiss()
        }
    }

    private fun goToUserListScreen() {
        UiUtil.replaceFragment(
            parentFragmentManager,
            R.id.fragmentHomeContainer,
            UiUtil.Screen.USER_LIST,
            null
        )
    }
}
