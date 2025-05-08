package br.com.lucolimac.shesafe.android.domain.repository

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact

interface SecureContactRepository {
    suspend fun registerSecureContact(secureContact: SecureContact): Boolean
    suspend fun getSecureContactList(): List<SecureContact>
    suspend fun getContactByPhone(phone: String): SecureContact?
    suspend fun updateSecureContact(phone: String, secureContact: SecureContact): Boolean
    suspend fun deleteSecureContact(phone: String): Boolean
}