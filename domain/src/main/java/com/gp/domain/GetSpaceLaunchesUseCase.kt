package com.gp.domain

import arrow.core.Either
import com.gp.api.space.SpaceApi
import com.gp.data.space.Launch
import com.gp.logger.log
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