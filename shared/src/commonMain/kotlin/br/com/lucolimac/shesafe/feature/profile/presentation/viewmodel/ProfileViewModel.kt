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

private const val MAX_RECENT_SENT = 5

enum class ProfileActiveSheet {
    NONE,
    SETTINGS,
    EDIT_MESSAGE,
}

data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: UserProfile? = null,
    val customHelpMessage: String = "",
    val recentHelpRequests: List<HelpRequest> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val activeSheet: ProfileActiveSheet = ProfileActiveSheet.NONE,
)

sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
    data object NavigateToHelpRequests : ProfileEvent
    data class ShowMessage(val messageRes: StringResource) : ProfileEvent
}

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

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
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

    fun refresh() {
        viewModelScope.launch {
            refreshHelpMessageUseCase()
            refreshSettingsUseCase()
            refreshHelpRequestsUseCase()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onOpenSettingsSheet() {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.SETTINGS) }
    }

    fun onOpenEditMessageSheet() {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.EDIT_MESSAGE) }
    }

    fun onDismissSheet() {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.NONE) }
    }

    fun onToggleSetting(key: SettingKey, enabled: Boolean) {
        viewModelScope.launch {
            updateSettingUseCase(key, enabled)
                .onError { error ->
                    _events.emit(ProfileEvent.ShowMessage(error.toStringResource()))
                }
        }
    }

    fun onSaveHelpMessage(newMessage: String) {
        _uiState.update { it.copy(activeSheet = ProfileActiveSheet.NONE) }
        viewModelScope.launch {
            updateHelpMessageUseCase(newMessage)
                .onError { error ->
                    _events.emit(ProfileEvent.ShowMessage(error.toStringResource()))
                }
        }
    }

    fun onShowHelpRequestsClicked() {
        viewModelScope.launch {
            _events.emit(ProfileEvent.NavigateToHelpRequests)
        }
    }

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

    private data class ProfileCombined(
        val user: UserProfile?,
        val message: String,
        val settings: UserSettings,
        val recentRequests: List<HelpRequest>,
    )
}
