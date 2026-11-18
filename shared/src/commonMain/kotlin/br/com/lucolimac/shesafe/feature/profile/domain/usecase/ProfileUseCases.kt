package br.com.lucolimac.shesafe.feature.profile.domain.usecase

import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import br.com.lucolimac.shesafe.feature.profile.domain.repository.ProfilePreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveHelpMessageUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    operator fun invoke(): Flow<String> = repository.observeHelpMessage()
}

class RefreshHelpMessageUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    suspend operator fun invoke(): Resource<String> = repository.refreshHelpMessage()
}

class UpdateHelpMessageUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    suspend operator fun invoke(message: String): Resource<Unit> {
        val trimmed = message.trim()
        if (trimmed.isBlank()) {
            return Resource.Error(AppError.Validation.EmptyMessage)
        }
        return repository.updateHelpMessage(trimmed)
    }
}

class ObserveSettingsUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    operator fun invoke(): Flow<UserSettings> = repository.observeSettings()
}

class RefreshSettingsUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    suspend operator fun invoke(): Resource<UserSettings> = repository.refreshSettings()
}

class UpdateSettingUseCase(
    private val repository: ProfilePreferencesRepository,
) {
    suspend operator fun invoke(key: SettingKey, enabled: Boolean): Resource<Unit> =
        repository.updateSetting(key, enabled)
}
