package com.githubusers.android.datasource.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.githubusers.android.datasource.model.EntityCacheUser

@Dao
interface DaoUser:
    BaseDao<EntityCacheUser> {

    // Order cache in descending so that cache items will be displayed starting from recent search.
    @Query("SELECT * FROM user WHERE id > :lastId ORDER BY id ASC")
    suspend fun getAll(lastId: Int): List<EntityCacheUser>

    @Query("SELECT * FROM user WHERE id=:id")
    suspend fun get(id: Int): EntityCacheUser

    @Query("SELECT * FROM user WHERE login=:username")
    suspend fun get(username: String): EntityCacheUser

    @Query("UPDATE user SET note=:note WHERE login=:username")
    suspend fun updateNote(
        username: String,
        note: String?
    )

    @Query(
        """UPDATE user SET type=:type, login=:login,
            avatar_url=:avatarUrl, url=:url, name=:name,
            company=:company, blog=:blog, location=:location,
            email=:email, hireable=:hireable, bio=:bio,
            twitter_username=:twitterUsername,
            public_repos=:publicRepos,
            public_gists=:publicGists,
            followers=:followers, following=:following,
            created_at=:createdAt
        WHERE id =:id"""
    )
    suspend fun updateUser(
        id: Int,
        type: String,
        login: String,
        avatarUrl: String,
        url: String,
        name: String?,
        company: String?,
        blog: String?,
        location: String?,
        email: String?,
        hireable: Boolean?,
        bio: String?,
        twitterUsername: String?,
        publicRepos: Int,
        publicGists: Int,
        followers: Int,
        following: Int,
        createdAt: String?
    )

    @Transaction
    suspend fun upsert(obj: EntityCacheUser) {
        val id = insert(obj)
        if (id == -1L) {
            updateUser(
                obj.id,
                obj.type,
                obj.login,
                obj.avatarUrl,
                obj.url,
                obj.name,
                obj.company,
                obj.blog,
                obj.location,
                obj.email,
                obj.hireable,
                obj.bio,
                obj.twitterUsername,
                obj.publicRepos,
                obj.publicGists,
                obj.followers,
                obj.following,
                obj.createdAt
            )
        }
    }

    @Transaction
    suspend fun upsert(objList: List<EntityCacheUser>) {
        for(obj in objList) {
            upsert(obj)
        }
    }
}
