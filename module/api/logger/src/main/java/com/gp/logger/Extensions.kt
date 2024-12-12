package com.gp.logger

/**
 * Uses a similar method to square/logcat.
 * By using an inline function that has access to this, the class name can be accessed without the runtime cost.
 *
 * Keep in mind that in release builds the tag name might also be obfuscated if this method is used.
 *
 * [message] is a lambda so evaluation of the strings can be skipped if the logger is disabled.
 */
inline fun Any.log(priority: LogPriority = LogPriority.Debug, tag: String? = null, message: () -> String) {
    Logger.logger?.log(priority, tag ?: this::class.java.simpleName, message())
}

fun Any.error(priority: LogPriority, tag: String? = null, message: String?, throwable: Throwable?) {
    Logger.logger?.error(priority, tag ?: this::class.java.simpleName, message, throwable)
}