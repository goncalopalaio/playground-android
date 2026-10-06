package com.playground.domain

import com.playground.device.DeviceInformation
import com.playground.logger.log

class GetNameUseCase(
    private val deviceInformation: DeviceInformation,
) {

    operator fun invoke(): String {
        val name =
            "Android ${deviceInformation.buildReleaseVersion} ${deviceInformation.buildModel}"
        log { "name=$name" }

        return name
    }
}