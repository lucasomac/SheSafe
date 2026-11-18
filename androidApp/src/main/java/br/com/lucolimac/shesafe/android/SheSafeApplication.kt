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

/**
 * Custom Android [Application] entry point responsible for process-wide initialization of logging,
 * Google authentication, Firebase Remote Config, and the Koin dependency graph.
 */
class SheSafeApplication : Application() {
    /**
     * Plants the appropriate [Timber] tree (`DebugTree` in debug builds or [CrashlyticsTree] in release),
     * bootstraps Google Sign-In, configures Firebase Remote Config, and starts Koin with Android context.
     */
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

    /**
     * Applies XML defaults and fetch interval settings to Firebase Remote Config, then asynchronously
     * fetches and activates remote parameters while safely tolerating missing Firebase configuration.
     */
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
        /** Log tag used by [SheSafeApplication] lifecycle diagnostics. */
        const val TAG = "SheSafeApplication"

        /** Minimum interval in seconds (1 hour) between Firebase Remote Config network fetches. */
        const val REMOTE_CONFIG_FETCH_INTERVAL_SECONDS = 3600L
    }
}
