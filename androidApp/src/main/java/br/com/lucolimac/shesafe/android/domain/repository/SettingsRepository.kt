package br.com.lucolimac.shesafe.android.domain.repository

interface SettingsRepository {
    suspend fun getToggleSetting(setting: String): Boolean

    suspend fun setToggleSetting(setting: String, value: Boolean): Boolean
}