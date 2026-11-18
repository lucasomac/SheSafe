package br.com.lucolimac.shesafe.feature.auth.domain.usecase

import br.com.lucolimac.shesafe.core.domain.event.AppEvent
import br.com.lucolimac.shesafe.core.domain.event.AppEventBus
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository

/**
 * Use case that signs a user in via Google OAuth tokens and broadcasts an authentication event.
 *
 * @property repository Authentication repository executing the credential sign-in.
 * @property eventBus Application-wide event bus notified when authentication succeeds.
 */
class SignInWithGoogleUseCase(
    private val repository: AuthRepository,
    private val eventBus: AppEventBus,
) {
    /**
     * Signs in using the provided Google tokens and emits [AppEvent.UserAuthenticated] upon success.
     *
     * @param idToken OpenID Connect ID token returned by Google Sign-In.
     * @param accessToken Optional OAuth 2.0 access token returned by Google Sign-In.
     * @return A [Resource] wrapping the authenticated [UserProfile] or a sign-in error.
     */
    suspend operator fun invoke(idToken: String, accessToken: String?): Resource<UserProfile> =
        repository.signInWithGoogleTokens(idToken, accessToken).onSuccess {
            eventBus.emit(AppEvent.UserAuthenticated)
        }
}

/**
 * Use case that signs out the current user and broadcasts a logout event across the application.
 *
 * @property repository Authentication repository executing the sign-out operation.
 * @property eventBus Application-wide event bus notified when sign-out succeeds.
 */
class SignOutUseCase(
    private val repository: AuthRepository,
    private val eventBus: AppEventBus,
) {
    /**
     * Signs out the active user and emits [AppEvent.UserLoggedOut] upon success.
     *
     * @return A [Resource] wrapping [Unit] on success or a sign-out error.
     */
    suspend operator fun invoke(): Resource<Unit> =
        repository.signOut().onSuccess {
            eventBus.emit(AppEvent.UserLoggedOut)
        }
}
