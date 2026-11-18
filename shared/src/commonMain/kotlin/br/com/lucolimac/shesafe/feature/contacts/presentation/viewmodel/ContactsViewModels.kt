package br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.onError
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.core.presentation.toStringResource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.DeleteSecureContactUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.GetSecureContactByPhoneUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.ObserveSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.RefreshSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.SaveSecureContactUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

/**
 * Required and maximum digit count for Brazilian mobile phone numbers in contact forms.
 */
private const val PHONE_DIGITS_MAX = 11

/**
 * Immutable UI state for the trusted emergency contacts list screen.
 *
 * @property isLoading Whether the contacts list is currently loading for the first time.
 * @property contacts List of [SecureContact] entries belonging to the current user.
 * @property contactPendingDelete Optional [SecureContact] awaiting user confirmation in the delete sheet.
 * @property errorResource Optional localized [StringResource] shown when loading fails with no cached items.
 */
data class SecureContactsUiState(
    val isLoading: Boolean = true,
    val contacts: List<SecureContact> = emptyList(),
    val contactPendingDelete: SecureContact? = null,
    val errorResource: StringResource? = null,
)

/**
 * One-off UI side-effect events emitted by [SecureContactsViewModel].
 */
sealed interface SecureContactsEvent {
    /**
     * Instructs the UI to navigate to the edit screen for the contact with [phoneNumber].
     *
     * @property phoneNumber Normalized phone number identifying the contact to edit.
     */
    data class NavigateToEdit(val phoneNumber: String) : SecureContactsEvent

    /**
     * Instructs the UI to display a transient localized feedback message.
     *
     * @property messageRes Localized [StringResource] for the message to display.
     */
    data class ShowMessage(val messageRes: StringResource) : SecureContactsEvent
}

/**
 * ViewModel managing presentation state and user interactions for the trusted contacts list screen.
 *
 * @param observeSecureContactsUseCase Use case streaming cached trusted contacts reactively.
 * @property refreshSecureContactsUseCase Use case synchronizing trusted contacts from remote storage.
 * @property deleteSecureContactUseCase Use case removing a selected trusted contact.
 */
class SecureContactsViewModel(
    observeSecureContactsUseCase: ObserveSecureContactsUseCase,
    private val refreshSecureContactsUseCase: RefreshSecureContactsUseCase,
    private val deleteSecureContactUseCase: DeleteSecureContactUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecureContactsUiState())

    /**
     * Reactive stream of [SecureContactsUiState] observed by the contacts list screen.
     */
    val uiState: StateFlow<SecureContactsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SecureContactsEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Stream of one-shot [SecureContactsEvent] side effects for navigation and error messages.
     */
    val events: SharedFlow<SecureContactsEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeSecureContactsUseCase().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        contacts = list,
                        errorResource = if (list.isNotEmpty()) null else state.errorResource,
                    )
                }
            }
        }
        refresh()
    }

    /**
     * Synchronizes trusted contacts with remote storage and updates loading/error indicators.
     */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = _uiState.value.contacts.isEmpty(), errorResource = null) }
            refreshSecureContactsUseCase()
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false, errorResource = null) }
                }
                .onError { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorResource = if (state.contacts.isEmpty()) error.toStringResource() else null,
                        )
                    }
                }
        }
    }

    /**
     * Emits [SecureContactsEvent.NavigateToEdit] when the user chooses to edit [contact].
     *
     * @param contact The [SecureContact] selected for editing.
     */
    fun onEditContactClicked(contact: SecureContact) {
        viewModelScope.launch {
            _events.emit(SecureContactsEvent.NavigateToEdit(contact.phoneNumber))
        }
    }

    /**
     * Stages [contact] for deletion, prompting the confirmation bottom sheet in the UI.
     *
     * @param contact The [SecureContact] requested for deletion.
     */
    fun onRequestDeleteContact(contact: SecureContact) {
        _uiState.update { it.copy(contactPendingDelete = contact) }
    }

    /**
     * Cancels any staged contact deletion and dismisses the confirmation bottom sheet.
     */
    fun onDismissDeleteSheet() {
        _uiState.update { it.copy(contactPendingDelete = null) }
    }

    /**
     * Executes deletion of the currently staged contact and emits an error message if deletion fails.
     */
    fun onConfirmDeleteContact() {
        val target = _uiState.value.contactPendingDelete ?: return
        _uiState.update { it.copy(contactPendingDelete = null) }
        viewModelScope.launch {
            deleteSecureContactUseCase(target.phoneNumber)
                .onError { error ->
                    _events.emit(SecureContactsEvent.ShowMessage(error.toStringResource()))
                }
        }
    }
}

/**
 * Immutable UI state for creating or editing a trusted emergency contact.
 *
 * @property isLoading Whether an existing contact record is currently being loaded for editing.
 * @property isSaving Whether a save or update operation is currently in progress.
 * @property isEditMode `true` when editing an existing contact; `false` when registering a new one.
 * @property originalPhoneNumber Original normalized phone number when in edit mode, or `null` when creating.
 * @property name Current value of the contact name input field.
 * @property phoneNumber Current digits-only value of the phone number input field.
 * @property nameErrorRes Optional localized validation error [StringResource] for the name field.
 * @property phoneErrorRes Optional localized validation error [StringResource] for the phone field.
 */
data class RegisterContactUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditMode: Boolean = false,
    val originalPhoneNumber: String? = null,
    val name: String = "",
    val phoneNumber: String = "",
    val nameErrorRes: StringResource? = null,
    val phoneErrorRes: StringResource? = null,
) {
    /**
     * Indicates whether all form inputs are valid and the form is ready to be submitted.
     */
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
            phoneNumber.filter(Char::isDigit).length == PHONE_DIGITS_MAX &&
            nameErrorRes == null &&
            phoneErrorRes == null &&
            !isSaving
}

/**
 * One-off UI side-effect events emitted by [RegisterSecureContactViewModel].
 */
sealed interface RegisterContactEvent {
    /**
     * Instructs the UI to navigate back to the previous screen after saving or aborting edit load.
     */
    data object NavigateBack : RegisterContactEvent

    /**
     * Instructs the UI to display a transient localized feedback message.
     *
     * @property messageRes Localized [StringResource] for the message to display.
     */
    data class ShowMessage(val messageRes: StringResource) : RegisterContactEvent
}

/**
 * ViewModel managing form validation, initialization, and persistence for creating or editing
 * a trusted emergency contact.
 *
 * @property getSecureContactByPhoneUseCase Use case loading an existing contact when editing.
 * @property saveSecureContactUseCase Use case validating and persisting new or updated contacts.
 */
class RegisterSecureContactViewModel(
    private val getSecureContactByPhoneUseCase: GetSecureContactByPhoneUseCase,
    private val saveSecureContactUseCase: SaveSecureContactUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterContactUiState())

    /**
     * Reactive stream of [RegisterContactUiState] observed by the contact registration/edit screen.
     */
    val uiState: StateFlow<RegisterContactUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterContactEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Stream of one-shot [RegisterContactEvent] side effects for navigation and user feedback.
     */
    val events: SharedFlow<RegisterContactEvent> = _events.asSharedFlow()

    private var loadedForPhone: String? = null

    /**
     * Initializes the form in create mode when [phoneNumberArg] is `null` or blank, or loads the
     * existing contact matching [phoneNumberArg] for editing. Idempotent across recompositions
     * with the same argument.
     *
     * @param phoneNumberArg Optional phone number route argument identifying a contact to edit.
     */
    fun initialize(phoneNumberArg: String?) {
        val normalized = phoneNumberArg?.filter(Char::isDigit)?.takeIf { it.isNotBlank() }
        if (loadedForPhone == normalized && (_uiState.value.isEditMode == (normalized != null))) {
            return
        }
        loadedForPhone = normalized

        if (normalized == null) {
            _uiState.value = RegisterContactUiState()
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    isEditMode = true,
                    originalPhoneNumber = normalized,
                )
            }
            getSecureContactByPhoneUseCase(normalized)
                .onSuccess { contact ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = contact.name,
                            phoneNumber = contact.phoneNumber,
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(RegisterContactEvent.ShowMessage(error.toStringResource()))
                    _events.emit(RegisterContactEvent.NavigateBack)
                }
        }
    }

    /**
     * Updates the contact name field and validates that it is non-blank.
     *
     * @param input Updated name text entered by the user.
     */
    fun onNameChanged(input: String) {
        val err = if (input.isBlank()) AppError.Validation.EmptyName.toStringResource() else null
        _uiState.update { it.copy(name = input, nameErrorRes = err) }
    }

    /**
     * Filters [input] to digits (ignoring inputs exceeding [PHONE_DIGITS_MAX]), updates the phone
     * number field, and validates its length.
     *
     * @param input Raw phone number text entered by the user.
     */
    fun onPhoneChanged(input: String) {
        val digits = input.filter(Char::isDigit)
        if (digits.length > PHONE_DIGITS_MAX) return
        val err = when {
            digits.isEmpty() -> AppError.Validation.EmptyPhone.toStringResource()
            digits.length < PHONE_DIGITS_MAX -> AppError.Validation.InvalidPhoneDigits.toStringResource()
            else -> null
        }
        _uiState.update { it.copy(phoneNumber = digits, phoneErrorRes = err) }
    }

    /**
     * Submits the current form state to [SaveSecureContactUseCase], navigating back on success or
     * surfacing field/snackbar errors on failure.
     */
    fun onSaveClicked() {
        val current = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            saveSecureContactUseCase(
                name = current.name,
                phoneNumber = current.phoneNumber,
                originalPhoneNumber = current.originalPhoneNumber,
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(RegisterContactEvent.NavigateBack)
            }.onError { error ->
                val res = error.toStringResource()
                when (error) {
                    AppError.Validation.EmptyName -> _uiState.update { it.copy(isSaving = false, nameErrorRes = res) }
                    AppError.Validation.EmptyPhone,
                    AppError.Validation.InvalidPhoneDigits,
                    -> _uiState.update { it.copy(isSaving = false, phoneErrorRes = res) }
                    else -> {
                        _uiState.update { it.copy(isSaving = false) }
                        _events.emit(RegisterContactEvent.ShowMessage(res))
                    }
                }
            }
        }
    }
}
