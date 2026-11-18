package br.com.lucolimac.shesafe.core.di

import br.com.lucolimac.shesafe.platform.IosLocationService
import br.com.lucolimac.shesafe.platform.IosSmsService
import br.com.lucolimac.shesafe.platform.LocationService
import br.com.lucolimac.shesafe.platform.SmsService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * iOS Koin [Module] binding [LocationService] to [IosLocationService]
 * and [SmsService] to [IosSmsService].
 *
 * @return Platform-specific core dependency injection [Module] for iOS.
 */
actual fun platformCoreModule(): Module = module {
    single<LocationService> { IosLocationService() }
    single<SmsService> { IosSmsService() }
}
