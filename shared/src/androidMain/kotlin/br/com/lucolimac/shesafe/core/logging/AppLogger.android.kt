package br.com.lucolimac.shesafe.core.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

actual fun logDebug(tag: String, message: String) {
    Timber.tag(tag).d(message.redactSensitiveData())
}

actual fun logInfo(tag: String, message: String) {
    Timber.tag(tag).i(message.redactSensitiveData())
}

actual fun logWarn(tag: String, message: String) {
    Timber.tag(tag).w(message.redactSensitiveData())
}

actual fun logError(tag: String, message: String, throwable: Throwable?) {
    val safe = message.redactSensitiveData()
    if (throwable != null) {
        Timber.tag(tag).e(throwable)
    } else {
        Timber.tag(tag).e(safe)
    }
}

class CrashlyticsTree : Timber.Tree() {
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
