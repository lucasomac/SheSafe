package br.com.lucolimac.shesafe.android.data.source

interface AuthDataSource {
    fun isUserLoggedIn(): Boolean

    fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit)
}