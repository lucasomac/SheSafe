package br.com.lucolimac.shesafe.android.domain.usecase

import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun isUserLoggedIn(): Flow<Boolean>

    fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit)
}