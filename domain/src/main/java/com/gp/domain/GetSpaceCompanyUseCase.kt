package com.gp.domain

import arrow.core.Either
import com.gp.api.space.SpaceApi
import com.gp.data.space.Company
import com.gp.logger.log
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