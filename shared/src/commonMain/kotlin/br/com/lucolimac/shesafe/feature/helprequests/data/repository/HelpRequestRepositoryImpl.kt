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

private const val COLLECTION_HELP_REQUESTS = "helpRequests"
private const val SUBCOLLECTION_HELPS = "helps"

class HelpRequestRepositoryImpl(
    private val helpRequestDao: HelpRequestDao,
    private val authRepository: AuthRepository,
) : HelpRequestRepository {

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
