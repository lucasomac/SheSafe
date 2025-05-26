package br.com.lucolimac.shesafe.android.data.repository

import br.com.lucolimac.shesafe.android.data.source.SettingsDataSource
import br.com.lucolimac.shesafe.android.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val settingsDataSource: SettingsDataSource) :
    SettingsRepository {
    override suspend fun getToggleSetting(setting: String): Boolean {
        return try {
            settingsDataSource.getToggleSetting(setting)
        } catch (_: Exception) {
            // Handle the exception, e.g., log it or return a default value
            false // Default value in case of an error
        }
    }

    override suspend fun setToggleSetting(
        setting: String, value: Boolean
    ): Boolean {
        return try {
            settingsDataSource.setToggleSetting(setting, value)
            true // Return true if the setting was successfully set
        } catch (_: Exception) {
            // Handle the exception, e.g., log it or throw a custom exception
            false // Return false in case of an error
        }
    }
}