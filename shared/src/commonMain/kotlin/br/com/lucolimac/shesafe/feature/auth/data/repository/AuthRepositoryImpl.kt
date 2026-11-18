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

class AuthRepositoryImpl : AuthRepository {
    override fun observeCurrentUser(): Flow<UserProfile?> =
        runCatching { Firebase.auth.authStateChanged }
            .getOrElse { kotlinx.coroutines.flow.flowOf(null) }
            .map { user -> user?.toDomain() }
            .catch { throwable ->
                logWarn("AuthRepository", "observeCurrentUser failed: ${throwable.message}")
                emit(null)
            }

    override fun getCurrentUser(): UserProfile? =
        runCatching { Firebase.auth.currentUser?.toDomain() }
            .getOrNull()

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

fun FirebaseUser.toDomain(): UserProfile = UserProfile(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoURL,
)
