package br.com.lucolimac.shesafe.core.datastore

import android.content.Context
import org.koin.mp.KoinPlatform.getKoin

/**
 * Android implementation of [producePreferencesPath] resolving [SHESAFE_PREFERENCES_FILE_NAME]
 * inside the application's internal files directory (`Context.filesDir`).
 *
 * @return Absolute filesystem path to the Jetpack DataStore preferences file on Android.
 */
actual fun producePreferencesPath(): String {
    val appContext = getKoin().get<Context>().applicationContext
    return appContext.filesDir.resolve(SHESAFE_PREFERENCES_FILE_NAME).absolutePath
}
