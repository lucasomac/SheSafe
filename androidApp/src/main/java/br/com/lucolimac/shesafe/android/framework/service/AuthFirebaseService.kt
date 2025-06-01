package br.com.lucolimac.shesafe.android.framework.service

import com.google.firebase.auth.FirebaseAuth

class AuthFirebaseService(private val firebaseAuth: FirebaseAuth) : AuthService {
    override fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null // Check if a user is currently logged in
    }

    override fun logoutUser(onLogoutSuccess: () -> Unit, onLogoutFailure: (Exception) -> Unit) {
        try {
            firebaseAuth.signOut() // Sign out the user
            onLogoutSuccess() // Notify success
        } catch (e: Exception) {
            onLogoutFailure(e) // Notify failure
        }
    }

    override fun getUserEmail(): String? {
        return firebaseAuth.currentUser?.email // Get the email of the currently logged-in user
    }

    override fun getUserName(): String? {
        return firebaseAuth.currentUser?.displayName // Get the display name of the currently logged-in user
    }

    override fun getUserPhotoUrl(): String? {
        return firebaseAuth.currentUser?.photoUrl?.toString() // Get the photo URL of the currently logged-in user
    }
}