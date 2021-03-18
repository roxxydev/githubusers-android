package com.githubusers.android.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.githubusers.android.R
import com.githubusers.android.presentation.util.UiUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UiUtil.replaceFragment(
            supportFragmentManager,
            R.id.container,
            UiUtil.Screen.HOME,
            null
        )
    }
}
