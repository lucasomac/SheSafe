package br.com.lucolimac.shesafe.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import br.com.lucolimac.shesafe.core.database.SheSafeDatabase
import br.com.lucolimac.shesafe.core.database.buildSheSafeDatabase
import br.com.lucolimac.shesafe.core.database.createDatabaseBuilder
import br.com.lucolimac.shesafe.core.database.dao.HelpRequestDao
import br.com.lucolimac.shesafe.core.database.dao.SecureContactDao
import br.com.lucolimac.shesafe.core.datastore.createPreferencesDataStore
import br.com.lucolimac.shesafe.core.domain.event.AppEventBus
import br.com.lucolimac.shesafe.core.domain.event.AppEventBusImpl
import br.com.lucolimac.shesafe.core.network.networkModule
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformCoreModule(): Module

val coreModule = module {
    includes(networkModule, platformCoreModule())
    single<AppEventBus> { AppEventBusImpl() }
    single<SheSafeDatabase> { buildSheSafeDatabase(createDatabaseBuilder()) }
    single<SecureContactDao> { get<SheSafeDatabase>().secureContactDao() }
    single<HelpRequestDao> { get<SheSafeDatabase>().helpRequestDao() }
    single<DataStore<Preferences>> { createPreferencesDataStore() }
}
