package com.playground.space.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RemoteUser(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
)
