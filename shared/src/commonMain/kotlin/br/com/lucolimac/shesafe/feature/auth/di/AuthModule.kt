package br.com.lucolimac.shesafe.feature.auth.di

import br.com.lucolimac.shesafe.feature.auth.data.repository.AuthRepositoryImpl
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.GetCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.ObserveCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignInWithGoogleUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignOutUseCase
import br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel.AuthViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    factoryOf(::ObserveCurrentUserUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::SignInWithGoogleUseCase)
    factoryOf(::SignOutUseCase)
    viewModelOf(::AuthViewModel)
}
