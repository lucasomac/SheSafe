package br.com.lucolimac.shesafe.core.logging

import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin._os_log_internal

@OptIn(ExperimentalForeignApi::class)
actual fun logDebug(tag: String, message: String) {
    _os_log_internal(
        null,
        OS_LOG_DEFAULT,
        OS_LOG_TYPE_DEBUG,
        "[%s] %s",
        tag,
        message.redactSensitiveData(),
    )
}

@OptIn(ExperimentalForeignApi::class)
actual fun logInfo(tag: String, message: String) {
    _os_log_internal(
        null,
        OS_LOG_DEFAULT,
        OS_LOG_TYPE_INFO,
        "[%s] %s",
        tag,
        message.redactSensitiveData(),
    )
}

@OptIn(ExperimentalForeignApi::class)
actual fun logWarn(tag: String, message: String) {
    _os_log_internal(
        null,
        OS_LOG_DEFAULT,
        OS_LOG_TYPE_ERROR,
        "[%s] WARN: %s",
        tag,
        message.redactSensitiveData(),
    )
}

@OptIn(ExperimentalForeignApi::class)
actual fun logError(tag: String, message: String, throwable: Throwable?) {
    val detail = buildString {
        append(message.redactSensitiveData())
        throwable?.message?.let {
            append(" | ")
            append(it.redactSensitiveData())
        }
    }
    _os_log_internal(
        null,
        OS_LOG_DEFAULT,
        OS_LOG_TYPE_FAULT,
        "[%s] ERROR: %s",
        tag,
        detail,
    )
}
