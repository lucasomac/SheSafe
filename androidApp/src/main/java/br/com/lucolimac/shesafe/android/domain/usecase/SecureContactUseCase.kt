package br.com.lucolimac.shesafe.android.domain.usecase

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import kotlinx.coroutines.flow.Flow

interface SecureContactUseCase {
    fun getSecureContacts(): Flow<List<SecureContact>>
    fun getSecureContactByPhoneNumber(phoneNumber: String): Flow<SecureContact?>
    fun registerSecureContact(contact: SecureContact): Flow<Boolean>
    fun updateSecureContact(phoneNumber: String, contact: SecureContact): Flow<Boolean>
    fun deleteSecureContact(phoneNumber: String): Flow<Boolean>
}