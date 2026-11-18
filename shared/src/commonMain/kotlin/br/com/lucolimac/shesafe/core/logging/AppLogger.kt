package br.com.lucolimac.shesafe.core.logging

expect fun logDebug(tag: String, message: String)
expect fun logInfo(tag: String, message: String)
expect fun logWarn(tag: String, message: String)
expect fun logError(tag: String, message: String, throwable: Throwable? = null)

private val bearerPattern = Regex("(Bearer|App)\\s+[A-Za-z0-9\\-._~+/]+=*")
private val emailPattern = Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")
private val queryKeyPattern = Regex("(key=)[^&\\s]+")

fun String.redactSensitiveData(): String = this
    .replace(bearerPattern, "$1 [REDACTED]")
    .replace(emailPattern, "[EMAIL_REDACTED]")
    .replace(queryKeyPattern, "$1[REDACTED]")
