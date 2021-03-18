package com.githubusers.android.datasource.model

data class EntityNetworkUser (
    var id: Int,
    var type: String,
    var login: String,
    var avatar_url: String,
    var url: String,
    var name: String?,
    var company: String?,
    var blog: String?,
    var location: String?,
    var email: String?,
    var hireable: Boolean?,
    var bio: String?,
    var twitter_username: String?,
    var public_repos: Int,
    var public_gists: Int,
    var followers: Int,
    var following: Int,
    var created_at: String?
) : EntityModel
