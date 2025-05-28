package br.com.lucolimac.shesafe.android.domain.repository

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact

interface SecureContactRepository {
    suspend fun registerSecureContact(secureContact: SecureContact): Boolean
    suspend fun getSecureContactList(): List<SecureContact>
    suspend fun getContactByPhoneNumber(phoneNumber: String): SecureContact?
    suspend fun updateSecureContact(phoneNumber: String, secureContact: SecureContact): Boolean
    suspend fun deleteSecureContact(phoneNumber: String): Boolean
}