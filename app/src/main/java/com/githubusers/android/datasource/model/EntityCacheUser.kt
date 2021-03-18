package com.githubusers.android.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class EntityCacheUser (

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "avatar_url")
    var avatarUrl: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "name")
    var name: String?,

    @ColumnInfo(name = "company")
    var company: String?,

    @ColumnInfo(name = "blog")
    var blog: String?,

    @ColumnInfo(name = "location")
    var location: String?,

    @ColumnInfo(name = "email")
    var email: String?,

    @ColumnInfo(name = "hireable")
    var hireable: Boolean?,

    @ColumnInfo(name = "bio")
    var bio: String?,

    @ColumnInfo(name = "twitter_username")
    var twitterUsername: String?,

    @ColumnInfo(name = "public_repos")
    var publicRepos: Int,

    @ColumnInfo(name = "public_gists")
    var publicGists: Int,

    @ColumnInfo(name = "followers")
    var followers: Int,

    @ColumnInfo(name = "following")
    var following: Int,

    @ColumnInfo(name = "created_at")
    var createdAt: String?,

    @ColumnInfo(name = "note")
    var note: String?
) : EntityModel
