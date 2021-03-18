package com.githubusers.android.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import com.githubusers.android.datasource.model.EntityCacheUser

// Increment version whenever schema changes
@Database(entities = [
    EntityCacheUser::class,
], version = 3, exportSchema = false)
abstract class Database: RoomDatabase() {

    abstract fun daoUser(): DaoUser

    companion object {

        val DATBASE_NAME: String = "db_app"
    }
}
