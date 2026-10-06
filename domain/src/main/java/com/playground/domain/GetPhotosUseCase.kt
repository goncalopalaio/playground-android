package com.playground.domain

import arrow.core.Either
import com.playground.api.remote.PhotosApi
import com.playground.data.remote.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPhotosUseCase(private val photosApi: PhotosApi) {
    operator fun invoke(): Flow<Either<List<Photo>, Exception>> = flow {
        emit(photosApi.photos().mapLeft { it.take(10) })
    }
}
