package com.gp.space

import com.gp.api.space.SpaceApi
import com.gp.common.failure
import com.gp.common.success
import com.gp.data.space.Company
import com.gp.data.space.Launch
import com.gp.data.space.LaunchLinks
import com.gp.space.api.SpaceXApi
import com.gp.space.data.SpaceXCompany
import com.gp.space.data.SpaceXLaunch
import com.gp.space.data.SpaceXLinks
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