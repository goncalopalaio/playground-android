package com.gp.logger

import android.util.Log

class LogcatLogger : Logger {
    override fun log(priority: LogPriority, tag: String, message: String) {
        when (priority) {
            LogPriority.Debug -> Log.d(tag, message)
            LogPriority.Error -> Log.e(tag, message)
            LogPriority.Assert -> Log.wtf(tag, message)
        }
    }

    override fun error(priority: LogPriority, tag: String, message: String?, throwable: Throwable?) {
        when (priority) {
            LogPriority.Debug -> Log.d(tag, message, throwable)
            LogPriority.Error -> Log.e(tag, message, throwable)
            LogPriority.Assert -> Log.wtf(tag, message, throwable)
        }
    }
}