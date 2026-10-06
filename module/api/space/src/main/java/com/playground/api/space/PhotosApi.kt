package com.playground.api.space

import arrow.core.Either
import com.playground.data.space.Photo

interface PhotosApi {
    suspend fun photos(): Either<List<Photo>, Exception>
}
