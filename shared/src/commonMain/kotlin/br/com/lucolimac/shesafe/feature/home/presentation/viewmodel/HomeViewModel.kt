package br.com.lucolimac.shesafe.feature.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.core.domain.model.onError
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.core.presentation.toStringResource
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.ObserveSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.contacts.domain.usecase.RefreshSecureContactsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.AlertDeliveryOutcome
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.TriggerEmergencyAlertUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateSettingUseCase
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import br.com.lucolimac.shesafe.platform.LocationResult
import br.com.lucolimac.shesafe.platform.LocationService
import br.com.lucolimac.shesafe.platform.awaitCurrentLocation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.sms_sent
import shesafe.shared.generated.resources.sms_user_confirmation_required

enum class HomeDialogType {
    NONE,
    NO_CONTACTS,
    CONFIRM_SEND,
}

data class HomeUiState(
    val isSending: Boolean = false,
    val secureContacts: List<SecureContact> = emptyList(),
    val customHelpMessage: String = "",
    val requireSendConfirmation: Boolean = true,
    val dontAskAgainChecked: Boolean = false,
    val userLocation: LocationCoordinates? = null,
    val activeDialog: HomeDialogType = HomeDialogType.NONE,
)

sealed interface HomeEvent {
    data object NavigateToRegisterContact : HomeEvent
    data class ShowMessage(val messageRes: StringResource) : HomeEvent
}

class HomeViewModel(
    observeSecureContactsUseCase: ObserveSecureContactsUseCase,
    private val refreshSecureContactsUseCase: RefreshSecureContactsUseCase,
    observeHelpMessageUseCase: ObserveHelpMessageUseCase,
    private val refreshHelpMessageUseCase: RefreshHelpMessageUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val refreshSettingsUseCase: RefreshSettingsUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase,
    private val triggerEmergencyAlertUseCase: TriggerEmergencyAlertUseCase,
    private val locationService: LocationService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                observeSecureContactsUseCase(),
                observeHelpMessageUseCase(),
                observeSettingsUseCase(),
            ) { contacts, message, settings ->
                Triple(contacts, message, settings.isEnabled(SettingKey.SEND_CONFIRMATION))
            }.collect { (contacts, message, confirmEnabled) ->
                _uiState.update { state ->
                    state.copy(
                        secureContacts = contacts,
                        customHelpMessage = message,
                        requireSendConfirmation = confirmEnabled,
                    )
                }
            }
        }
        refreshInitialData()
    }

    fun refreshInitialData() {
        viewModelScope.launch {
            refreshSecureContactsUseCase()
            refreshHelpMessageUseCase()
            refreshSettingsUseCase()
        }
    }

    fun refreshLocation() {
        viewModelScope.launch {
            when (val result = locationService.awaitCurrentLocation()) {
                is LocationResult.Available -> {
                    _uiState.update { it.copy(userLocation = result.coordinates) }
                }
                LocationResult.PermissionRequired,
                LocationResult.Unavailable,
                is LocationResult.Failed,
                -> Unit
            }
        }
    }

    fun onRequestHelpClicked(defaultDangerMessage: String) {
        val current = _uiState.value
        when {
            current.secureContacts.isEmpty() -> {
                _uiState.update { it.copy(activeDialog = HomeDialogType.NO_CONTACTS) }
            }
            current.requireSendConfirmation -> {
                _uiState.update {
                    it.copy(
                        activeDialog = HomeDialogType.CONFIRM_SEND,
                        dontAskAgainChecked = false,
                    )
                }
            }
            else -> {
                dispatchEmergencyAlert(defaultDangerMessage)
            }
        }
    }

    fun onDontAskAgainChanged(checked: Boolean) {
        _uiState.update { it.copy(dontAskAgainChecked = checked) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(activeDialog = HomeDialogType.NONE, dontAskAgainChecked = false) }
    }

    fun onConfirmNoContactsDialog() {
        _uiState.update { it.copy(activeDialog = HomeDialogType.NONE) }
        viewModelScope.launch {
            _events.emit(HomeEvent.NavigateToRegisterContact)
        }
    }

    fun onConfirmSendAlert(defaultDangerMessage: String) {
        val disableFutureConfirmation = _uiState.value.dontAskAgainChecked
        _uiState.update { it.copy(activeDialog = HomeDialogType.NONE, dontAskAgainChecked = false) }
        viewModelScope.launch {
            if (disableFutureConfirmation) {
                updateSettingUseCase(SettingKey.SEND_CONFIRMATION, false)
            }
            dispatchEmergencyAlert(defaultDangerMessage)
        }
    }

    private fun dispatchEmergencyAlert(defaultDangerMessage: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }

            // Refresh latest location right before sending if possible
            val latestLocation = when (val locResult = locationService.awaitCurrentLocation()) {
                is LocationResult.Available -> {
                    _uiState.update { it.copy(userLocation = locResult.coordinates) }
                    locResult.coordinates
                }
                else -> _uiState.value.userLocation
            }

            val state = _uiState.value
            val effectiveMessage = state.customHelpMessage.ifBlank { defaultDangerMessage }

            triggerEmergencyAlertUseCase(
                contacts = state.secureContacts,
                baseMessage = effectiveMessage,
                location = latestLocation,
            ).onSuccess { outcome ->
                _uiState.update { it.copy(isSending = false) }
                val messageRes = when (outcome) {
                    is AlertDeliveryOutcome.SentViaGateway,
                    AlertDeliveryOutcome.SentViaDeviceSms,
                    -> Res.string.sms_sent
                    AlertDeliveryOutcome.UserConfirmationRequired -> Res.string.sms_user_confirmation_required
                }
                _events.emit(HomeEvent.ShowMessage(messageRes))
            }.onError { error ->
                _uiState.update { it.copy(isSending = false) }
                _events.emit(HomeEvent.ShowMessage(error.toStringResource()))
            }
        }
    }
}
