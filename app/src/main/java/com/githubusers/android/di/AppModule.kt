package com.githubusers.android.di

import android.content.Context
import android.util.Log
import com.githubusers.android.BuildConfig
import com.githubusers.android.datasource.preference.AppPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesTimberTree(): Timber.Tree {
        class ReportingTree : Timber.Tree() {
            override fun log(
                priority: Int,
                tag: String?,
                message: String,
                throwable: Throwable?
            ) {
                if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                    return
                }
                // val t = throwable ?: Exception(message)
                // Pass the exception variable t to crash reporting service
            }
        }

        return when(BuildConfig.DEBUG) {
            true -> Timber.DebugTree()
            else -> ReportingTree()
        }
    }

    @Singleton
    @Provides
    fun provideAppPrefs(@ApplicationContext context: Context): AppPrefs {
        return AppPrefs(context)
    }
}
