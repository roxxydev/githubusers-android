package com.githubusers.android.di

import android.content.Context
import com.githubusers.android.BuildConfig
import com.githubusers.android.datasource.network.ApiServiceRetrofit
import com.githubusers.android.datasource.network.HeaderInterceptor
import com.githubusers.android.datasource.network.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesAuthorizationInterceptor(@ApplicationContext context: Context): HeaderInterceptor {

        return HeaderInterceptor(context)
    }

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logger = object: LoggingInterceptor() {}
        val loggingInterceptor = HttpLoggingInterceptor(logger)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return loggingInterceptor
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(@ApplicationContext context: Context,
                             loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {

        val appHeaderInterceptor = HeaderInterceptor(context)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(appHeaderInterceptor)
            .build();

        return okHttpClient
    }

    @Singleton
    @Provides
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.API_HOST)
            .addConverterFactory(MoshiConverterFactory.create())
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit.Builder): ApiServiceRetrofit {

        return retrofit
            .build()
            .create(ApiServiceRetrofit::class.java)
    }
}
