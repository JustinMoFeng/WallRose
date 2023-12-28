package com.shingekinokyojin.wallrose.model

import kotlinx.serialization.Serializable

@Serializable
data class AlarmArgument(
    val date: String,
    val time: String
)