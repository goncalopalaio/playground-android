package com.playground.space.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RemotePhoto(
    val albumId: Int,
    val id: Int,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
)
