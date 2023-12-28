package com.shingekinokyojin.wallrose.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResult (
    val username: String,
    val nickname: String,
    val avatarUrl: String
)