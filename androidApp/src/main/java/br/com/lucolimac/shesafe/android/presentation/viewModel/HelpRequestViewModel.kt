package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.SmsDevUseCase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HelpRequestViewModel(
    private val helpRequestUseCase: HelpRequestUseCase,
    private val smsDevUseCase: SmsDevUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true) // Add a loading state
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _helpRequests = MutableStateFlow<List<HelpRequest>>(emptyList())
    val helpRequests = _helpRequests.asStateFlow()
    private val _sendSmsState = MutableStateFlow(0)
    val sendSmsState: StateFlow<Int> = _sendSmsState.asStateFlow()
    private val authListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            // new user logged in -> fetch their help requests
            fetchHelpRequests()
        } else {
            // no user -> clear UI state
            resetAllStates()
        }
    }

    init {
        // register listener to react to auth changes while ViewModel is alive
        firebaseAuth.addAuthStateListener(authListener)
    }

    fun fetchHelpRequests() {
        viewModelScope.launch {
            helpRequestUseCase.getHelpRequests().onStart { _isLoading.emit(true) }
                .onCompletion { _isLoading.emit(false) }.collect { helpRequests ->
                    _helpRequests.emit(helpRequests)
                }
        }
    }

    fun registerHelpRequest(helpRequest: HelpRequest) {
        viewModelScope.launch {
            helpRequestUseCase.registerHelpRequest(helpRequest).onStart { _isLoading.emit(true) }
                .onCompletion { _isLoading.emit(false) }.collect { isRegistered ->
                    if (isRegistered) {
                        fetchHelpRequests()
                    }
                }
        }
    }

    fun sendSms(contacts: List<SecureContact>, message: String, location: GeoPoint) {
        viewModelScope.launch {
            val body = contacts.map {
                SmsDevBody(
                    key = API_KEY, msg = message, number = it.phoneNumber.toLong(), type = TYPE_SEND
                )
            }
            smsDevUseCase.sendSms(body = body).onStart { _sendSmsState.update { 0 } }.collect {
                val data: List<SmsDevEntity> = it
                _sendSmsState.update { data.count { it.codigo == "OK" } }
                val helpRequest = HelpRequest(
                    phoneNumbers = contacts.map { it.phoneNumber },
                    location = location,
                    createdAt = Timestamp.now()
                )
                registerHelpRequest(helpRequest)
            }
        }
    }

    fun resetAllStates() {
        viewModelScope.launch {
            _isLoading.emit(true)
            _helpRequests.emit(emptyList())
        }
    }

    override fun onCleared() {
        firebaseAuth.removeAuthStateListener(authListener)
        super.onCleared()
    }

    companion object {
        const val API_KEY = ""
        const val TYPE_SEND = 9
    }
}