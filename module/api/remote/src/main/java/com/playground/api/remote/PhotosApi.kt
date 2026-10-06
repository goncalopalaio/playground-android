package com.playground.api.remote

import arrow.core.Either
import com.playground.data.remote.Photo

interface PhotosApi {
    suspend fun photos(): Either<List<Photo>, Exception>
}
