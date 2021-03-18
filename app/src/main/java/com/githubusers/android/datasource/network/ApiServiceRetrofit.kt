package com.githubusers.android.datasource.network

import com.githubusers.android.datasource.model.EntityNetworkUser
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceRetrofit {

    @GET("users")
    suspend fun listUsers(
        @Query("since") since: Int,
        @Query("per_page") perPage: Int): List<EntityNetworkUser>

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): EntityNetworkUser
}

