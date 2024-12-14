package com.gp.domain

import com.gp.api.space.SpaceApi
import com.gp.device.DeviceInformation
import com.gp.logger.log

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