package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel
import br.com.lucolimac.shesafe.android.data.source.SecureContactDataSource
import br.com.lucolimac.shesafe.android.framework.service.SecureContactService

class SecureSecureContactDataSourceImpl(private val secureContactService: SecureContactService) :
    SecureContactDataSource {
    override suspend fun getSecureContacts(): List<SecureContactModel> {
        return secureContactService.getSecureContacts()
    }

    override suspend fun getSecureContactByPhoneNumber(phoneNumber: String): SecureContactModel? {
        return secureContactService.getSecureContactByPhoneNumber(phoneNumber)
    }

    override suspend fun registerSecureContact(contact: SecureContactModel): Boolean {
        return secureContactService.registerSecureContact(contact)
    }

    override suspend fun updateSecureContact(
        phoneNumber: String, contact: SecureContactModel
    ): Boolean {
        return secureContactService.updateSecureContact(phoneNumber, contact)
    }

    override suspend fun deleteSecureContact(phoneNumber: String): Boolean {
        return secureContactService.deleteSecureContact(phoneNumber)
    }
}