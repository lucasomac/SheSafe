package br.com.lucolimac.shesafe.core.di

import br.com.lucolimac.shesafe.platform.IosLocationService
import br.com.lucolimac.shesafe.platform.IosSmsService
import br.com.lucolimac.shesafe.platform.LocationService
import br.com.lucolimac.shesafe.platform.SmsService
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformCoreModule(): Module = module {
    single<LocationService> { IosLocationService() }
    single<SmsService> { IosSmsService() }
}
