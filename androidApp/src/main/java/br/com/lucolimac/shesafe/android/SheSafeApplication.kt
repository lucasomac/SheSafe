package br.com.lucolimac.shesafe.android

import android.app.Application
import br.com.lucolimac.shesafe.AppInitializer

class SheSafeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.onApplicationStart()
    }
}