package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel

interface SecureContactService {
    suspend fun getSecureContacts(): List<SecureContactModel>

    suspend fun getSecureContactByPhoneNumber(phoneNumber: String): SecureContactModel?

    suspend fun registerSecureContact(
        secureContactModel: SecureContactModel
    ): Boolean

    suspend fun updateSecureContact(
        phoneNumber: String, secureContactModel: SecureContactModel
    ): Boolean

    suspend fun deleteSecureContact(phoneNumber: String): Boolean
}