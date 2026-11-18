package br.com.lucolimac.shesafe.core.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Emits a redacted debug-level log entry on Android via [Timber].
 *
 * @param tag Component or subsystem tag identifying the log source.
 * @param message Log message whose sensitive tokens, emails, and API keys are redacted before output.
 */
actual fun logDebug(tag: String, message: String) {
    Timber.tag(tag).d(message.redactSensitiveData())
}

/**
 * Emits a redacted info-level log entry on Android via [Timber].
 *
 * @param tag Component or subsystem tag identifying the log source.
 * @param message Log message whose sensitive tokens, emails, and API keys are redacted before output.
 */
actual fun logInfo(tag: String, message: String) {
    Timber.tag(tag).i(message.redactSensitiveData())
}

/**
 * Emits a redacted warning-level log entry on Android via [Timber].
 *
 * @param tag Component or subsystem tag identifying the log source.
 * @param message Log message whose sensitive tokens, emails, and API keys are redacted before output.
 */
actual fun logWarn(tag: String, message: String) {
    Timber.tag(tag).w(message.redactSensitiveData())
}

/**
 * Emits a redacted error-level log entry on Android via [Timber], attaching [throwable] when present.
 *
 * @param tag Component or subsystem tag identifying the log source.
 * @param message Diagnostic message redacted prior to logging when [throwable] is `null`.
 * @param throwable Optional exception recorded by the active [Timber.Tree].
 */
actual fun logError(tag: String, message: String, throwable: Throwable?) {
    val safe = message.redactSensitiveData()
    if (throwable != null) {
        Timber.tag(tag).e(throwable)
    } else {
        Timber.tag(tag).e(safe)
    }
}

/**
 * Production [Timber.Tree] that forwards warning and error logs to [FirebaseCrashlytics]
 * after redacting personally identifiable and credential data.
 */
class CrashlyticsTree : Timber.Tree() {
    /**
     * Filters out log priorities below [Log.WARN], redacts [message], and records breadcrumbs and
     * non-fatal exceptions to [FirebaseCrashlytics].
     *
     * @param priority Android [Log] priority level of the entry.
     * @param tag Optional tag identifying the caller.
     * @param message Raw log message to redact and forward.
     * @param t Optional [Throwable] recorded as a non-fatal exception in Crashlytics.
     */
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.WARN) return
        val safeMessage = message.redactSensitiveData()
        runCatching {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("[${tag.orEmpty()}] $safeMessage")
            if (t != null) {
                crashlytics.recordException(t)
            }
        }
    }
}
