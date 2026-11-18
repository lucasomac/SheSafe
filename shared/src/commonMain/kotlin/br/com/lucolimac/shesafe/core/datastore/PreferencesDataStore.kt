package br.com.lucolimac.shesafe.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

/**
 * File name used for persisting Jetpack DataStore Preferences on disk.
 */
const val SHESAFE_PREFERENCES_FILE_NAME = "shesafe.preferences_pb"

/**
 * Resolves the platform-specific absolute file system path where [SHESAFE_PREFERENCES_FILE_NAME] is stored.
 *
 * @return Absolute path string pointing to the preferences file on the current platform.
 */
expect fun producePreferencesPath(): String

/**
 * Creates a multiplatform [DataStore] of [Preferences] backed by Okio at the path supplied by [producePath].
 *
 * @param producePath Lambda returning the absolute file path for storing preferences; defaults to
 * [producePreferencesPath].
 * @return A configured [DataStore] instance for reading and writing key-value [Preferences].
 */
fun createPreferencesDataStore(producePath: () -> String = ::producePreferencesPath): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() },
    )
