package br.com.lucolimac.shesafe.feature.auth.domain.usecase

import br.com.lucolimac.shesafe.core.domain.event.AppEvent
import br.com.lucolimac.shesafe.core.domain.event.AppEventBus
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val repository: AuthRepository,
    private val eventBus: AppEventBus,
) {
    suspend operator fun invoke(idToken: String, accessToken: String?): Resource<UserProfile> =
        repository.signInWithGoogleTokens(idToken, accessToken).onSuccess {
            eventBus.emit(AppEvent.UserAuthenticated)
        }
}

class SignOutUseCase(
    private val repository: AuthRepository,
    private val eventBus: AppEventBus,
) {
    suspend operator fun invoke(): Resource<Unit> =
        repository.signOut().onSuccess {
            eventBus.emit(AppEvent.UserLoggedOut)
        }
}
