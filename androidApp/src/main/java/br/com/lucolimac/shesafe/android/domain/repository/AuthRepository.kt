package br.com.lucolimac.shesafe.android.domain.repository

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit)
    fun getUserEmail(): String?
    fun getUserName(): String?
    fun getUserPhotoUrl(): String?
}