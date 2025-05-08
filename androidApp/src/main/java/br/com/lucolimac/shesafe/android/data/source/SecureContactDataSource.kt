package br.com.lucolimac.shesafe.android.data.source

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel

interface SecureContactDataSource {
    suspend fun getSecureContacts(): List<SecureContactModel>

    suspend fun getSecureContactByPhone(phone: String): SecureContactModel?

    suspend fun registerSecureContact(contact: SecureContactModel): Boolean

    suspend fun updateSecureContact(phone: String, contact: SecureContactModel): Boolean

    suspend fun deleteSecureContact(phone: String): Boolean
}