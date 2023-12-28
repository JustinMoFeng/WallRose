package com.shingekinokyojin.wallrose.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateLoginResult (
    val access_token:String,
    val token_type: String
)