package br.com.lucolimac.shesafe.feature.contacts.data.repository

import br.com.lucolimac.shesafe.core.database.dao.SecureContactDao
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.logging.logWarn
import br.com.lucolimac.shesafe.feature.auth.domain.repository.AuthRepository
import br.com.lucolimac.shesafe.feature.contacts.data.mapper.SecureContactFirestoreDto
import br.com.lucolimac.shesafe.feature.contacts.data.mapper.toDomain
import br.com.lucolimac.shesafe.feature.contacts.data.mapper.toEntity
import br.com.lucolimac.shesafe.feature.contacts.data.mapper.toFirestoreDto
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.repository.SecureContactRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** Top-level Cloud Firestore collection storing per-user secure contact documents. */
private const val COLLECTION_SECURE_CONTACTS = "secureContacts"

/** Subcollection under each user document storing individual contact records. */
private const val SUBCOLLECTION_CONTACTS = "contacts"

/** Firestore document field name used to query contacts by phone number. */
private const val FIELD_PHONE_NUMBER = "phoneNumber"

/**
 * Offline-first implementation of [SecureContactRepository] backed by Room (`SecureContactDao`)
 * as the single source of truth and Cloud Firestore for cross-device synchronization.
 *
 * Writes are committed to Room immediately so emergency alerts remain functional offline,
 * while remote Firestore mutations are attempted on a best-effort basis.
 *
 * @property contactDao Local Room DAO managing cached secure contact entities.
 * @property authRepository Authentication repository identifying the active user's email scope.
 */
class SecureContactRepositoryImpl(
    private val contactDao: SecureContactDao,
    private val authRepository: AuthRepository,
) : SecureContactRepository {

    /**
     * Observes the active user's cached contacts in Room, automatically switching streams when
     * the authenticated user changes and emitting an empty list when unauthenticated.
     *
     * @return A [Flow] emitting the mapped list of [SecureContact] domain models.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeSecureContacts(): Flow<List<SecureContact>> =
        authRepository.observeCurrentUser().flatMapLatest { user ->
            val email = user?.email
            if (email.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                contactDao.observeByUser(email).map { entities ->
                    entities.map { it.toDomain() }
                }
            }
        }

    /**
     * Fetches the user's contacts from Cloud Firestore and replaces the local Room cache.
     *
     * Falls back to existing Room records if the remote fetch fails and cached entries are available.
     *
     * @return [Resource.Success] with the latest or cached [SecureContact] list, or [Resource.Error]
     * if unauthenticated or offline with an empty cache.
     */
    override suspend fun refreshSecureContacts(): Resource<List<SecureContact>> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            val snapshot = Firebase.firestore
                .collection(COLLECTION_SECURE_CONTACTS)
                .document(email)
                .collection(SUBCOLLECTION_CONTACTS)
                .get()
            val remoteContacts = snapshot.documents.mapNotNull { doc ->
                runCatching { doc.data(SecureContactFirestoreDto.serializer()).toDomain() }
                    .getOrNull()
                    ?.takeIf { it.phoneNumber.isNotBlank() }
            }
            contactDao.replaceAllForUser(
                userEmail = email,
                entities = remoteContacts.map { it.toEntity(email) },
            )
            Resource.Success(remoteContacts)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("SecureContactRepo", "Remote refresh failed, serving local Room cache: ${e.message}")
            val cached = contactDao.getAllByUser(email).map { it.toDomain() }
            if (cached.isNotEmpty()) {
                Resource.Success(cached)
            } else {
                Resource.Error(AppError.Network.NoConnection)
            }
        }
    }

    /**
     * Looks up a contact by [phoneNumber] in the local Room cache first, falling back to a
     * Firestore query and caching the result locally when found remotely.
     *
     * @param phoneNumber Normalized digits-only phone number to search for.
     * @return [Resource.Success] with the found [SecureContact], or [Resource.Error] if unauthenticated,
     * missing ([AppError.Database.NotFound]), or unreadable.
     */
    override suspend fun getSecureContactByPhone(phoneNumber: String): Resource<SecureContact> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        val local = contactDao.getByPhone(email, phoneNumber)?.toDomain()
        if (local != null) {
            return Resource.Success(local)
        }
        return try {
            val snapshot = Firebase.firestore
                .collection(COLLECTION_SECURE_CONTACTS)
                .document(email)
                .collection(SUBCOLLECTION_CONTACTS)
                .where { FIELD_PHONE_NUMBER equalTo phoneNumber }
                .get()
            val contact = snapshot.documents.firstOrNull()
                ?.data(SecureContactFirestoreDto.serializer())
                ?.toDomain()
                ?: return Resource.Error(AppError.Database.NotFound)
            contactDao.upsert(contact.toEntity(email))
            Resource.Success(contact)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            logWarn("SecureContactRepo", "getSecureContactByPhone failed: ${e.message}")
            Resource.Error(AppError.Database.ReadFailure(e.message))
        }
    }

    /**
     * Inserts or updates [contact] in the local Room cache first for offline availability,
     * then attempts to write the contact document to Cloud Firestore.
     *
     * @param contact The [SecureContact] to persist for the current user.
     * @return [Resource.Success] once saved locally, or [Resource.Error] if unauthenticated or local write fails.
     */
    override suspend fun registerSecureContact(contact: SecureContact): Resource<Unit> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            // Write to local Room cache first for offline availability
            contactDao.upsert(contact.toEntity(email))
            runCatching {
                Firebase.firestore
                    .collection(COLLECTION_SECURE_CONTACTS)
                    .document(email)
                    .collection(SUBCOLLECTION_CONTACTS)
                    .document(contact.phoneNumber)
                    .set(SecureContactFirestoreDto.serializer(), contact.toFirestoreDto())
            }.onFailure {
                logWarn("SecureContactRepo", "Remote contact sync deferred: ${it.message}")
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(AppError.Database.WriteFailure(e.message))
        }
    }

    /**
     * Replaces an existing contact identified by [originalPhoneNumber] with [contact] in Room,
     * removing the old phone key if changed, and mirrors the update to Cloud Firestore.
     *
     * @param originalPhoneNumber Normalized phone number of the contact prior to modification.
     * @param contact Updated [SecureContact] data to persist.
     * @return [Resource.Success] once updated locally, or [Resource.Error] if unauthenticated or local write fails.
     */
    override suspend fun updateSecureContact(
        originalPhoneNumber: String,
        contact: SecureContact,
    ): Resource<Unit> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            if (originalPhoneNumber != contact.phoneNumber) {
                contactDao.deleteByPhone(email, originalPhoneNumber)
            }
            contactDao.upsert(contact.toEntity(email))

            runCatching {
                val contactsRef = Firebase.firestore
                    .collection(COLLECTION_SECURE_CONTACTS)
                    .document(email)
                    .collection(SUBCOLLECTION_CONTACTS)
                val existingDocs = contactsRef
                    .where { FIELD_PHONE_NUMBER equalTo originalPhoneNumber }
                    .get()
                    .documents
                if (existingDocs.isNotEmpty()) {
                    existingDocs.forEach { doc -> doc.reference.delete() }
                }
                contactsRef
                    .document(contact.phoneNumber)
                    .set(SecureContactFirestoreDto.serializer(), contact.toFirestoreDto())
            }.onFailure {
                logWarn("SecureContactRepo", "Remote contact update deferred: ${it.message}")
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(AppError.Database.WriteFailure(e.message))
        }
    }

    /**
     * Deletes the contact matching [phoneNumber] from the local Room cache and attempts to remove
     * matching documents from Cloud Firestore.
     *
     * @param phoneNumber Normalized digits-only phone number of the contact to delete.
     * @return [Resource.Success] once removed locally, or [Resource.Error] if unauthenticated or local delete fails.
     */
    override suspend fun deleteSecureContact(phoneNumber: String): Resource<Unit> {
        val email = authRepository.getCurrentUser()?.email
            ?: return Resource.Error(AppError.Auth.Unauthenticated)
        return try {
            contactDao.deleteByPhone(email, phoneNumber)
            runCatching {
                val contactsRef = Firebase.firestore
                    .collection(COLLECTION_SECURE_CONTACTS)
                    .document(email)
                    .collection(SUBCOLLECTION_CONTACTS)
                val docs = contactsRef
                    .where { FIELD_PHONE_NUMBER equalTo phoneNumber }
                    .get()
                    .documents
                docs.forEach { it.reference.delete() }
            }.onFailure {
                logWarn("SecureContactRepo", "Remote contact delete deferred: ${it.message}")
            }
            Resource.Success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resource.Error(AppError.Database.WriteFailure(e.message))
        }
    }
}
