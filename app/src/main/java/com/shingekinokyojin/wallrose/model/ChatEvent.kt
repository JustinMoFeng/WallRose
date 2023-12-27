package com.shingekinokyojin.wallrose.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatEvent(
    val event: String = "",
    val data: String = ""
)