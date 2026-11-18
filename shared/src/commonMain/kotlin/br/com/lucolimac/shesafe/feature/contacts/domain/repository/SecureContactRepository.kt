package br.com.lucolimac.shesafe.feature.contacts.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import kotlinx.coroutines.flow.Flow

/**
 * Contract for reading, synchronizing, and mutating the authenticated user's trusted emergency contacts.
 */
interface SecureContactRepository {
    /**
     * Observes the list of trusted emergency contacts associated with the currently signed-in user.
     *
     * @return A [Flow] emitting the latest list of [SecureContact] entries, or an empty list when signed out.
     */
    fun observeSecureContacts(): Flow<List<SecureContact>>

    /**
     * Synchronizes the local emergency contacts cache with the remote cloud data source.
     *
     * @return A [Resource] containing the refreshed list of [SecureContact] items, or an error on failure.
     */
    suspend fun refreshSecureContacts(): Resource<List<SecureContact>>

    /**
     * Retrieves a single trusted contact matching the given [phoneNumber].
     *
     * @param phoneNumber Normalized digits-only phone number identifying the contact.
     * @return A [Resource] wrapping the matching [SecureContact], or an error if not found or unreadable.
     */
    suspend fun getSecureContactByPhone(phoneNumber: String): Resource<SecureContact>

    /**
     * Persists a newly created trusted emergency contact locally and schedules remote synchronization.
     *
     * @param contact The [SecureContact] to register for the current user.
     * @return A [Resource] wrapping [Unit] on success, or an error if persistence fails.
     */
    suspend fun registerSecureContact(contact: SecureContact): Resource<Unit>

    /**
     * Updates an existing trusted emergency contact, handling phone number key changes if modified.
     *
     * @param originalPhoneNumber Normalized phone number of the contact prior to editing.
     * @param contact Updated [SecureContact] details to persist.
     * @return A [Resource] wrapping [Unit] on success, or an error if persistence fails.
     */
    suspend fun updateSecureContact(originalPhoneNumber: String, contact: SecureContact): Resource<Unit>

    /**
     * Deletes a trusted emergency contact identified by [phoneNumber] from local and remote storage.
     *
     * @param phoneNumber Normalized digits-only phone number of the contact to remove.
     * @return A [Resource] wrapping [Unit] on success, or an error if deletion fails.
     */
    suspend fun deleteSecureContact(phoneNumber: String): Resource<Unit>
}
