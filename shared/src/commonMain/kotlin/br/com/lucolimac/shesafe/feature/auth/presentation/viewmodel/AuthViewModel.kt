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

/**
 * Immutable UI state representing the login and authentication screen.
 *
 * @property isAuthenticated Whether the user currently holds an active authenticated session.
 * @property isLoading Whether an authentication request is currently in progress.
 * @property errorResource Optional localized [StringResource] describing the latest sign-in error.
 */
data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorResource: StringResource? = null,
)

/**
 * One-off UI side-effect events emitted by [AuthViewModel].
 */
sealed interface AuthEvent {
    /**
     * Instructs the UI to navigate from the login screen to the home destination.
     */
    data object NavigateToHome : AuthEvent

    /**
     * Instructs the UI to display a transient user-facing feedback message.
     *
     * @property messageRes Localized [StringResource] for the message to display.
     */
    data class ShowMessage(val messageRes: StringResource) : AuthEvent
}

/**
 * ViewModel coordinating Google Sign-In authentication flows and session state observation.
 *
 * @param observeCurrentUserUseCase Use case observing real-time authentication state changes.
 * @param getCurrentUserUseCase Use case synchronously reading the initial authentication snapshot.
 * @property signInWithGoogleUseCase Use case exchanging Google OAuth tokens for a Firebase session.
 */
class AuthViewModel(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(isAuthenticated = getCurrentUserUseCase() != null),
    )

    /**
     * Reactive stream of [AuthUiState] observed by the login screen.
     */
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    /**
     * Stream of one-shot [AuthEvent] side effects for navigation and user feedback.
     */
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

    /**
     * Exchanges raw Google Sign-In tokens with the authentication backend and updates UI state.
     *
     * @param idToken OpenID Connect ID token issued by Google.
     * @param accessToken Optional OAuth 2.0 access token issued by Google.
     */
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

    /**
     * Marks authentication as succeeded when an external authentication provider completes sign-in,
     * clearing any error state and emitting [AuthEvent.NavigateToHome].
     */
    fun onExternalAuthSuccess() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true, errorResource = null) }
            _events.emit(AuthEvent.NavigateToHome)
        }
    }

    /**
     * Handles an authentication failure reported by an external sign-in provider.
     *
     * @param reason Optional diagnostic failure message returned by the external provider.
     */
    fun onExternalAuthFailure(reason: String?) {
        viewModelScope.launch {
            val res = AppError.Auth.SignInFailed(reason).toStringResource()
            _uiState.update { it.copy(isLoading = false, errorResource = res) }
            _events.emit(AuthEvent.ShowMessage(res))
        }
    }
}
