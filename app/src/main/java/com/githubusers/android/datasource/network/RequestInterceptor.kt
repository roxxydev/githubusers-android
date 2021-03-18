package com.githubusers.android.datasource.network

import android.content.Context
import com.githubusers.android.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class HeaderInterceptor(val context: Context) : Interceptor {

    companion object {
        fun getAppVersionHeader(): Pair<String, String> {
            return Pair("app_version", "${BuildConfig.VERSION_NAME}")
        }

        fun getAppVersionCodeHeader(): Pair<String, String> {
            return Pair("app_version_code", "${BuildConfig.VERSION_CODE}")
        }

        fun getAppClientHeader(): Pair<String, String> {
            return Pair("app_client", "android")
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val appClientHeader = getAppClientHeader()
        val appVersionHeader = getAppVersionHeader()
        val appVersionCodeHeader = getAppVersionCodeHeader()

        val requestBuilder = chain.request().newBuilder()
            .addHeader(appClientHeader.first, appClientHeader.second)
            .addHeader(appVersionHeader.first, appVersionHeader.second)
            .addHeader(appVersionCodeHeader.first, appVersionCodeHeader.second)

        return chain.proceed(requestBuilder.build())
    }
}

open class LoggingInterceptor: HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        Timber.tag("HTTP").d(message)
    }
}
