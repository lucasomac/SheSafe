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

    override suspend fun getContactByPhone(phone: String): SecureContact? {
        return secureContactDataSource.getSecureContactByPhone(phone)?.toEntity()
    }

    override suspend fun updateSecureContact(phone: String, secureContact: SecureContact): Boolean {
        return secureContactDataSource.updateSecureContact(
            phone, SecureContactModel.fromEntity(secureContact)
        )
    }

    override suspend fun deleteSecureContact(phone: String): Boolean {
        return secureContactDataSource.deleteSecureContact(phone)
    }
}