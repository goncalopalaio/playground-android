package com.gp.api.space

import arrow.core.Either
import com.gp.data.space.Company
import com.gp.data.space.Launch

/**
 *  Access to remote company details and list of launches.
 */
interface SpaceApi {

    /**
     * @return Company details or failure.
     */
    suspend fun company(): Either<Company, Exception>

    /**
     * @return List of launches or failure.
     */
    suspend fun launches(): Either<List<Launch>, Exception>
}