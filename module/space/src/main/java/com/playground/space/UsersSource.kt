package com.playground.space

import com.playground.api.remote.UsersApi
import com.playground.data.remote.User
import com.playground.space.api.JsonPlaceholderApi
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class UsersSource : UsersApi {
    private val service by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build()))
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .build()
            .create(JsonPlaceholderApi::class.java)
    }

    override suspend fun user(id: String): User {
        val remote = service.user(id)
        return User(remote.id, remote.name, remote.username, remote.email, remote.phone, remote.website)
    }
}
