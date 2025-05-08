package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel

interface SecureContactService {
    suspend fun getSecureContacts(): List<SecureContactModel>

    suspend fun getSecureContactByPhone(phone: String): SecureContactModel?

    suspend fun registerSecureContact(
        secureContactModel: SecureContactModel
    ): Boolean

    suspend fun updateSecureContact(
        phone: String, secureContactModel: SecureContactModel
    ): Boolean

    suspend fun deleteSecureContact(phone: String): Boolean
}