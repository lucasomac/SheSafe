package br.com.lucolimac.shesafe.android.domain.usecase

import kotlinx.coroutines.flow.Flow

interface AuthUseCase {
    fun isUserLoggedIn(): Flow<Boolean>
    fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit)
    fun getUserEmail(): Flow<String?>
    fun getUserName(): Flow<String?>
    fun getUserPhotoUrl(): Flow<String?>
}