package com.githubusers.android.presentation.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.githubusers.android.R

data class DisplayImageOpts(
    val isDisplayLoading: Boolean,
    val isDisplayErrorImage: Boolean,
    val isCircleCrop: Boolean
)

class UiUtil {

    enum class Screen {
        USER_LIST,
        USER_DETAILS
    }

    companion object {

        fun displayToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        /**
         * Helper method for navigating between fragments in NavigationHost.
         */
        fun navigateTo(navController: NavController, action: Int, bundle: Bundle?) {
            if (bundle != null) {
                navController.navigate(action, bundle)
            }
            else {
                navController.navigate(action)
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
