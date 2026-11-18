package br.com.lucolimac.shesafe.core.logging

import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin._os_log_internal

/**
 * Emits a redacted debug-level log entry on iOS using Apple's unified logging system (`os_log`).
 *
 * @param tag Component or subsystem tag prefixed to the formatted log message.
 * @param message Log message whose sensitive tokens, emails, and API keys are redacted before output.
 */
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

/**
 * Emits a redacted info-level log entry on iOS using Apple's unified logging system (`os_log`).
 *
 * @param tag Component or subsystem tag prefixed to the formatted log message.
 * @param message Log message whose sensitive tokens, emails, and API keys are redacted before output.
 */
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

/**
 * Emits a redacted warning-level log entry on iOS using Apple's unified logging system (`os_log`).
 *
 * @param tag Component or subsystem tag prefixed to the formatted log message.
 * @param message Log message whose sensitive tokens, emails, and API keys are redacted before output.
 */
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

/**
 * Emits a redacted fault-level error log entry on iOS using Apple's unified logging system (`os_log`),
 * appending the redacted [throwable] message when available.
 *
 * @param tag Component or subsystem tag prefixed to the formatted log message.
 * @param message Primary error description redacted before output.
 * @param throwable Optional exception whose message is redacted and appended to the log detail.
 */
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
