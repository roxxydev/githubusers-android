package com.githubusers.android.presentation.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.githubusers.android.R
import com.githubusers.android.presentation.details.UserDetailsFragment
import com.githubusers.android.presentation.home.HomeFragment
import com.githubusers.android.presentation.list.UserListFragment
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

data class DisplayImageOpts(
    val isDisplayLoading: Boolean,
    val isDisplayErrorImage: Boolean,
    val isCircleCrop: Boolean
)

class UiUtil {

    enum class Screen {
        HOME,
        USER_LIST,
        USER_DETAILS
    }

    companion object {

        fun displayToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        @ExperimentalCoroutinesApi
        fun replaceFragment(
            fragmentManager: FragmentManager,
            fragmentContainer: Int,
            screen: Screen,
            arguments: Bundle?
        ) {
            var newFrag: Fragment? = null
            var newFragTag: String? = null
            var isAddToBackStack = false

            when (screen) {
                Screen.HOME -> {
                    newFrag = HomeFragment()
                    newFragTag = HomeFragment::class.simpleName
                }
                Screen.USER_LIST -> {
                    newFrag = UserListFragment()
                    newFragTag = UserListFragment::class.simpleName
                }
                Screen.USER_DETAILS -> {
                    newFrag = UserDetailsFragment()
                    newFrag.arguments = arguments
                    newFragTag = UserDetailsFragment::class.simpleName
                    isAddToBackStack = true
                }
            }

            val currFragment = fragmentManager
                .findFragmentById(fragmentContainer)

            if (!currFragment?.tag.equals(newFragTag)) {
                newFrag.let {
                    val fgmtTrx = fragmentManager.beginTransaction()
                    if (isAddToBackStack) {
                        fgmtTrx
                            .replace(fragmentContainer, it, newFragTag)
                            .addToBackStack(null)
                            .commit()
                    }
                    else {
                        fgmtTrx
                            .replace(fragmentContainer, it, newFragTag)
                            .commit()
                    }

                }
            }
        }

        fun initTopAppbar(
            screen: Screen,
            topAppBarView: MaterialToolbar,
            eventOnNavIconClicked: (() -> Unit)?
        ) {
            initTopAppbar(screen, topAppBarView, null, null, eventOnNavIconClicked)
        }

        fun initTopAppbar(
            screen: Screen,
            topAppBarView: MaterialToolbar,
            toolbarTitle: String?,
            navigationIconImageUrl: String?,
            eventOnNavIconClicked: (() -> Unit)?
        ) {
            val ctx = topAppBarView.context

            eventOnNavIconClicked?.let {
                topAppBarView.setNavigationOnClickListener {
                    eventOnNavIconClicked.invoke()
                }
            }

            when(screen) {
                Screen.HOME, Screen.USER_LIST -> {
                    topAppBarView.title = toolbarTitle ?: ctx.getString(R.string.app_name)
                    if (navigationIconImageUrl == null) {
                        topAppBarView.navigationIcon = ContextCompat.getDrawable(ctx, R.drawable.ic_logo_githubusers)
                    }
                    else {
                        displayDrawable(ctx, navigationIconImageUrl) { organizationIconDrawable ->
                            topAppBarView.navigationIcon = organizationIconDrawable
                        }
                    }
                }
                Screen.USER_DETAILS -> {
                    topAppBarView.navigationIcon = ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_back)
                    topAppBarView.title = toolbarTitle ?: ctx.getString(R.string.user_details_appbar_title)
                }
            }
        }

        /**
         * Helper method for displaying and loading image to ImageView using Glide.
         * SwipeRefreshLayout CircularProgressDrawable will be used as placeholder while
         * Material Design progress indicator is not yet supported.
         */
        fun displayImage(
            context: Context,
            imgUrl: String?,
            imageView: ImageView,
            opts: DisplayImageOpts
        ) {

            val glideRequestBuilder = Glide.with(context)
                .asBitmap()

            if (opts.isDisplayLoading) {
                val circularProgressDrawable = CircularProgressDrawable(context)
                circularProgressDrawable.setColorSchemeColors(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorOnPrimary),
                    ContextCompat.getColor(context, R.color.colorPrimaryVariant))
                circularProgressDrawable.strokeWidth = 10f
                circularProgressDrawable.centerRadius = 30f
                circularProgressDrawable.start()
                glideRequestBuilder.placeholder(circularProgressDrawable)
            }

            if (opts.isDisplayErrorImage) {
                glideRequestBuilder.error(ContextCompat.getDrawable(context, R.drawable.ic_img_broken))
            }

            if (opts.isCircleCrop) {
                glideRequestBuilder.circleCrop()
            }

            glideRequestBuilder
                .fallback(ContextCompat.getDrawable(context, R.drawable.ic_img_fallback))
                .load(imgUrl)
                .into(imageView)
        }

        fun displayDrawable(
            context: Context,
            imgUrl: String?,
            callback: ((Drawable?) -> Unit)
        ) {
            Glide.with(context)
                .asDrawable()
                .load(imgUrl)
                .into(
                    object: CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?,
                        ) {
                            callback.invoke(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            callback.invoke(placeholder)
                        }
                    }
                )
        }
    }
}
