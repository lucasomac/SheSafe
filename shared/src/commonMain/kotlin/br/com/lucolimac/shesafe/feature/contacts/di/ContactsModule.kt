package br.com.lucolimac.shesafe.feature.contacts.di

import br.com.lucolimac.shesafe.feature.contacts.data.repository.SecureContactRepositoryImpl
import br.com.lucolimac.shesafe.feature.contacts.domain.repository.SecureContactRepository
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.DeleteSecureContactUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.GetSecureContactByPhoneUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.ObserveSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.RefreshSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.SaveSecureContactUseCase
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.RegisterSecureContactViewModel
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.SecureContactsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin dependency injection module for the trusted emergency contacts feature.
 *
 * Provides a singleton [SecureContactRepositoryImpl] bound to [SecureContactRepository],
 * factory definitions for all contact domain use cases, and ViewModel bindings for
 * [SecureContactsViewModel] and [RegisterSecureContactViewModel].
 */
val contactsModule = module {
    singleOf(::SecureContactRepositoryImpl) bind SecureContactRepository::class
    factoryOf(::ObserveSecureContactsUseCase)
    factoryOf(::RefreshSecureContactsUseCase)
    factoryOf(::GetSecureContactByPhoneUseCase)
    factoryOf(::SaveSecureContactUseCase)
    factoryOf(::DeleteSecureContactUseCase)
    viewModelOf(::SecureContactsViewModel)
    viewModelOf(::RegisterSecureContactViewModel)
}
