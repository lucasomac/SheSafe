package br.com.lucolimac.shesafe.auth

import com.google.firebase.auth.FirebaseAuth

private class AuthFirebaseService(
    private val firebaseAuth: FirebaseAuth,
) : AuthService {
    override fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    override fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit) {
        try {
            firebaseAuth.signOut()
            onLogoutSuccess()
        } catch (exception: Exception) {
            onLogoutFailure(exception)
        }
    }

    override fun getUserEmail(): String? = firebaseAuth.currentUser?.email

    override fun getUserName(): String? = firebaseAuth.currentUser?.displayName

    override fun getUserPhotoUrl(): String? = firebaseAuth.currentUser?.photoUrl?.toString()
}

actual fun platformAuthService(): AuthService = AuthFirebaseService(FirebaseAuth.getInstance())
