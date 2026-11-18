package br.com.lucolimac.shesafe.feature.contacts.domain.repository

import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import kotlinx.coroutines.flow.Flow

interface SecureContactRepository {
    fun observeSecureContacts(): Flow<List<SecureContact>>
    suspend fun refreshSecureContacts(): Resource<List<SecureContact>>
    suspend fun getSecureContactByPhone(phoneNumber: String): Resource<SecureContact>
    suspend fun registerSecureContact(contact: SecureContact): Resource<Unit>
    suspend fun updateSecureContact(originalPhoneNumber: String, contact: SecureContact): Resource<Unit>
    suspend fun deleteSecureContact(phoneNumber: String): Resource<Unit>
}
