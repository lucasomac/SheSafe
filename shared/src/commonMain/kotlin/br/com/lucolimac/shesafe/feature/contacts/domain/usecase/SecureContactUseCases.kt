package br.com.lucolimac.shesafe.feature.contacts.domain.usecase

import br.com.lucolimac.shesafe.core.domain.event.AppEvent
import br.com.lucolimac.shesafe.core.domain.event.AppEventBus
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.repository.SecureContactRepository
import kotlinx.coroutines.flow.Flow

private const val REQUIRED_BR_PHONE_DIGITS = 11

class ObserveSecureContactsUseCase(
    private val repository: SecureContactRepository,
) {
    operator fun invoke(): Flow<List<SecureContact>> = repository.observeSecureContacts()
}

class RefreshSecureContactsUseCase(
    private val repository: SecureContactRepository,
) {
    suspend operator fun invoke(): Resource<List<SecureContact>> = repository.refreshSecureContacts()
}

class GetSecureContactByPhoneUseCase(
    private val repository: SecureContactRepository,
) {
    suspend operator fun invoke(phoneNumber: String): Resource<SecureContact> =
        repository.getSecureContactByPhone(phoneNumber.filter(Char::isDigit))
}

class SaveSecureContactUseCase(
    private val repository: SecureContactRepository,
    private val eventBus: AppEventBus,
) {
    suspend operator fun invoke(
        name: String,
        phoneNumber: String,
        originalPhoneNumber: String? = null,
    ): Resource<Unit> {
        val cleanName = name.trim()
        val cleanDigits = phoneNumber.filter(Char::isDigit)
        if (cleanName.isBlank()) {
            return Resource.Error(AppError.Validation.EmptyName)
        }
        if (cleanDigits.isBlank()) {
            return Resource.Error(AppError.Validation.EmptyPhone)
        }
        if (cleanDigits.length < REQUIRED_BR_PHONE_DIGITS) {
            return Resource.Error(AppError.Validation.InvalidPhoneDigits)
        }

        val contact = SecureContact(name = cleanName, phoneNumber = cleanDigits)
        val result = if (originalPhoneNumber.isNullOrBlank()) {
            repository.registerSecureContact(contact)
        } else {
            repository.updateSecureContact(originalPhoneNumber.filter(Char::isDigit), contact)
        }
        return result.onSuccess {
            eventBus.emit(AppEvent.ContactsChanged)
        }
    }
}

class DeleteSecureContactUseCase(
    private val repository: SecureContactRepository,
    private val eventBus: AppEventBus,
) {
    suspend operator fun invoke(phoneNumber: String): Resource<Unit> =
        repository.deleteSecureContact(phoneNumber.filter(Char::isDigit)).onSuccess {
            eventBus.emit(AppEvent.ContactsChanged)
        }
}
