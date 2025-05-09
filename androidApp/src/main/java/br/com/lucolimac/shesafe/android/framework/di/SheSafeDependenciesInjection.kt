package br.com.lucolimac.shesafe.android.framework.di

import br.com.lucolimac.shesafe.android.FirebaseProvider
import br.com.lucolimac.shesafe.android.data.repository.SecureContactRepositoryImpl
import br.com.lucolimac.shesafe.android.data.source.SecureContactDataSource
import br.com.lucolimac.shesafe.android.domain.repository.SecureContactRepository
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SecureContactUseCaseImpl
import br.com.lucolimac.shesafe.android.framework.data.source.SecureSecureContactDataSourceImpl
import br.com.lucolimac.shesafe.android.framework.service.SecureContactService
import br.com.lucolimac.shesafe.android.framework.service.SecureContactFirebaseService
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
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
        factoryOf(::SecureSecureContactDataSourceImpl) { bind<SecureContactDataSource>() }
        factoryOf(::SecureContactRepositoryImpl) { bind<SecureContactRepository>() }
        factoryOf(::SecureContactUseCaseImpl) { bind<SecureContactUseCase>() }
        viewModelOf(::SecureContactViewModel)
    }
}