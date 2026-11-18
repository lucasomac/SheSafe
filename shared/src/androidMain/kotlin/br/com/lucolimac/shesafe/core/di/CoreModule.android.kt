package br.com.lucolimac.shesafe.core.di

import br.com.lucolimac.shesafe.platform.AndroidLocationService
import br.com.lucolimac.shesafe.platform.AndroidSmsService
import br.com.lucolimac.shesafe.platform.LocationService
import br.com.lucolimac.shesafe.platform.SmsService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android Koin [Module] binding [LocationService] to [AndroidLocationService]
 * and [SmsService] to [AndroidSmsService].
 *
 * @return Platform-specific core dependency injection [Module] for Android.
 */
actual fun platformCoreModule(): Module = module {
    single<LocationService> { AndroidLocationService(get()) }
    single<SmsService> { AndroidSmsService(get()) }
}
