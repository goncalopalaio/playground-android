package com.gp.logger

interface Logger {
    fun log(priority: LogPriority, tag: String, message: String)
    fun error(priority: LogPriority, tag: String, message: String?, throwable: Throwable?)

    companion object {
        var logger: Logger? = null
            private set

        fun install(logger: Logger?) {
            this.logger = logger
        }
    }
}