package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthUseCaseImpl(private val authRepository: AuthRepository) : AuthUseCase {
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun isUserLoggedIn(): Flow<Boolean> {
        return flow { emit(authRepository.isUserLoggedIn()) }.flowOn(
            coroutineDispatcher
        )
    }

    override fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit) {
        authRepository.logoutUser(onLogoutSuccess, onLogoutFailure)
    }

    override fun getUserEmail(): Flow<String?> {
        return flow { emit(authRepository.getUserEmail()) }.flowOn(coroutineDispatcher)
    }

    override fun getUserName(): Flow<String?> {
        return flow { emit(authRepository.getUserName()) }.flowOn(coroutineDispatcher)
    }

    override fun getUserPhotoUrl(): Flow<String?> {
        return flow { emit(authRepository.getUserPhotoUrl()) }.flowOn(coroutineDispatcher)
    }
}