package br.com.lucolimac.shesafe.feature.auth.domain.usecase

import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveCurrentUserUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke(): Flow<UserProfile?> = repository.observeCurrentUser()
}

class GetCurrentUserUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke(): UserProfile? = repository.getCurrentUser()
}
