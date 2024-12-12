package com.gp.device

import android.os.Build

class AndroidDeviceInformation : DeviceInformation {

    override val buildReleaseVersion: String
        get() = Build.VERSION.RELEASE

    override val buildModel: String
        get() = Build.MODEL
}