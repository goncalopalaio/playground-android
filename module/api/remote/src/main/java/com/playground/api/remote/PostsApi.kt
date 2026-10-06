package com.playground.api.remote

import arrow.core.Either
import com.playground.data.remote.Post

interface PostsApi {
    suspend fun posts(userId: Int): Either<List<Post>, Exception>
}
