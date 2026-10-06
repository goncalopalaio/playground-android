package com.playground.domain

import arrow.core.Either
import com.playground.api.space.SpaceApi
import com.playground.data.space.Company
import com.playground.logger.log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetSpaceCompanyUseCase(
    private val spaceApi: SpaceApi,
) {

    operator fun invoke(): Flow<Either<Company, Exception>> = flow {
        val companyResult = spaceApi.company()
        log { "companyResult=$companyResult" }

        emit(companyResult)
    }
}