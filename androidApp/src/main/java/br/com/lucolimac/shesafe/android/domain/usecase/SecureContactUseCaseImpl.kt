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
    override fun getSecureContact(): Flow<List<SecureContact>> {
        return flow {
            emit(secureContactRepository.getSecureContactList())
        }.flowOn(coroutineDispatcher)
    }

    override fun getSecureContactByPhone(phone: String): Flow<SecureContact?> {
        return flow {
            emit(secureContactRepository.getContactByPhone(phone))
        }.flowOn(coroutineDispatcher)
    }

    override fun registerSecureContact(contact: SecureContact): Flow<Boolean> {
        return flow {
            emit(secureContactRepository.registerSecureContact(contact))
        }.flowOn(coroutineDispatcher)
    }

    override fun updateSecureContact(
        phone: String, contact: SecureContact
    ): Flow<Boolean> {
        return flow {
            emit(secureContactRepository.updateSecureContact(phone, contact))
        }.flowOn(coroutineDispatcher)
    }

    override fun deleteSecureContact(phone: String): Flow<Boolean> {
        return flow {
            emit(secureContactRepository.deleteSecureContact(phone))
        }.flowOn(coroutineDispatcher)
    }
}