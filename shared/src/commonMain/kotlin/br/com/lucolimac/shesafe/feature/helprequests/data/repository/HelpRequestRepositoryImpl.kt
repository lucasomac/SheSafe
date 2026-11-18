package br.com.lucolimac.shesafe.feature.helprequests.data.repository

import br.com.lucolimac.shesafe.core.database.dao.HelpRequestDao
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.feature.helprequests.data.mapper.HelpRequestFirestoreDto
import br.com.lucolimac.shesafe.feature.helprequests.data.mapper.toDomain
import br.com.lucolimac.shesafe.feature.helprequests.data.mapper.toEntity
import br.com.lucolimac.shesafe.feature.helprequests.data.mapper.toFirestoreDto
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.feature.helprequests.domain.repository.HelpRequestRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** Top-level Firestore collection storing per-user help request documents. */
private const val COLLECTION_HELP_REQUESTS = "helpRequests"

/** Subcollection under each user document holding individual [HelpRequestFirestoreDto] entries. */
private const val SUBCOLLECTION_HELPS = "helps"

/**
 * Offline-first implementation of [HelpRequestRepository] backed by Room ([HelpRequestDao]) as the
 * single source of truth and synchronized with Cloud Firestore.
 *
 * @param helpRequestDao Local Room DAO used to observe and cache help request entities.
 * @param authRepository Repository providing the currently authenticated user's identity.
 */
class HelpRequestRepositoryImpl(
    private val helpRequestDao: HelpRequestDao,
    private val authRepository: AuthRepository,
) : HelpRequestRepository {

    /**
     * Observes the authenticated user's cached help requests ordered by Room query semantics.
     *
     * Emits an empty list whenever no user with a valid email is signed in.
     *
     * @return A reactive [Flow] of [HelpRequest] items for the active user.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeHelpRequests(): Flow<List<HelpRequest>> =
        authRepository.observeCurrentUser().flatMapLatest { user ->
            val email = user?.email
            if (email.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                helpRequestDao.observeByUser(email).map { entities ->
                    entities.map { it.toDomain() }
                }
            }
        }

    /**
     * Pulls the authenticated user's help requests from Cloud Firestore and replaces the local Room cache.
     *
     * @return A [Resource] containing the refreshed list sorted newest-first, or an [AppError] on auth
     * or network failure while preserving cached Room entries.
     */
    override suspend fun refreshHelpRequests(): Resource<List<HelpRequest>> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            val snapshot = Firebase.firestore
                .collection(COLLECTION_HELP_REQUESTS)
                .document(email)
                .collection(SUBCOLLECTION_HELPS)
                .get()
            val remoteRequests = snapshot.documents.mapNotNull { doc ->
                runCatching { doc.data(HelpRequestFirestoreDto.serializer()).toDomain(doc.id) }
                    .getOrNull()
            }.sortedByDescending { it.createdAt }
            helpRequestDao.replaceAllForUser(
                userEmail = email,
                entities = remoteRequests.map { it.toEntity(email) },
            )
            Resource.Success(remoteRequests)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("HelpRequestRepo", "Remote refresh failed, using Room cache: ${e.message}")
            Resource.Error(AppError.Network.NoConnection)
        }
    }

    /**
     * Persists a newly triggered [helpRequest] locally in Room immediately and attempts best-effort
     * synchronization to Cloud Firestore.
     *
     * @param helpRequest The emergency help request record to store.
     * @return [Resource.Success] once persisted locally, or [Resource.Error] if unauthenticated or local write fails.
     */
    override suspend fun registerHelpRequest(helpRequest: HelpRequest): Resource<Unit> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            // Persist locally immediately
            helpRequestDao.upsert(helpRequest.toEntity(email))
            runCatching {
                Firebase.firestore
                    .collection(COLLECTION_HELP_REQUESTS)
                    .document(email)
                    .collection(SUBCOLLECTION_HELPS)
                    .add(HelpRequestFirestoreDto.serializer(), helpRequest.toFirestoreDto())
            }.onFailure {
                logWarn("HelpRequestRepo", "Remote help request sync deferred: ${it.message}")
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(AppError.Database.WriteFailure(e.message))
        }
    }
}
