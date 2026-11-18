package br.com.lucolimac.shesafe.feature.profile.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface ProfilePreferencesRepository {
    fun observeHelpMessage(): Flow<String>
    suspend fun refreshHelpMessage(): Resource<String>
    suspend fun updateHelpMessage(message: String): Resource<Unit>

    fun observeSettings(): Flow<UserSettings>
    suspend fun refreshSettings(): Resource<UserSettings>
    suspend fun updateSetting(key: SettingKey, enabled: Boolean): Resource<Unit>
}
