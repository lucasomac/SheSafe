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

private const val COLLECTION_SECURE_CONTACTS = "secureContacts"
private const val SUBCOLLECTION_CONTACTS = "contacts"
private const val FIELD_PHONE_NUMBER = "phoneNumber"

class SecureContactRepositoryImpl(
    private val contactDao: SecureContactDao,
    private val authRepository: AuthRepository,
) : SecureContactRepository {

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
