package br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.core.domain.model.AppError
import br.com.lucolimac.shesafe.core.domain.model.onError
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.core.presentation.toStringResource
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.GetCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.ObserveCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.SignInWithGoogleUseCase
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

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorResource: StringResource? = null,
)

sealed interface AuthEvent {
    data object NavigateToHome : AuthEvent
    data class ShowMessage(val messageRes: StringResource) : AuthEvent
}

class AuthViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = getCurrentUserUseCase() != null),
    )
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                val loggedIn = user != null
                val wasLoggedIn = _uiState.value.isAuthenticated
                _uiState.update { it.copy(isAuthenticated = loggedIn, isLoading = false) }
                if (loggedIn && !wasLoggedIn) {
                    _events.emit(AuthEvent.NavigateToHome)
                }
            }
        }
    }

    fun onGoogleTokensReceived(idToken: String, accessToken: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorResource = null) }
            signInWithGoogleUseCase(idToken = idToken, accessToken = accessToken)
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false, isAuthenticated = true) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                .onError { error ->
                    val res = error.toStringResource()
                    _uiState.update { state -> state.copy(isLoading = false, errorResource = res) }
                    _events.emit(AuthEvent.ShowMessage(res))
                }
        }
    }

    fun onExternalAuthSuccess() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true, errorResource = null) }
            _events.emit(AuthEvent.NavigateToHome)
        }
    }

    fun onExternalAuthFailure(reason: String?) {
        viewModelScope.launch {
            val res = AppError.Auth.SignInFailed(reason).toStringResource()
            _uiState.update { it.copy(isLoading = false, errorResource = res) }
            _events.emit(AuthEvent.ShowMessage(res))
        }
    }
}
