package br.com.lucolimac.shesafe.android.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.domain.entity.InfoBipBody
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.domain.entity.SheSafeResult
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevBody
import br.com.lucolimac.shesafe.android.domain.entity.SmsDevEntity
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.api.InfoBipUseCase
import br.com.lucolimac.shesafe.android.domain.usecase.api.SmsDevUseCase
import br.com.lucolimac.shesafe.android.presentation.state.SmsStatusState
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
    private val infoBipUseCase: InfoBipUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true) // Add a loading state
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _helpRequests = MutableStateFlow<List<HelpRequest>>(emptyList())
    val helpRequests = _helpRequests.asStateFlow()
    private val _smsStatusSent = MutableStateFlow<SmsStatusState>(SmsStatusState.Idle)
    val smsStatusSent: StateFlow<SmsStatusState> = _smsStatusSent.asStateFlow()
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
                    _helpRequests.emit(helpRequests.sortedByDescending { it.createdAt })
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
                    key = SMS_DEV_API_KEY,
                    msg = message,
                    number = it.phoneNumber.toLong(),
                    type = TYPE_SEND
                )
            }
            smsDevUseCase.sendSms(body = body).collect {
                val data: List<SmsDevEntity> = it
                _smsStatusSent.update {
                    SmsStatusState.Result(data.any { it.situacao == "OK" })
                }
                val helpRequest = HelpRequest(
                    phoneNumbers = contacts.map { contact -> contact.phoneNumber },
                    location = location,
                    createdAt = Timestamp.now()
                )
                registerHelpRequest(helpRequest)
            }
        }
    }

    fun sendSmsInfoBip(contacts: List<SecureContact>, message: String, location: GeoPoint) {
        viewModelScope.launch {
            val body = InfoBipBody(
                messages = listOf(
                    InfoBipBody.Message(
                    content = InfoBipBody.Message.Content(text = message),
                    destinations = contacts.map { contact ->
                        InfoBipBody.Message.Destination(
                            "55${contact.phoneNumber}"
                        )
                    })),
            )


            infoBipUseCase.sendSms(body = body, authorization = "App $INFO_BIP_API_KEY").collect {
                when (it) {
                    is SheSafeResult.Error -> {
                        Log.e("SmsDevViewModel", "sendSms: ", it.throwable)

                    }

                    is SheSafeResult.Failure -> {
                        Log.e("SmsDevViewModel", "sendSms:  ${it.data}")
                    }

                    is SheSafeResult.Success -> {
                        _smsStatusSent.update { SmsStatusState.Result(true) }
                        val helpRequest = HelpRequest(
                            phoneNumbers = contacts.map { contact -> contact.phoneNumber },
                            location = location,
                            createdAt = Timestamp.now()
                        )
                        registerHelpRequest(helpRequest)
                    }
                }
            }
        }
    }

    fun resetSmsStatus() {
        _smsStatusSent.value = SmsStatusState.Idle
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
        const val SMS_DEV_API_KEY =
            "DE7IVGCICFQ54IXAM14B63U0IG8EVSKMC515IBVY1EQQTITSTHDPTURVVGXQ8X5OKRANUT9NFU10LNSCYM3ONCI7LYWR90NL50ENC2G9CFPFHK8U21FWANW6P6F3UW9K"
        const val INFO_BIP_API_KEY =
            "4ad1a1941baf4838c8c162bd5745432b-fb511225-9e8a-4075-bc83-f032a3d7f8f9"
        const val TYPE_SEND = 9
    }
}