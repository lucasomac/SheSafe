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

/**
 * Represents the modal dialog variant currently presented on the home screen.
 */
enum class HomeDialogType {
    /** No modal dialog is visible. */
    NONE,

    /** Prompts the user to register at least one trusted contact before requesting help. */
    NO_CONTACTS,

    /** Asks the user to confirm sending an emergency alert, with an optional "don't ask again" toggle. */
    CONFIRM_SEND,
}

/**
 * Immutable UI state for the home screen and emergency SOS workflow.
 *
 * @property isSending Whether an emergency SOS alert dispatch is currently in flight.
 * @property secureContacts Cached list of [SecureContact] recipients who will receive the alert.
 * @property customHelpMessage User-configured custom SOS message, or blank when using the default text.
 * @property requireSendConfirmation Whether user confirmation is required before sending an alert.
 * @property dontAskAgainChecked Current checkbox state inside the [HomeDialogType.CONFIRM_SEND] dialog.
 * @property userLocation Latest known [LocationCoordinates] of the device, or `null` if unavailable.
 * @property activeDialog Currently visible [HomeDialogType] modal on the home screen.
 */
data class HomeUiState(
    val isSending: Boolean = false,
    val secureContacts: List<SecureContact> = emptyList(),
    val customHelpMessage: String = "",
    val requireSendConfirmation: Boolean = true,
    val dontAskAgainChecked: Boolean = false,
    val userLocation: LocationCoordinates? = null,
    val activeDialog: HomeDialogType = HomeDialogType.NONE,
)

/**
 * One-off UI side-effect events emitted by [HomeViewModel].
 */
sealed interface HomeEvent {
    /**
     * Instructs the UI to navigate to the trusted contact registration screen.
     */
    data object NavigateToRegisterContact : HomeEvent

    /**
     * Instructs the UI to display a transient localized feedback message.
     *
     * @property messageRes Localized [StringResource] for the message to display.
     */
    data class ShowMessage(val messageRes: StringResource) : HomeEvent
}

/**
 * ViewModel orchestrating the home map state, emergency contact readiness, confirmation preferences,
 * device location acquisition, and SOS alert dispatch.
 *
 * @param observeSecureContactsUseCase Use case streaming the user's trusted emergency contacts.
 * @property refreshSecureContactsUseCase Use case synchronizing trusted contacts from remote storage.
 * @param observeHelpMessageUseCase Use case streaming the user's custom emergency message.
 * @property refreshHelpMessageUseCase Use case synchronizing the custom emergency message remotely.
 * @param observeSettingsUseCase Use case streaming user safety preferences.
 * @property refreshSettingsUseCase Use case synchronizing user safety preferences remotely.
 * @property updateSettingUseCase Use case persisting updates to individual user preferences.
 * @property triggerEmergencyAlertUseCase Use case dispatching SOS alerts to trusted contacts.
 * @property locationService Platform service providing current device GPS coordinates.
 */
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

    /**
     * Reactive stream of [HomeUiState] observed by the home screen.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Stream of one-shot [HomeEvent] side effects for navigation and user feedback.
     */
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

    /**
     * Triggers remote synchronization of trusted contacts, custom help message, and user settings.
     */
    fun refreshInitialData() {
        viewModelScope.launch {
            refreshSecureContactsUseCase()
            refreshHelpMessageUseCase()
            refreshSettingsUseCase()
        }
    }

    /**
     * Queries [locationService] for the current device coordinates and updates [HomeUiState.userLocation]
     * when a location fix is available.
     */
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

    /**
     * Handles taps on the primary "Request Help" SOS button: prompts for contacts if none exist,
     * shows a confirmation dialog if enabled, or immediately dispatches the emergency alert.
     *
     * @param defaultDangerMessage Fallback localized SOS message used when no custom message is configured.
     */
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

    /**
     * Updates the "don't ask again" checkbox state within the send confirmation dialog.
     *
     * @param checked Whether the user opted to skip future send confirmations.
     */
    fun onDontAskAgainChanged(checked: Boolean) {
        _uiState.update { it.copy(dontAskAgainChecked = checked) }
    }

    /**
     * Dismisses any currently active dialog on the home screen and resets transient checkbox state.
     */
    fun onDismissDialog() {
        _uiState.update { it.copy(activeDialog = HomeDialogType.NONE, dontAskAgainChecked = false) }
    }

    /**
     * Dismisses the missing-contacts dialog and emits [HomeEvent.NavigateToRegisterContact].
     */
    fun onConfirmNoContactsDialog() {
        _uiState.update { it.copy(activeDialog = HomeDialogType.NONE) }
        viewModelScope.launch {
            _events.emit(HomeEvent.NavigateToRegisterContact)
        }
    }

    /**
     * Confirms emergency alert dispatch from the confirmation dialog, persisting the
     * [SettingKey.SEND_CONFIRMATION] preference if "don't ask again" was checked.
     *
     * @param defaultDangerMessage Fallback localized SOS message used when no custom message is configured.
     */
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

    /**
     * Refreshes the latest device location if available, resolves the effective SOS message,
     * invokes [triggerEmergencyAlertUseCase], and emits a user-facing status message.
     *
     * @param defaultDangerMessage Fallback localized SOS message used when no custom message is configured.
     */
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
