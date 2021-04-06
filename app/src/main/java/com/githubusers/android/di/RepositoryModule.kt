package com.githubusers.android.di

import android.content.Context
import com.githubusers.android.datasource.cache.DaoUser
import com.githubusers.android.datasource.network.ApiServiceRetrofit
import com.githubusers.android.datasource.preference.AppPrefs
import com.githubusers.android.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        @ApplicationContext appContext: Context,
        daoUser: DaoUser,
        appPrefs: AppPrefs,
        retrofit: ApiServiceRetrofit
    ): MainRepository {
        return MainRepository(
            appContext,
            daoUser,
            appPrefs,
            retrofit
        )
    }
}
