package com.gp.space.api

import com.gp.space.data.SpaceXCompany
import com.gp.space.data.SpaceXLaunch
import retrofit2.http.GET

/**
 * Partial definition of the r/SpaceX API (V3).
 *
 * Base URL: [https://api.spacexdata.com/v3/]
 *
 * From [https://documenter.getpostman.com/view/2025350/RWaEzAiG?version=latest]
 *
 * NOTICE: The V3 API is deprecated as of November 2020! All existing links will continue to work,
 * but no new data will be added or updated.
 *
 */
internal interface SpaceXApi {

    @GET("info")
    suspend fun company(): SpaceXCompany

    @GET("launches")
    suspend fun launches(): List<SpaceXLaunch>
}