package br.com.lucolimac.shesafe.feature.profile.domain.usecase

import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import br.com.lucolimac.shesafe.feature.profile.domain.repository.ProfilePreferencesRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case that observes the user's custom emergency help message.
 *
 * @param repository Preferences repository providing observable message updates.
 */
class ObserveHelpMessageUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    /**
     * Starts observing the stored custom help message.
     *
     * @return A [Flow] emitting the user's custom SOS message, or an empty string if unset.
     */
    operator fun invoke(): Flow<String> = repository.observeHelpMessage()
}

/**
 * Use case that refreshes the user's custom emergency help message from remote storage.
 *
 * @param repository Preferences repository handling remote synchronization.
 */
class RefreshHelpMessageUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    /**
     * Synchronizes the custom help message with the remote backend.
     *
     * @return A [Resource] containing the latest message string or an [AppError] on failure.
     */
    suspend operator fun invoke(): Resource<String> = repository.refreshHelpMessage()
}

/**
 * Use case that validates, trims, and persists a user's custom emergency help message.
 *
 * @param repository Preferences repository used to persist the validated message.
 */
class UpdateHelpMessageUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    /**
     * Validates that [message] is non-blank after trimming and saves it to [ProfilePreferencesRepository].
     *
     * @param message Raw message input entered by the user.
     * @return [Resource.Success] when saved, or [AppError.Validation.EmptyMessage] if [message] is blank.
     */
    suspend operator fun invoke(message: String): Resource<Unit> {
        val trimmed = message.trim()
        if (trimmed.isBlank()) {
            return Resource.Error(AppError.Validation.EmptyMessage)
        }
        return repository.updateHelpMessage(trimmed)
    }
}

/**
 * Use case that observes the user's [UserSettings] preferences.
 *
 * @param repository Preferences repository providing observable settings updates.
 */
class ObserveSettingsUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    /**
     * Starts observing the user's [UserSettings].
     *
     * @return A [Flow] emitting current [UserSettings] snapshots.
     */
    operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}

/**
 * Use case that refreshes the user's [UserSettings] from remote storage.
 *
 * @param repository Preferences repository handling remote settings synchronization.
 */
class RefreshSettingsUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    /**
     * Synchronizes user settings with the remote backend.
     *
     * @return A [Resource] containing the refreshed [UserSettings] or an [AppError] on failure.
     */
    suspend operator fun invoke(): Resource<UserSettings> = repository.refreshSettings()
}

/**
 * Use case that updates a single boolean [SettingKey] preference.
 *
 * @param repository Preferences repository persisting setting updates.
 */
class UpdateSettingUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    /**
     * Persists the new [enabled] state for [key].
     *
     * @param key Target [SettingKey] to update.
     * @param enabled Desired boolean state for the setting.
     * @return A [Resource] indicating success or an [AppError] on failure.
     */
    suspend operator fun invoke(key: SettingKey, enabled: Boolean): Resource<Unit> =
        repository.updateSetting(key, enabled)
}
