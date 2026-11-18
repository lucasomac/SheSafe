package br.com.lucolimac.shesafe.feature.helprequests.di

import br.com.lucolimac.shesafe.feature.helprequests.data.repository.EmergencySmsGatewayImpl
import br.com.lucolimac.shesafe.feature.helprequests.data.repository.FirebaseSmsProviderConfigSource
import br.com.lucolimac.shesafe.feature.helprequests.data.repository.HelpRequestRepositoryImpl
import br.com.lucolimac.shesafe.feature.helprequests.data.repository.SmsProviderConfigSource
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.EmergencySmsGateway
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.HelpRequestRepository
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.ObserveHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.RefreshHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.TriggerEmergencyAlertUseCase
import br.com.lucolimac.shesafe.feature.helprequests.presentation.viewmodel.HelpRequestsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val helpRequestsModule = module {
    singleOf(::HelpRequestRepositoryImpl) bind HelpRequestRepository::class
    singleOf(::FirebaseSmsProviderConfigSource) bind SmsProviderConfigSource::class
    single<EmergencySmsGateway> {
        EmergencySmsGatewayImpl(
            smsDevService = get(),
            infoBipService = get(),
            nativeSmsService = get(),
            providerConfigSource = get(),
        )
    }
    factoryOf(::ObserveHelpRequestsUseCase)
    factoryOf(::RefreshHelpRequestsUseCase)
    factoryOf(::TriggerEmergencyAlertUseCase)
    viewModelOf(::HelpRequestsViewModel)
}
