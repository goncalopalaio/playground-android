package com.playground.domain

import arrow.core.Either
import com.playground.api.space.SpaceApi
import com.playground.data.space.Launch
import com.playground.logger.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSpaceLaunchesUseCase(
    private val spaceApi: SpaceApi,
) {

    operator fun invoke(): Flow<Either<List<Launch>, Exception>> = flow {
        val launchesResult = spaceApi.launches()

        log { "launchesResult=$launchesResult" }
        emit(launchesResult)
    }
}