package br.com.lucolimac.shesafe.core.logging

/**
 * Writes a debug-level log entry using the platform-native logging facility.
 *
 * @param tag Short identifier indicating the component or class emitting the log.
 * @param message Diagnostic message to log (sensitive tokens/PII are sanitized by platform implementations).
 */
expect fun logDebug(tag: String, message: String)

/**
 * Writes an informational log entry using the platform-native logging facility.
 *
 * @param tag Short identifier indicating the component or class emitting the log.
 * @param message Informational message to log.
 */
expect fun logInfo(tag: String, message: String)

/**
 * Writes a warning-level log entry using the platform-native logging facility.
 *
 * @param tag Short identifier indicating the component or class emitting the log.
 * @param message Warning message describing a recoverable or unexpected condition.
 */
expect fun logWarn(tag: String, message: String)

/**
 * Writes an error-level log entry with an optional exception stack trace using the platform-native logger.
 *
 * @param tag Short identifier indicating the component or class emitting the log.
 * @param message Error message describing the failure.
 * @param throwable Optional [Throwable] associated with the error.
 */
expect fun logError(tag: String, message: String, throwable: Throwable? = null)

/**
 * Regular expression matching `Bearer` and `App` authorization header credentials.
 */
private val bearerPattern = Regex("(Bearer|App)\\s+[A-Za-z0-9\\-._~+/]+=*")

/**
 * Regular expression matching standard email addresses to prevent PII leakage in logs.
 */
private val emailPattern = Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")

/**
 * Regular expression matching `key=` URL query parameters containing API secrets.
 */
private val queryKeyPattern = Regex("(key=)[^&\\s]+")

/**
 * Masks authorization tokens, email addresses, and `key=` query secrets in this string before logging.
 *
 * @return A sanitized copy of this [String] with sensitive values replaced by redaction placeholders.
 */
fun String.redactSensitiveData(): String = this
    .replace(bearerPattern, "$1 [REDACTED]")
    .replace(emailPattern, "[EMAIL_REDACTED]")
    .replace(queryKeyPattern, "$1[REDACTED]")
