package com.githubusers.android.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class User(
    var id: Int,
    var type: String,
    var login: String,
    var avatarUrl: String,
    var url: String,
    var name: String?,
    var company: String?,
    var blog: String?,
    var location: String?,
    var email: String?,
    var hireable: Boolean?,
    var bio: String?,
    var twitterUsername: String?,
    var publicRepos: Int,
    var publicGists: Int,
    var followers: Int,
    var following: Int,
    var createdAt: String?,
    var note: String?
): Model, Parcelable {

    fun getUsername(): String {
        return "${this.login}"
    }
}

