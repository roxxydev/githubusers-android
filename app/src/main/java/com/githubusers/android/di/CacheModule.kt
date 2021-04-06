package com.githubusers.android.di

import android.content.Context
import androidx.room.Room
import com.githubusers.android.datasource.cache.DaoUser
import com.githubusers.android.datasource.cache.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            Database.DATBASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideDaoUser(database: Database): DaoUser {
        return database.daoUser()
    }
}
