package br.com.lucolimac.shesafe.feature.auth.data.repository

import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Firebase-backed implementation of [AuthRepository] using the GitLive Firebase Auth SDK.
 *
 * Handles Google credential exchange, session state observation, and graceful fallback when
 * Firebase Auth is unavailable or encounters runtime errors.
 */
class AuthRepositoryImpl : AuthRepository {
    /**
     * Observes Firebase authentication state changes and maps the active user to [UserProfile].
     *
     * Emits `null` if Firebase is uninitialized or if the underlying stream throws an error.
     *
     * @return A [Flow] emitting the current [UserProfile] or `null` when unauthenticated.
     */
    override fun observeCurrentUser(): Flow<UserProfile?> =
        runCatching { Firebase.auth.authStateChanged }
            .getOrElse { kotlinx.coroutines.flow.flowOf(null) }
            .map { user -> user?.toDomain() }
            .catch { throwable ->
                logWarn("AuthRepository", "observeCurrentUser failed: ${throwable.message}")
                emit(null)
            }

    /**
     * Reads the current Firebase user synchronously, returning `null` if unauthenticated or unavailable.
     *
     * @return The active [UserProfile], or `null` when signed out.
     */
    override fun getCurrentUser(): UserProfile? =
        runCatching { Firebase.auth.currentUser?.toDomain() }
            .getOrNull()

    /**
     * Exchanges Google OAuth tokens for a Firebase credential and signs the user in.
     *
     * @param idToken OpenID Connect ID token issued by Google.
     * @param accessToken Optional OAuth 2.0 access token issued by Google.
     * @return [Resource.Success] with the resulting [UserProfile], or [Resource.Error] with
     * [AppError.Auth.SignInFailed] when authentication fails.
     */
    override suspend fun signInWithGoogleTokens(
        idToken: String,
        accessToken: String?,
    ): Resource<UserProfile> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken = idToken, accessToken = accessToken)
            val result = Firebase.auth.signInWithCredential(credential)
            val user = result.user?.toDomain()
                ?: return Resource.Error(AppError.Auth.SignInFailed("Missing Firebase user after Google Sign-In"))
            Resource.Success(user)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("AuthRepository", "signInWithGoogleTokens failed: ${e.message}")
            Resource.Error(AppError.Auth.SignInFailed(e.message))
        }
    }

    /**
     * Signs the current user out of Firebase Authentication.
     *
     * @return [Resource.Success] on completion, or [Resource.Error] with
     * [AppError.Auth.SignOutFailed] when sign-out fails.
     */
    override suspend fun signOut(): Resource<Unit> {
        return try {
            Firebase.auth.signOut()
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("AuthRepository", "signOut failed: ${e.message}")
            Resource.Error(AppError.Auth.SignOutFailed(e.message))
        }
    }
}

/**
 * Maps a GitLive [FirebaseUser] instance into a domain [UserProfile] model.
 *
 * @return The mapped [UserProfile] containing the user's UID, email, display name, and photo URL.
 */
fun FirebaseUser.toDomain(): UserProfile = UserProfile(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoURL,
)
