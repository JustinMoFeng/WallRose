package com.shingekinokyojin.wallrose.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateErrorResult (
    val detail: String
)