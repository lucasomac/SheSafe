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

private const val PHONE_DIGITS_MAX = 11

data class SecureContactsUiState(
    val isLoading: Boolean = true,
    val contacts: List<SecureContact> = emptyList(),
    val contactPendingDelete: SecureContact? = null,
    val errorResource: StringResource? = null,
)

sealed interface SecureContactsEvent {
    data class NavigateToEdit(val phoneNumber: String) : SecureContactsEvent
    data class ShowMessage(val messageRes: StringResource) : SecureContactsEvent
}

class SecureContactsViewModel(
    observeSecureContactsUseCase: ObserveSecureContactsUseCase,
    private val refreshSecureContactsUseCase: RefreshSecureContactsUseCase,
    private val deleteSecureContactUseCase: DeleteSecureContactUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SecureContactsUiState())
    val uiState: StateFlow<SecureContactsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SecureContactsEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
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

    fun onEditContactClicked(contact: SecureContact) {
        viewModelScope.launch {
            _events.emit(SecureContactsEvent.NavigateToEdit(contact.phoneNumber))
        }
    }

    fun onRequestDeleteContact(contact: SecureContact) {
        _uiState.update { it.copy(contactPendingDelete = contact) }
    }

    fun onDismissDeleteSheet() {
        _uiState.update { it.copy(contactPendingDelete = null) }
    }

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
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
            phoneNumber.filter(Char::isDigit).length == PHONE_DIGITS_MAX &&
            nameErrorRes == null &&
            phoneErrorRes == null &&
            !isSaving
}

sealed interface RegisterContactEvent {
    data object NavigateBack : RegisterContactEvent
    data class ShowMessage(val messageRes: StringResource) : RegisterContactEvent
}

class RegisterSecureContactViewModel(
    private val getSecureContactByPhoneUseCase: GetSecureContactByPhoneUseCase,
    private val saveSecureContactUseCase: SaveSecureContactUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterContactUiState())
    val uiState: StateFlow<RegisterContactUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<RegisterContactEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<RegisterContactEvent> = _events.asSharedFlow()

    private var loadedForPhone: String? = null

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

    fun onNameChanged(input: String) {
        val err = if (input.isBlank()) AppError.Validation.EmptyName.toStringResource() else null
        _uiState.update { it.copy(name = input, nameErrorRes = err) }
    }

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
