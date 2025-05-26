package br.com.lucolimac.shesafe.android.framework.service

interface SettingsService {
    suspend fun getToggleSetting(settingName: String): Boolean

    suspend fun setToggleSetting(settingName: String, value: Boolean): Boolean
}