package br.com.lucolimac.shesafe.android.framework.di

import br.com.lucolimac.shesafe.android.FirebaseProvider
import br.com.lucolimac.shesafe.android.data.repository.AuthRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.HelpMessageRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.HelpRequestRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.SecureContactRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.SettingsRepositoryImpl
import br.com.lucolimac.shesafe.android.data.repository.api.SmsDevRepositoryImpl
import br.com.lucolimac.shesafe.android.data.source.AuthDataSource
import br.com.lucolimac.shesafe.android.data.source.HelpMessageDataSource
import br.com.lucolimac.shesafe.android.data.source.HelpRequestDataSource
import br.com.lucolimac.shesafe.android.data.source.SecureContactDataSource
import br.com.lucolimac.shesafe.android.data.source.SettingsDataSource
import br.com.lucolimac.shesafe.android.data.source.api.SmsDevDataSource
import br.com.lucolimac.shesafe.android.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.android.domain.repository.HelpMessageRepository
import br.com.lucolimac.shesafe.android.domain.repository.HelpRequestRepository
import br.com.lucolimac.shesafe.android.domain.repository.SecureContactRepository
import br.com.lucolimac.shesafe.android.domain.repository.SettingsRepository
import br.com.lucolimac.shesafe.android.domain.repository.api.SmsDevRepository
import br.com.lucolimac.shesafe.android.domain.usecase.api.SmsDevUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.AuthUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.AuthUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.HelpMessageUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.HelpMessageUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SettingsUseCaseImpl
import br.com.lucolimac.shesafe.android.domain.usecase.api.SmsDevUseCase
import br.com.lucolimac.shesafe.android.framework.constants.SmsDevApi.provideOkHttpClient
import br.com.lucolimac.shesafe.android.framework.constants.SmsDevApi.provideRetrofit
import br.com.lucolimac.shesafe.android.framework.data.source.AuthDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.HelpMessageDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.HelpRequestDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.SecureSecureContactDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.SettingsDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.data.source.api.SmsDevDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.service.AuthFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.AuthService
import br.com.lucolimac.shesafe.android.framework.service.HelpMessageFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.HelpMessageService
import br.com.lucolimac.shesafe.android.framework.service.HelpRequestFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.HelpRequestService
import br.com.lucolimac.shesafe.android.framework.service.PowerButtonService
import br.com.lucolimac.shesafe.android.framework.service.SecureContactFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.SecureContactService
import br.com.lucolimac.shesafe.android.framework.service.SettingsFirebaseService
import br.com.lucolimac.shesafe.android.framework.service.SettingsService
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.RegisterSecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit

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
        factoryOf(::HelpMessageFirebaseService) { bind<HelpMessageService>() }
        factoryOf(::PowerButtonService)
        factoryOf(::SecureSecureContactDataSourceImpl) { bind<SecureContactDataSource>() }
        factoryOf(::HelpRequestDataSourceImpl) { bind<HelpRequestDataSource>() }
        factoryOf(::SettingsDataSourceImpl) { bind<SettingsDataSource>() }
        factoryOf(::AuthDataSourceImpl) { bind<AuthDataSource>() }
        factoryOf(::HelpMessageDataSourceImpl) { bind<HelpMessageDataSource>() }
        factoryOf(::SmsDevDataSourceImpl) { bind<SmsDevDataSource>() }
        factoryOf(::SecureContactRepositoryImpl) { bind<SecureContactRepository>() }
        factoryOf(::HelpRequestRepositoryImpl) { bind<HelpRequestRepository>() }
        factoryOf(::SettingsRepositoryImpl) { bind<SettingsRepository>() }
        factoryOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
        factoryOf(::HelpMessageRepositoryImpl) { bind<HelpMessageRepository>() }
        factoryOf(::SmsDevRepositoryImpl) { bind<SmsDevRepository>() }
        factoryOf(::SecureContactUseCaseImpl) { bind<SecureContactUseCase>() }
        factoryOf(::HelpRequestUseCaseImpl) { bind<HelpRequestUseCase>() }
        factoryOf(::SettingsUseCaseImpl) { bind<SettingsUseCase>() }
        factoryOf(::AuthUseCaseImpl) { bind<AuthUseCase>() }
        factoryOf(::HelpMessageUseCaseImpl) { bind<HelpMessageUseCase>() }
        factoryOf(::SmsDevUseCaseImpl) { bind<SmsDevUseCase>() }
        viewModelOf(::SecureContactViewModel)
        viewModelOf(::HelpRequestViewModel)
        viewModelOf(::SettingsViewModel)
        viewModelOf(::AuthViewModel)
        viewModelOf(::HomeViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::RegisterSecureContactViewModel)


        factory { provideOkHttpClient() }
        factory<Retrofit> { provideRetrofit(get()) }

    }
}