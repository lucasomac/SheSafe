package br.com.lucolimac.shesafe.feature.profile.di

import br.com.lucolimac.shesafe.feature.profile.data.repository.ProfilePreferencesRepositoryImpl
import br.com.lucolimac.shesafe.feature.profile.domain.repository.ProfilePreferencesRepository
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateSettingUseCase
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin dependency injection module providing the profile preferences repository, custom help message
 * and settings use cases, and [ProfileViewModel].
 */
val profileModule = module {
    singleOf(::ProfilePreferencesRepositoryImpl) bind ProfilePreferencesRepository::class
    factoryOf(::ObserveHelpMessageUseCase)
    factoryOf(::RefreshHelpMessageUseCase)
    factoryOf(::UpdateHelpMessageUseCase)
    factoryOf(::ObserveSettingsUseCase)
    factoryOf(::RefreshSettingsUseCase)
    factoryOf(::UpdateSettingUseCase)
    viewModelOf(::ProfileViewModel)
}
