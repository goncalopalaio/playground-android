package com.gp.playground

import android.app.Application
import android.content.pm.ApplicationInfo
import com.gp.logger.LogcatLogger
import com.gp.logger.Logger
import com.gp.logger.log
import com.gp.playground.injection.appModule
import org.koin.core.context.GlobalContext.startKoin

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Logger.install(if (isDebuggable()) LogcatLogger() else null) // No logs for release build
        log { "onCreate" }

        startKoin {
            modules(appModule)
        }
    }

    private fun isDebuggable(): Boolean = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}