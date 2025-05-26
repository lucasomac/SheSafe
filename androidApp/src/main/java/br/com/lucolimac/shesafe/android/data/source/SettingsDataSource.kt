package br.com.lucolimac.shesafe.android.data.source

interface SettingsDataSource {
    suspend fun getToggleSetting(setting: String): Boolean

    suspend fun setToggleSetting(setting: String, value: Boolean): Boolean
}