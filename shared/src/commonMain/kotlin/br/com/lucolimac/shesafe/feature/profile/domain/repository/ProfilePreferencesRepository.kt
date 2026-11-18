package br.com.lucolimac.shesafe.feature.profile.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for managing the user's custom emergency help message and [UserSettings].
 */
interface ProfilePreferencesRepository {
    /**
     * Observes the currently authenticated user's custom SOS help message.
     *
     * @return A [Flow] emitting the stored custom message, or an empty string if none is set.
     */
    fun observeHelpMessage(): Flow<String>

    /**
     * Synchronizes the user's custom help message from the remote backend into local storage.
     *
     * @return A [Resource] containing the refreshed message string or an error on failure.
     */
    suspend fun refreshHelpMessage(): Resource<String>

    /**
     * Persists a new custom emergency help [message] locally and synchronizes it remotely.
     *
     * @param message Validated custom SOS message text.
     * @return A [Resource] indicating success or failure.
     */
    suspend fun updateHelpMessage(message: String): Resource<Unit>

    /**
     * Observes the currently authenticated user's [UserSettings].
     *
     * @return A [Flow] emitting updated [UserSettings] snapshots whenever preferences change.
     */
    fun observeSettings(): Flow<UserSettings>

    /**
     * Synchronizes the user's [UserSettings] from the remote backend into local storage.
     *
     * @return A [Resource] containing the refreshed [UserSettings] or an error on failure.
     */
    suspend fun refreshSettings(): Resource<UserSettings>

    /**
     * Updates an individual [SettingKey] to [enabled] locally and synchronizes the change remotely.
     *
     * @param key Preference key to modify.
     * @param enabled Desired boolean value for [key].
     * @return A [Resource] indicating success or failure.
     */
    suspend fun updateSetting(key: SettingKey, enabled: Boolean): Resource<Unit>
}
