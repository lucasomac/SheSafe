package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.repository.SecureContactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SecureContactUseCaseImpl(
    private val secureContactRepository: SecureContactRepository
) : SecureContactUseCase {

    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
    override fun getSecureContacts(): Flow<List<SecureContact>> {
        return flow {
            emit(secureContactRepository.getSecureContactList())
        }.flowOn(coroutineDispatcher)
    }

    override fun getSecureContactByPhoneNumber(phoneNumber: String): Flow<SecureContact?> {
        return flow {
            emit(secureContactRepository.getContactByPhoneNumber(phoneNumber))
        }.flowOn(coroutineDispatcher)
    }

    override fun registerSecureContact(contact: SecureContact): Flow<Boolean> {
        return flow {
            emit(secureContactRepository.registerSecureContact(contact))
        }.flowOn(coroutineDispatcher)
    }

    override fun updateSecureContact(
        phoneNumber: String, contact: SecureContact
    ): Flow<Boolean> {
        return flow {
            emit(secureContactRepository.updateSecureContact(phoneNumber, contact))
        }.flowOn(coroutineDispatcher)
    }

    override fun deleteSecureContact(phoneNumber: String): Flow<Boolean> {
        return flow {
            emit(secureContactRepository.deleteSecureContact(phoneNumber))
        }.flowOn(coroutineDispatcher)
    }
}