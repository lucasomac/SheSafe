package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.source.SettingsDataSource
import br.com.lucolimac.shesafe.android.framework.service.SettingsService

class SettingsDataSourceImpl(private val settingsService: SettingsService) : SettingsDataSource {
    override suspend fun getToggleSetting(setting: String): Boolean {
        return try {
            settingsService.getToggleSetting(setting)
        } catch (_: Exception) {
            // Handle the exception, e.g., log it or return a default value
            false // Default value in case of an error
        }
    }

    override suspend fun setToggleSetting(setting: String, value: Boolean): Boolean {
        return try {
            settingsService.setToggleSetting(setting, value)
        } catch (_: Exception) {
            // Handle the exception, e.g., log it or throw a custom exception
            false
        }
    }
}