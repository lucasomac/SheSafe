package br.com.lucolimac.shesafe.auth

private class AuthUnavailableService : AuthService {
    override fun isUserLoggedIn(): Boolean = false

    override fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit) {
        onLogoutSuccess()
    }

    override fun getUserEmail(): String? = null

    override fun getUserName(): String? = null

    override fun getUserPhotoUrl(): String? = null
}

actual fun platformAuthService(): AuthService = AuthUnavailableService()
