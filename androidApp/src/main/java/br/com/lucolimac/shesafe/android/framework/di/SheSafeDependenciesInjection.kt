package br.com.lucolimac.shesafe.android.framework.di

import br.com.lucolimac.shesafe.android.FirebaseProvider
import br.com.lucolimac.shesafe.android.data.repository.AuthRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.HelpRequestRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.SecureContactRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.SettingsRepositoryImpl
import br.com.lucolimac.shesafe.android.data.source.AuthDataSource
import br.com.lucolimac.shesafe.android.data.source.HelpRequestDataSource
import br.com.lucolimac.shesafe.android.data.source.SecureContactDataSource
import br.com.lucolimac.shesafe.android.data.source.SettingsDataSource
import br.com.lucolimac.shesafe.android.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.android.domain.repository.HelpRequestRepository
import br.com.lucolimac.shesafe.android.domain.repository.SecureContactRepository
import br.com.lucolimac.shesafe.android.domain.repository.SettingsRepository
import br.com.lucolimac.shesafe.android.domain.usecase.AuthUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.AuthUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCaseImpl
import br.com.lucolimac.shesafe.android.framework.data.source.AuthDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.HelpRequestDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.SecureSecureContactDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.SettingsDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.service.AuthFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.AuthService
import br.com.lucolimac.shesafe.android.framework.service.HelpRequestFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.HelpRequestService
import br.com.lucolimac.shesafe.android.framework.service.SecureContactService
import br.com.lucolimac.shesafe.android.framework.service.SecureContactFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.SettingsFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.SettingsService
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object SheSafeDependenciesInjection {
    val sheSafeModule = module {
        factory<FirebaseAuth> {
            FirebaseProvider.auth
        }
        factory<FirebaseFirestore> {
            FirebaseProvider.firestore
        }
        //Define a factory injection for SecureContactFirebaseService
        factoryOf(::SecureContactFirebaseService) {
            bind<SecureContactService>()
        }
        factoryOf(::HelpRequestFirebaseService) {
            bind<HelpRequestService>()
        }
        factoryOf(::SettingsFirebaseService) {
            bind<SettingsService>()
        }
        factoryOf(::AuthFirebaseService) { bind<AuthService>() }
        factoryOf(::SecureSecureContactDataSourceImpl) { bind<SecureContactDataSource>() }
        factoryOf(::HelpRequestDataSourceImpl) { bind<HelpRequestDataSource>() }
        factoryOf(::SettingsDataSourceImpl) { bind<SettingsDataSource>() }
        factoryOf(::AuthDataSourceImpl) { bind<AuthDataSource>() }
        factoryOf(::SecureContactRepositoryImpl) { bind<SecureContactRepository>() }
        factoryOf(::HelpRequestRepositoryImpl) { bind<HelpRequestRepository>() }
        factoryOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
        factoryOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
        factoryOf(::SecureContactUseCaseImpl) { bind<SecureContactUseCase>() }
        factoryOf(::HelpRequestUseCaseImpl) { bind<HelpRequestUseCase>() }
        factoryOf(::SettingsUseCaseImpl) { bind<SettingsUseCase>() }
        factoryOf(::AuthUseCaseImpl) { bind<AuthUseCase>() }
        viewModelOf(::SecureContactViewModel)
        viewModelOf(::HelpRequestViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::AuthViewModel)
        viewModelOf(::HomeViewModel)
    }
}