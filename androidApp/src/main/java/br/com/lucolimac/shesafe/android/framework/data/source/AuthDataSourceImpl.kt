package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.source.AuthDataSource
import br.com.lucolimac.shesafe.android.framework.service.AuthService

class AuthDataSourceImpl(private val authService: AuthService) : AuthDataSource {
    override fun isUserLoggedIn(): Boolean {
        return authService.isUserLoggedIn()
    }

    override fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit) {
        authService.logoutUser(onLogoutSuccess, onLogoutFailure)
    }
}