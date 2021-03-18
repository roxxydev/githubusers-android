package com.githubusers.android.datasource.cache

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(obj: T): Long

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: List<T>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(obj: List<T>?)

    @Delete
    suspend fun delete(obj: T)
}
