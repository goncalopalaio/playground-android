package com.playground.api.remote

import com.playground.data.remote.User

interface UsersApi {
    suspend fun user(id: String): User
}
