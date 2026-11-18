package br.com.lucolimac.shesafe.core.datastore

import android.content.Context
import org.koin.mp.KoinPlatform.getKoin

actual fun producePreferencesPath(): String {
    val appContext = getKoin().get<Context>().applicationContext
    return appContext.filesDir.resolve(SHESAFE_PREFERENCES_FILE_NAME).absolutePath
}
