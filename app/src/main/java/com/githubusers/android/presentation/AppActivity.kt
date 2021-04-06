package com.githubusers.android.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.githubusers.android.R
import com.githubusers.android.databinding.ActivityMainBinding
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.presentation.util.UiUtil
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    private val viewModel: AppViewModel by  viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var appBarConfiguration : AppBarConfiguration
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        subscribeObservers()
        viewModel.setStateEvent(MainIntent.InitHomeIntent)
    }

    private fun initViews() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupWithNavController(binding.topAppbarLayout.topAppBar, navController)

        snackbar = Snackbar.make(
            binding.containerHome,
            R.string.msg_no_internet,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.msg_btn_ok) {
            snackbar.dismiss()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun subscribeObservers() {
        viewModel.registerConnectionObserver(this)
        viewModel.dataState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.SUCCESS<MainDataState> -> {
                    dataState.data?.isHomeLoaded?.let {

                    }
                    dataState.data?.hasNetwork?.let {
                        showNoInternet(!it)
                    }
                }

                is DataState.LOADING -> {
                }

                is DataState.ERROR -> {
                    dataState.stateMessage?.message?.let {
                        UiUtil.displayToast(applicationContext, it)
                    }
                }
            }
        })
    }

    private fun showNoInternet(isShow: Boolean) {
        if (isShow) {
            snackbar.show()
        }
        else {
            snackbar.dismiss()
        }
    }
}
