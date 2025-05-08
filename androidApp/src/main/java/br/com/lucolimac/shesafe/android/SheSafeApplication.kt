package br.com.lucolimac.shesafe.android

import android.app.Application
import br.com.lucolimac.shesafe.AppInitializer
import br.com.lucolimac.shesafe.android.framework.di.SheSafeDependenciesInjection.sheSafeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SheSafeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
        FirebaseProvider.initialize(this.applicationContext)
        startKoin {
            androidLogger()
            androidContext(this@SheSafeApplication)
            modules(listOf(sheSafeModule))
        }
    }
}