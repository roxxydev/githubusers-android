package com.githubusers.android.repository

import android.content.Context
import com.githubusers.android.R
import com.githubusers.android.datasource.cache.DaoUser
import com.githubusers.android.datasource.mapper.UserMapper
import com.githubusers.android.datasource.network.ApiServiceRetrofit
import com.githubusers.android.datasource.network.ResultWrapper
import com.githubusers.android.datasource.network.safeApiCall
import com.githubusers.android.datasource.preference.AppPrefs
import com.githubusers.android.domain.model.User
import com.githubusers.android.domain.state.DataState
import com.githubusers.android.domain.state.DataState.LOADING
import com.githubusers.android.domain.state.ErrorType
import com.githubusers.android.domain.state.StateMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class  MainRepository
constructor(
    private val appContext: Context,
    private val daoUser: DaoUser,
    private val appPrefs: AppPrefs,
    private val apiService: ApiServiceRetrofit
) {

    suspend fun listUsers(lastIdFetched: Int?): Flow<DataState<List<User>>> = flow {
        val lastId = lastIdFetched ?: 0;
        try {
            emit(LOADING<List<User>>(true))
            val response = safeApiCall {
                apiService.listUsers(lastId, 25)
            }
            when(response) {
                is ResultWrapper.Success -> {
                    val networkDto = response.value
                    val users: List<User> = UserMapper.mapFromNetworkEntity(networkDto)
                    val dtoToCacheUser = UserMapper.mapToEntityCache(users)
                    daoUser.upsert(dtoToCacheUser)

                    val entityCacheUsers = daoUser.getAll(lastId)
                    val cacheUsers = UserMapper.mapFromEntityCache(entityCacheUsers)
                    emit(DataState.SUCCESS(cacheUsers))
                }
                else -> {
                    val message = appContext.getString(R.string.error_network)
                    val stateMessage = StateMessage(message)
                    val entityCacheUsers = daoUser.getAll(lastId)
                    val cacheUsers = UserMapper.mapFromEntityCache(entityCacheUsers)

                    emit(
                        DataState.ERROR(
                            stateMessage,
                            ErrorType.NETWORK_ERROR,
                            cacheUsers
                        )
                    )
                }
            }
            emit(LOADING<List<User>>(false))
        } catch (e: Exception) {
            Timber.e(e)
            val errorTxt = appContext.getString(R.string.error_message)
            val stateMessage = StateMessage(errorTxt)
            val entityCacheUsers = daoUser.getAll(lastId)
            val cacheUsers = UserMapper.mapFromEntityCache(entityCacheUsers)
            emit(DataState.ERROR(stateMessage, ErrorType.APP_ERROR, cacheUsers))
        }
    }

    suspend fun getUser(username: String): Flow<DataState<User>> = flow {
        try {
            emit(LOADING<User>(true))
            val response = safeApiCall {
                apiService.getUser(username)
            }
            when(response) {
                is ResultWrapper.Success -> {
                    val networkDto = response.value
                    val user = UserMapper.mapFromNetworkEntity(networkDto)
                    val dtoToCacheUser = UserMapper.mapToEntityCache(user)
                    daoUser.upsert(dtoToCacheUser)

                    val entityCacheUser = daoUser.get(username)
                    var cacheUser = UserMapper.mapFromEntityCache(entityCacheUser)
                    emit(DataState.SUCCESS(cacheUser))
                }
                else -> {
                    val message = appContext.getString(R.string.error_network)
                    val stateMessage = StateMessage(message)
                    val entityCacheUser = daoUser.get(username)
                    var user = UserMapper.mapFromEntityCache(entityCacheUser)
                    emit(
                        DataState.ERROR(
                            stateMessage,
                            ErrorType.NETWORK_ERROR,
                            user
                        )
                    )
                }
            }
            emit(LOADING<User>(false))
        } catch (e: Exception) {
            Timber.e(e)
            val errorTxt = appContext.getString(R.string.error_message)
            val stateMessage = StateMessage(errorTxt)
            val entityCacheUser = daoUser.get(username)
            var user = UserMapper.mapFromEntityCache(entityCacheUser)
            emit(
                DataState.ERROR(
                    stateMessage,
                    ErrorType.NETWORK_ERROR,
                    user
                )
            )
        }
    }

    suspend fun saveNote(username: String, note: String?): Flow<DataState<User>> = flow {
        try {
            val entityCacheUser = daoUser.get(username)
            entityCacheUser.note = note ?: ""
            daoUser.updateNote(username, note)
            val entityCacheUserUpdated = daoUser.get(username)
            val userUpdated = UserMapper.mapFromEntityCache(entityCacheUserUpdated)
            emit(DataState.SUCCESS(userUpdated))
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}
