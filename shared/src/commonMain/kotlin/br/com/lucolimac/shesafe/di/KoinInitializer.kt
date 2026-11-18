package br.com.lucolimac.shesafe.di

import br.com.lucolimac.shesafe.core.di.coreModule
import br.com.lucolimac.shesafe.feature.auth.di.authModule
import br.com.lucolimac.shesafe.feature.contacts.di.contactsModule
import br.com.lucolimac.shesafe.feature.helprequests.di.helpRequestsModule
import br.com.lucolimac.shesafe.feature.home.di.homeModule
import br.com.lucolimac.shesafe.feature.profile.di.profileModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatformTools

/**
 * Aggregated list of Koin dependency injection modules shared across all SheSafe features and core layers.
 */
val appModules = listOf(
    coreModule,
    authModule,
    contactsModule,
    helpRequestsModule,
    homeModule,
    profileModule,
)

/**
 * Starts the global Koin dependency injection container with [appModules] if it has not been started yet.
 *
 * @param appDeclaration Optional platform-specific Koin configuration block (e.g., Android context binding).
 * @return The newly started [KoinApplication] instance, or `null` if Koin was already initialized.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication? {
    if (KoinPlatformTools.defaultContext().getOrNull() != null) {
        return null
    }
    return startKoin {
        appDeclaration()
        modules(appModules)
    }
}
