package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.usecase.AuthUseCase
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.HOME_GRAPH_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.graph.LOGIN_GRAPH_ROUTE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {
    init {
        setStartDestination()
    }

    // SharedFlow is good for one-time events like navigation or showing a toast
    private val _logoutCompleteEvent = MutableStateFlow<Boolean>(false)
    val logoutCompleteEvent = _logoutCompleteEvent.asStateFlow()
    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn = _isUserLoggedIn.asStateFlow()
    private val _startDestination = MutableStateFlow(LOGIN_GRAPH_ROUTE)
    val startDestination = _startDestination.asStateFlow()
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail = _userEmail.asStateFlow()
    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()
    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl = _userPhotoUrl.asStateFlow()

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

    fun setStartDestination() {
        viewModelScope.launch {
            authUseCase.isUserLoggedIn().collect { isLoggedIn ->
                _startDestination.emit(
                    if (isLoggedIn) HOME_GRAPH_ROUTE else LOGIN_GRAPH_ROUTE
                )
            }
        }
    }

    fun setFieldsOfLoggedUser() {
        getUserEmail()
        getName()
        getPhotoUrl()
    }

    fun getUserEmail() {
        viewModelScope.launch {
            authUseCase.getUserEmail().collect { email ->
                _userEmail.emit(email)
            }
        }
    }

    fun getName() {
        viewModelScope.launch {
            authUseCase.getUserName().collect { name ->
                _userName.emit(name)
            }
        }
    }

    fun getPhotoUrl() {
        viewModelScope.launch {
            authUseCase.getUserPhotoUrl().collect { photoUrl ->
                _userPhotoUrl.emit(photoUrl)
            }
        }
    }

    fun resetAllStates() {
        viewModelScope.launch {
            _logoutCompleteEvent.emit(false)
            _isUserLoggedIn.emit(false)
            _startDestination.emit(LOGIN_GRAPH_ROUTE)
            _userEmail.emit(null)
            _userName.emit(null)
            _userPhotoUrl.emit(null)
        }
    }
}