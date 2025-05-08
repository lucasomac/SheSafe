package br.com.lucolimac.shesafe.android.framework.data.source

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel
import br.com.lucolimac.shesafe.android.data.source.SecureContactDataSource
import br.com.lucolimac.shesafe.android.framework.service.SecureContactService

class SecureSecureContactDataSourceImpl(private val secureContactService: SecureContactService) :
    SecureContactDataSource {
    override suspend fun getSecureContacts(): List<SecureContactModel> {
        return secureContactService.getSecureContacts()
    }

    override suspend fun getSecureContactByPhone(phone: String): SecureContactModel? {
        return secureContactService.getSecureContactByPhone(phone)
    }

    override suspend fun registerSecureContact(contact: SecureContactModel): Boolean {
        return secureContactService.registerSecureContact(contact)
    }

    override suspend fun updateSecureContact(
        phone: String, contact: SecureContactModel
    ): Boolean {
        return secureContactService.updateSecureContact(phone, contact)
    }

    override suspend fun deleteSecureContact(phone: String): Boolean {
        return secureContactService.deleteSecureContact(phone)
    }
}