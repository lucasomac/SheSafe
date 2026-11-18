package br.com.lucolimac.shesafe.feature.auth.domain.usecase

import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case that exposes a reactive stream of the currently authenticated user's profile.
 *
 * @property repository Authentication repository providing session state updates.
 */
class ObserveCurrentUserUseCase(
    private val repository: AuthRepository,
) {
    /**
     * Subscribes to authentication state changes.
     *
     * @return A [Flow] emitting the active [UserProfile], or `null` when no user is signed in.
     */
    operator fun invoke(): Flow<UserProfile?> = repository.observeCurrentUser()
}

/**
 * Use case that synchronously retrieves the currently authenticated user's profile snapshot.
 *
 * @property repository Authentication repository providing the current user snapshot.
 */
class GetCurrentUserUseCase(
    private val repository: AuthRepository,
) {
    /**
     * Fetches the current authenticated user synchronously.
     *
     * @return The active [UserProfile], or `null` if unauthenticated.
     */
    operator fun invoke(): UserProfile? = repository.getCurrentUser()
}
