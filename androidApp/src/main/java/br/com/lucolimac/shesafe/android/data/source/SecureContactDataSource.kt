package br.com.lucolimac.shesafe.android.data.source

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel

interface SecureContactDataSource {
    suspend fun getSecureContacts(): List<SecureContactModel>

    suspend fun getSecureContactByPhoneNumber(phoneNumber: String): SecureContactModel?

    suspend fun registerSecureContact(contact: SecureContactModel): Boolean

    suspend fun updateSecureContact(phoneNumber: String, contact: SecureContactModel): Boolean

    suspend fun deleteSecureContact(phoneNumber: String): Boolean
}