package br.com.lucolimac.shesafe.android

import android.app.Application
import android.util.Log
import br.com.lucolimac.shesafe.AppInitializer
import br.com.lucolimac.shesafe.android.framework.di.SheSafeDependenciesInjection.sheSafeModule
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class SheSafeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
        FirebaseProvider.initialize(this.applicationContext)
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Fetch remote values and activate them before starting DI so that
        // the Remote Config value for "she_safe_api_server" is available
        // when Koin creates/injects SmsProviderType.
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SheSafeApplication", "Remote config fetchAndActivate succeeded. Value for she_safe_api_server=${task.isSuccessful}")
                } else {
                    Log.w("SheSafeApplication", "Remote config fetchAndActivate failed, using defaults")
                }
            }
        // Start Koin after remote config has been fetched (or failed)
        startKoin {
            androidLogger()
            androidContext(this@SheSafeApplication)
            modules(listOf(sheSafeModule))
        }
    }
}