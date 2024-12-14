package com.gp.data.space

import java.time.Instant

/**
 * Information about the launch.
 *
 * - [id] - ID of the launch.
 * - [rocketName] - Name of rocket.
 * - [rocketType] - Rocket type.
 * - [missionName] - Mission name.
 * - [missionPatchSmall] - Mission patch image (small).
 * - [wasSuccessful] - `true` if the launch was successful, `false` otherwise.
 * - [links] - Links related to the launch.
 * - [launchUtc] - Launch date in UTC.
 * - [launchYear] - Year the launch occurred.
 */
data class Launch(
    val id: String,
    val rocketName: String,
    val rocketType: String,
    val missionName: String,
    val missionPatchSmall: String?,
    val launchUtc: Instant,
    val wasSuccessful: Boolean?,
    val links: LaunchLinks,
    val launchYear: Int,
)

/**
 * Details about the company.
 *
 * - [companyName] - Name of the company.
 * - [founderName] - Name of the founder.
 * - [yearFounded] - Year it was founded.
 * - [employees] - Number of employees.
 * - [launchSites] - Number of launch sites.
 * - [valuation] - Current valuation.
 */
data class Company(
    val companyName: String,
    val founderName: String,
    val yearFounded: Int,
    val employees: Long,
    val launchSites: Long,
    val valuation: Long,
)

/**
 * Links related to a launch.
 *
 * - [article] - Article link.
 * - [wikipedia] - Wikipedia link.
 * - [video] - Video link.
 */
data class LaunchLinks(
    val article: String?,
    val wikipedia: String?,
    val video: String?,
)