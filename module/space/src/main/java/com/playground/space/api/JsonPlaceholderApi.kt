package com.playground.space.api

import com.playground.space.data.RemotePhoto
import com.playground.space.data.RemoteUser
import retrofit2.http.Path
import retrofit2.http.GET

internal interface JsonPlaceholderApi {
    @GET("users/{id}")
    suspend fun user(@Path("id") id: String): RemoteUser

    @GET("photos")
    suspend fun photos(): List<RemotePhoto>
}
