package com.githubusers.android.datasource.mapper

import com.githubusers.android.datasource.model.EntityCacheUser
import com.githubusers.android.datasource.model.EntityNetworkUser
import com.githubusers.android.domain.model.User

class UserMapper {

    companion object {

        fun mapFromNetworkEntity(entityNetworkUser: EntityNetworkUser): User {
            return User(
                id = entityNetworkUser.id,
                type = entityNetworkUser.type,
                login = entityNetworkUser.login,
                avatarUrl = entityNetworkUser.avatar_url,
                url = entityNetworkUser.url,
                name = entityNetworkUser.name,
                company = entityNetworkUser.company,
                blog = entityNetworkUser.blog,
                location = entityNetworkUser.location,
                email = entityNetworkUser.email,
                hireable = entityNetworkUser.hireable,
                bio = entityNetworkUser.bio,
                twitterUsername = entityNetworkUser.twitter_username,
                publicRepos = entityNetworkUser.public_repos,
                publicGists = entityNetworkUser.public_gists,
                followers = entityNetworkUser.followers,
                following = entityNetworkUser.following,
                createdAt = entityNetworkUser.created_at,
                note = null
            )
        }

        fun mapFromNetworkEntity(entityNetworkUsers: List<EntityNetworkUser>): List<User> {
            val users: ArrayList<User> = arrayListOf()
            for (user in entityNetworkUsers) {
                users.add(mapFromNetworkEntity(user))
            }
            return users
        }

        fun mapToEntityCache(user: User): EntityCacheUser {
            return EntityCacheUser(
                id = user.id,
                type = user.type,
                login = user.login,
                avatarUrl = user.avatarUrl,
                url = user.url,
                name = user.name,
                company = user.company,
                blog = user.blog,
                location = user.location,
                email = user.email,
                hireable = user.hireable,
                bio = user.bio,
                twitterUsername = user.twitterUsername,
                publicRepos = user.publicRepos,
                publicGists = user.publicGists,
                followers = user.followers,
                following = user.following,
                createdAt = user.createdAt,
                note = user.note
            )
        }

        fun mapToEntityCache(users: List<User>): List<EntityCacheUser> {
            val entityCacheUsers: ArrayList<EntityCacheUser> = arrayListOf()
            for (user in users) {
                entityCacheUsers.add(mapToEntityCache(user))
            }
            return entityCacheUsers
        }

        fun mapFromEntityCache(entityCacheUser: EntityCacheUser): User {
            return User(
                id = entityCacheUser.id,
                type = entityCacheUser.type,
                login = entityCacheUser.login,
                avatarUrl = entityCacheUser.avatarUrl,
                url = entityCacheUser.url,
                name = entityCacheUser.name,
                company = entityCacheUser.company,
                blog = entityCacheUser.blog,
                location = entityCacheUser.location,
                email = entityCacheUser.email,
                hireable = entityCacheUser.hireable,
                bio = entityCacheUser.bio,
                twitterUsername = entityCacheUser.twitterUsername,
                publicRepos = entityCacheUser.publicRepos,
                publicGists = entityCacheUser.publicGists,
                followers = entityCacheUser.followers,
                following = entityCacheUser.following,
                createdAt = entityCacheUser.createdAt,
                note = entityCacheUser.note
            )
        }

        fun mapFromEntityCache(entityCacheUsers: List<EntityCacheUser>): List<User> {
            val users: ArrayList<User> = arrayListOf()
            for (entityCacheUser in entityCacheUsers) {
                users.add(mapFromEntityCache(entityCacheUser))
            }
            return users
        }
    }
}


