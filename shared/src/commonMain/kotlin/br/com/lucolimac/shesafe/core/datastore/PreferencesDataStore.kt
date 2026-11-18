package br.com.lucolimac.shesafe.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

const val SHESAFE_PREFERENCES_FILE_NAME = "shesafe.preferences_pb"

expect fun producePreferencesPath(): String

fun createPreferencesDataStore(producePath: () -> String = ::producePreferencesPath): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() },
    )
