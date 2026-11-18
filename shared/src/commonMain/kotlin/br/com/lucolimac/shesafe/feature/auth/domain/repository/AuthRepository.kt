package br.com.lucolimac.shesafe.feature.auth.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeCurrentUser(): Flow<UserProfile?>
    fun getCurrentUser(): UserProfile?
    suspend fun signInWithGoogleTokens(idToken: String, accessToken: String?): Resource<UserProfile>
    suspend fun signOut(): Resource<Unit>
}
