package br.com.lucolimac.shesafe.android

import android.app.Application
import br.com.lucolimac.shesafe.AuthProviderBootstrap
import br.com.lucolimac.shesafe.core.logging.CrashlyticsTree
import br.com.lucolimac.shesafe.core.logging.logInfo
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.di.initKoin
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber

class SheSafeApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsTree())
        }

        AuthProviderBootstrap.initializeGoogleAuth()
        configureRemoteConfig()

        initKoin {
            androidLogger()
            androidContext(this@SheSafeApplication)
        }
    }

    private fun configureRemoteConfig() {
        runCatching {
            val remoteConfig = Firebase.remoteConfig
            val settings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = REMOTE_CONFIG_FETCH_INTERVAL_SECONDS
            }
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            remoteConfig.setConfigSettingsAsync(settings)
            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    logInfo(TAG, "Remote config activated")
                } else {
                    logWarn(TAG, "Remote config fetch failed; using defaults")
                }
            }
        }.onFailure { error ->
            logWarn(TAG, "Remote config initialization skipped: ${error.message}")
        }
    }

    private companion object {
        const val TAG = "SheSafeApplication"
        const val REMOTE_CONFIG_FETCH_INTERVAL_SECONDS = 3600L
    }
}
