package br.com.lucolimac.shesafe.android.data.repository

import br.com.lucolimac.shesafe.android.data.source.AuthDataSource
import br.com.lucolimac.shesafe.android.domain.repository.AuthRepository

class AuthRepositoryImpl(private val authDataSource: AuthDataSource) : AuthRepository {
    override fun isUserLoggedIn(): Boolean {
        return authDataSource.isUserLoggedIn()
    }

    override fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit) {
        authDataSource.logoutUser(onLogoutSuccess, onLogoutFailure)
    }
}