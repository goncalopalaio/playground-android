package com.playground.space

import com.playground.api.space.SpaceApi
import com.playground.common.failure
import com.playground.common.success
import com.playground.data.space.Company
import com.playground.data.space.Launch
import com.playground.data.space.LaunchLinks
import com.playground.space.api.SpaceXApi
import com.playground.space.data.SpaceXCompany
import com.playground.space.data.SpaceXLaunch
import com.playground.space.data.SpaceXLinks
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.ZoneOffset
import java.util.Date

private const val BASE_URL = "https://api.spacexdata.com/v3/"

class SpaceSource : SpaceApi {
    private val service by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder()
                        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                        .build()
                )
            )
            .baseUrl(BASE_URL)
            .build()
        retrofit.create(SpaceXApi::class.java)
    }

    override suspend fun launches() = try {
        map(service.launches()).success()
    } catch (e: Exception) {
        e.failure()
    }

    override suspend fun company() = try {
        map(service.company()).success()
    } catch (e: Exception) {
        e.failure()
    }

    private fun map(company: SpaceXCompany) =
        with(company) {
            Company(
                name,
                founderName,
                yearFounded,
                employees,
                launchSites,
                valuation
            )
        }

    private fun map(list: List<SpaceXLaunch>) = list.map {
        val instant = it.launchDate.toInstant()
        Launch(
            it.rocket.rocketId,
            it.rocket.rocketName,
            it.rocket.rocketType,
            it.missionName,
            it.links.missionPatchSmall,
            instant,
            it.wasSuccessful,
            map(it.links),
            instant.atZone(ZoneOffset.UTC).year
        )
    }

    private fun map(links: SpaceXLinks) =
        LaunchLinks(links.articleLink, links.wikipedia, links.videoLink)
}