package br.com.lucolimac.shesafe.feature.auth.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Contract for managing user authentication state and session operations.
 */
interface AuthRepository {
    /**
     * Observes real-time changes to the currently authenticated user session.
     *
     * @return A cold or hot [Flow] emitting the active [UserProfile], or `null` when signed out.
     */
    fun observeCurrentUser(): Flow<UserProfile?>

    /**
     * Synchronously retrieves a snapshot of the currently authenticated user, if any.
     *
     * @return The current [UserProfile], or `null` if no user is authenticated.
     */
    fun getCurrentUser(): UserProfile?

    /**
     * Authenticates a user with Firebase using credentials obtained from Google Sign-In.
     *
     * @param idToken OpenID Connect ID token issued by Google.
     * @param accessToken Optional OAuth 2.0 access token issued by Google.
     * @return A [Resource] containing the authenticated [UserProfile] on success, or an error on failure.
     */
    suspend fun signInWithGoogleTokens(idToken: String, accessToken: String?): Resource<UserProfile>

    /**
     * Terminates the current authenticated user session.
     *
     * @return A [Resource] containing [Unit] on success, or an error if sign-out fails.
     */
    suspend fun signOut(): Resource<Unit>
}
