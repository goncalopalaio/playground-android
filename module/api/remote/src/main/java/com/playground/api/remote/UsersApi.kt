package com.playground.api.remote

import arrow.core.Either
import com.playground.data.remote.User

interface UsersApi {
    suspend fun user(id: String): Either<User, Exception>
}
