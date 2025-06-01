package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.AuthUseCase
import br.com.lucolimac.shesafe.route.SheSafeDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {
    init {
        isUserLoggedIn()
    }

    // SharedFlow is good for one-time events like navigation or showing a toast
    private val _logoutCompleteEvent = MutableStateFlow<Boolean>(false)
    val logoutCompleteEvent = _logoutCompleteEvent.asStateFlow()
    private val _isUserLoggedIn = MutableStateFlow<Boolean>(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()
    private val _startDestination = MutableStateFlow<SheSafeDestination>(SheSafeDestination.Login)
    val startDestination = _startDestination.asStateFlow()

    fun logoutUser() {
        viewModelScope.launch {
            authUseCase.logoutUser({
                // Emit a logout complete event on successful logout
                _logoutCompleteEvent.value = true
            }, { exception ->
                // Handle logout failure if needed, e.g., show a message
                _logoutCompleteEvent.value = false
            })
        }
    }

    fun isUserLoggedIn() {
        viewModelScope.launch {
            authUseCase.isUserLoggedIn().collect { isLoggedIn ->
                _startDestination.emit(
                    if (isLoggedIn) SheSafeDestination.Home else SheSafeDestination.Login
                )
                _isUserLoggedIn.emit(true)
            }
        }
    }
}