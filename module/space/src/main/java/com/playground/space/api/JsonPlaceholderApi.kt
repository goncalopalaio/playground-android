package com.playground.space.api

import com.playground.space.data.RemotePhoto
import retrofit2.http.GET

internal interface JsonPlaceholderApi {
    @GET("photos")
    suspend fun photos(): List<RemotePhoto>
}
