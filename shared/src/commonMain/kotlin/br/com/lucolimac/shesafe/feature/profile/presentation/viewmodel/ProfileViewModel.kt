package br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.core.domain.model.onError
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.core.presentation.toStringResource
import br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.ObserveCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignOutUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.ObserveHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.RefreshHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.ObserveSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.RefreshSettingsUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateHelpMessageUseCase
import br.com.lucolimac.shesafe.feature.profile.domain.usecase.UpdateSettingUseCase
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

/** Maximum number of recent [HelpRequest] entries displayed inline on the profile screen. */
private const val MAX_RECENT_SENT = 5

/**
 * Identifies which modal bottom sheet, if any, is currently presented on the profile screen.
 */
enum class ProfileActiveSheet {
    /** No modal bottom sheet is visible. */
    NONE,

    /** The user settings sheet is visible. */
    SETTINGS,

    /** The custom emergency help message editor sheet is visible. */
    EDIT_MESSAGE,
}

/**
 * Immutable UI state for the profile screen.
 *
 * @property isLoading Whether profile data and recent help requests are still loading.
 * @property user Authenticated [UserProfile] information, or `null` if unavailable.
 * @property customHelpMessage User-defined emergency SOS message, or blank when using the default message.
 * @property recentHelpRequests Up to [MAX_RECENT_SENT] most recent [HelpRequest] records.
 * @property settings Current [UserSettings] preferences for the authenticated user.
 * @property activeSheet Modal bottom sheet currently open over the profile screen.
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserProfile? = null,
    val customHelpMessage: String = "",
    val recentHelpRequests: List<HelpRequest> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val activeSheet: ProfileActiveSheet = ProfileActiveSheet.NONE,
)

/**
 * One-shot UI events emitted by [ProfileViewModel] for navigation and transient user feedback.
 */
sealed interface ProfileEvent {
    /** Instructs the UI to navigate back to the authentication screen after sign-out. */
    data object NavigateToLogin : ProfileEvent

    /** Instructs the UI to navigate to the full help requests history screen. */
    data object NavigateToHelpRequests : ProfileEvent

    /**
     * Instructs the UI to display a localized snackbar or toast message.
     *
     * @property messageRes Localized [StringResource] describing the feedback or error.
     */
    data class ShowMessage(val messageRes: StringResource) : ProfileEvent
}

/**
 * ViewModel coordinating user profile details, custom SOS message editing, user settings,
 * recent help request previews, and sign-out actions.
 *
 * @param observeCurrentUserUseCase Use case streaming the active [UserProfile].
 * @param observeHelpMessageUseCase Use case streaming the cached custom help message.
 * @param refreshHelpMessageUseCase Use case synchronizing the custom help message from remote storage.
 * @param updateHelpMessageUseCase Use case validating and saving a new custom help message.
 * @param observeSettingsUseCase Use case streaming [UserSettings] updates.
 * @param refreshSettingsUseCase Use case synchronizing [UserSettings] from remote storage.
 * @param updateSettingUseCase Use case persisting individual [SettingKey] changes.
 * @param observeHelpRequestsUseCase Use case streaming recorded [HelpRequest] entries.
 * @param refreshHelpRequestsUseCase Use case synchronizing [HelpRequest] entries from remote storage.
 * @param signOutUseCase Use case signing out the current user session.
 */
class ProfileViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeHelpMessageUseCase: ObserveHelpMessageUseCase,
    private val refreshHelpMessageUseCase: RefreshHelpMessageUseCase,
    private val updateHelpMessageUseCase: UpdateHelpMessageUseCase,
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val refreshSettingsUseCase: RefreshSettingsUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase,
    observeHelpRequestsUseCase: ObserveHelpRequestsUseCase,
    private val refreshHelpRequestsUseCase: RefreshHelpRequestsUseCase,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    /** Backing mutable state flow holding the current [ProfileUiState]. */
    private val _uiState = MutableStateFlow(ProfileUiState())

    /** Publicly observable [StateFlow] of [ProfileUiState] consumed by the UI layer. */
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    /** Backing shared flow used to dispatch one-shot [ProfileEvent] emissions. */
    private val _events = MutableSharedFlow<ProfileEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /** Publicly observable [SharedFlow] of one-shot [ProfileEvent] side effects. */
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                observeCurrentUserUseCase(),
                observeHelpMessageUseCase(),
                observeSettingsUseCase(),
                observeHelpRequestsUseCase(),
            ) { user, message, settings, requests ->
                ProfileCombined(
                    user = user,
                    message = message,
                    settings = settings,
                    recentRequests = requests.take(MAX_RECENT_SENT),
                )
            }.collect { combined ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        user = combined.user,
                        customHelpMessage = combined.message,
                        settings = combined.settings,
                        recentHelpRequests = combined.recentRequests,
                    )
                }
            }
        }
        refresh()
    }

    /**
     * Synchronizes the custom help message, user settings, and help request history with remote storage.
     */
    fun refresh() {
        viewModelScope.launch {
            refreshHelpMessageUseCase()
            refreshSettingsUseCase()
            refreshHelpRequestsUseCase()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Opens the user settings modal bottom sheet.
     */
    fun onOpenSettingsSheet() {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.SETTINGS) }
    }

    /**
     * Opens the custom emergency help message editor modal bottom sheet.
     */
    fun onOpenEditMessageSheet() {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.EDIT_MESSAGE) }
    }

    /**
     * Dismisses whichever modal bottom sheet is currently open.
     */
    fun onDismissSheet() {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.NONE) }
    }

    /**
     * Persists a boolean toggle change for [key] and emits [ProfileEvent.ShowMessage] if saving fails.
     *
     * @param key The [SettingKey] being toggled.
     * @param enabled New boolean state selected by the user.
     */
    fun onToggleSetting(key: SettingKey, enabled: Boolean) {
        viewModelScope.launch {
            updateSettingUseCase(key, enabled)
                .onError { error ->
                    _events.emit(ProfileEvent.ShowMessage(error.toStringResource()))
                }
        }
    }

    /**
     * Closes the editor sheet, validates and persists [newMessage], and emits [ProfileEvent.ShowMessage]
     * if validation or storage fails.
     *
     * @param newMessage Raw custom SOS message submitted by the user.
     */
    fun onSaveHelpMessage(newMessage: String) {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.NONE) }
        viewModelScope.launch {
            updateHelpMessageUseCase(newMessage)
                .onError { error ->
                    _events.emit(ProfileEvent.ShowMessage(error.toStringResource()))
                }
        }
    }

    /**
     * Emits [ProfileEvent.NavigateToHelpRequests] to navigate to the full help requests history screen.
     */
    fun onShowHelpRequestsClicked() {
        viewModelScope.launch {
            _events.emit(ProfileEvent.NavigateToHelpRequests)
        }
    }

    /**
     * Signs out the current user and emits [ProfileEvent.NavigateToLogin] on success or
     * [ProfileEvent.ShowMessage] on failure.
     */
    fun onLogoutClicked() {
        viewModelScope.launch {
            signOutUseCase()
                .onSuccess {
                    _events.emit(ProfileEvent.NavigateToLogin)
                }
                .onError { error ->
                    _events.emit(ProfileEvent.ShowMessage(error.toStringResource()))
                }
        }
    }

    /**
     * Internal tuple combining observed profile streams before updating [ProfileUiState].
     *
     * @property user Active [UserProfile] or `null` if signed out.
     * @property message Current custom help message string.
     * @property settings Current [UserSettings] preferences.
     * @property recentRequests Trimmed list of the most recent [HelpRequest] items.
     */
    private data class ProfileCombined(
        val user: UserProfile?,
        val message: String,
        val settings: UserSettings,
        val recentRequests: List<HelpRequest>,
    )
}
