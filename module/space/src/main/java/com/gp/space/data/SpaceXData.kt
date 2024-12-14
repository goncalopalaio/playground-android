package com.gp.space.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class SpaceXRocket(
    @Json(name = "rocket_id") val rocketId: String,
    @Json(name = "rocket_name") val rocketName: String,
    @Json(name = "rocket_type") val rocketType: String,
)

@JsonClass(generateAdapter = true)
data class SpaceXLaunch(
    @Json(name = "mission_name") val missionName: String,
    @Json(name = "launch_date_utc") val launchDate: Date,
    @Json(name = "launch_success") val wasSuccessful: Boolean?,
    @Json(name = "rocket") val rocket: SpaceXRocket,
    @Json(name = "links") val links: SpaceXLinks,
)

@JsonClass(generateAdapter = true)
data class SpaceXCompany(
    @Json(name = "name") val name: String,
    @Json(name = "founder") val founderName: String,
    @Json(name = "founded") val yearFounded: Int,
    @Json(name = "launch_sites")val launchSites: Long,
    @Json(name = "valuation") val valuation: Long,
    @Json(name = "employees") val employees: Long,
)

/**
 * Links to external media from a [SpaceXLaunch].
 * All are optional.
 */
@JsonClass(generateAdapter = true)
data class SpaceXLinks(
    @Json(name = "mission_patch") val missionPatch: String?,
    @Json(name = "mission_patch_small") val missionPatchSmall: String?,
    @Json(name = "article_link") val articleLink: String?,
    @Json(name = "wikipedia") val wikipedia: String?,
    @Json(name = "video_link") val videoLink: String?,
)
