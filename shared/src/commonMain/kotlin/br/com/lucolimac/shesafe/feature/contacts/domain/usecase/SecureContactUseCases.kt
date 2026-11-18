package br.com.lucolimac.shesafe.feature.contacts.domain.usecase

import br.com.lucolimac.shesafe.core.domain.event.AppEvent
import br.com.lucolimac.shesafe.core.domain.event.AppEventBus
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.Resource
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.repository.SecureContactRepository
import kotlinx.coroutines.flow.Flow

/**
 * Required digit count for a valid Brazilian mobile phone number (2-digit DDD + 9-digit subscriber number).
 */
private const val REQUIRED_BR_PHONE_DIGITS = 11

/**
 * Use case that exposes a reactive stream of the current user's trusted emergency contacts.
 *
 * @property repository Repository providing reactive access to stored [SecureContact] records.
 */
class ObserveSecureContactsUseCase(
    private val repository: SecureContactRepository,
) {
    /**
     * Subscribes to changes in the user's trusted contacts list.
     *
     * @return A [Flow] emitting the current list of [SecureContact] items.
     */
    operator fun invoke(): Flow<List<SecureContact>> = repository.observeSecureContacts()
}

/**
 * Use case that triggers synchronization of trusted emergency contacts from remote storage.
 *
 * @property repository Repository responsible for refreshing and caching remote contacts.
 */
class RefreshSecureContactsUseCase(
    private val repository: SecureContactRepository,
) {
    /**
     * Refreshes the user's trusted contacts from the remote backend into local storage.
     *
     * @return A [Resource] containing the synchronized list of [SecureContact] records or an error.
     */
    suspend operator fun invoke(): Resource<List<SecureContact>> = repository.refreshSecureContacts()
}

/**
 * Use case that looks up a single trusted contact by its phone number.
 *
 * @property repository Repository used to query the contact record.
 */
class GetSecureContactByPhoneUseCase(
    private val repository: SecureContactRepository,
) {
    /**
     * Normalizes [phoneNumber] to digits only and fetches the corresponding [SecureContact].
     *
     * @param phoneNumber Raw or formatted phone number string identifying the contact.
     * @return A [Resource] containing the matching [SecureContact] or a lookup error.
     */
    suspend operator fun invoke(phoneNumber: String): Resource<SecureContact> =
        repository.getSecureContactByPhone(phoneNumber.filter(Char::isDigit))
}

/**
 * Use case that validates input fields, creates or updates a [SecureContact], and broadcasts
 * [AppEvent.ContactsChanged] when persistence succeeds.
 *
 * @property repository Repository persisting the new or updated contact.
 * @property eventBus Application-wide event bus notified when the contacts list changes.
 */
class SaveSecureContactUseCase(
    private val repository: SecureContactRepository,
    private val eventBus: AppEventBus,
) {
    /**
     * Validates [name] and [phoneNumber], then registers a new contact or updates an existing one
     * when [originalPhoneNumber] is provided.
     *
     * @param name Contact display name entered by the user.
     * @param phoneNumber Raw or formatted phone number entered by the user.
     * @param originalPhoneNumber Optional previous phone number when editing an existing contact;
     * when `null` or blank, a new contact is registered.
     * @return [Resource.Success] when saved, or [Resource.Error] containing validation or storage errors.
     */
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

/**
 * Use case that removes a trusted emergency contact and emits [AppEvent.ContactsChanged] on success.
 *
 * @property repository Repository executing the contact deletion.
 * @property eventBus Application-wide event bus notified when the contacts list changes.
 */
class DeleteSecureContactUseCase(
    private val repository: SecureContactRepository,
    private val eventBus: AppEventBus,
) {
    /**
     * Normalizes [phoneNumber] to digits only, deletes the corresponding contact, and notifies listeners.
     *
     * @param phoneNumber Raw or formatted phone number of the contact to delete.
     * @return A [Resource] containing [Unit] on success or a deletion error.
     */
    suspend operator fun invoke(phoneNumber: String): Resource<Unit> =
        repository.deleteSecureContact(phoneNumber.filter(Char::isDigit)).onSuccess {
            eventBus.emit(AppEvent.ContactsChanged)
        }
}
