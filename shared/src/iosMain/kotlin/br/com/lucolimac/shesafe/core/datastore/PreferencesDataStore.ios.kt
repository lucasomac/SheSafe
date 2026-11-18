package br.com.lucolimac.shesafe.core.datastore

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * iOS implementation of [producePreferencesPath] resolving [SHESAFE_PREFERENCES_FILE_NAME]
 * inside the app's sandboxed `Documents` directory (`NSDocumentDirectory`).
 *
 * @return Absolute filesystem path to the Jetpack DataStore preferences file on iOS.
 */
@OptIn(ExperimentalForeignApi::class)
actual fun producePreferencesPath(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return "${requireNotNull(documentDirectory?.path)}/$SHESAFE_PREFERENCES_FILE_NAME"
}
