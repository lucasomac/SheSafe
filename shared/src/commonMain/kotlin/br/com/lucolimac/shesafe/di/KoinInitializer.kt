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

val appModules = listOf(
    coreModule,
    authModule,
    contactsModule,
    helpRequestsModule,
    homeModule,
    profileModule,
)

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication? {
    if (KoinPlatformTools.defaultContext().getOrNull() != null) {
        return null
    }
    return startKoin {
        appDeclaration()
        modules(appModules)
    }
}
