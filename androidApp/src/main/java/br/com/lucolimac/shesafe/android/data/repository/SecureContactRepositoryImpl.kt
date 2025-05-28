package br.com.lucolimac.shesafe.android.data.repository

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel
import br.com.lucolimac.shesafe.android.data.source.SecureContactDataSource
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.repository.SecureContactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class SecureContactRepositoryImpl(
    private val secureContactDataSource: SecureContactDataSource
) : SecureContactRepository {
    override suspend fun registerSecureContact(secureContact: SecureContact): Boolean {
        return secureContactDataSource.registerSecureContact(
            SecureContactModel.fromEntity(secureContact)
        )
    }

    override suspend fun getSecureContactList(): List<SecureContact> {
        return secureContactDataSource.getSecureContacts().map { it.toEntity() }
    }

    override suspend fun getContactByPhoneNumber(phoneNumber: String): SecureContact? {
        return secureContactDataSource.getSecureContactByPhoneNumber(phoneNumber)?.toEntity()
    }

    override suspend fun updateSecureContact(phoneNumber: String, secureContact: SecureContact): Boolean {
        return secureContactDataSource.updateSecureContact(
            phoneNumber, SecureContactModel.fromEntity(secureContact)
        )
    }

    override suspend fun deleteSecureContact(phoneNumber: String): Boolean {
        return secureContactDataSource.deleteSecureContact(phoneNumber)
    }
}