package br.com.lucolimac.shesafe.android.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.android.domain.entity.HelpRequest
import br.com.lucolimac.shesafe.android.domain.usecase.HelpRequestUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HelpRequestViewModel(private val helpRequestUseCase: HelpRequestUseCase) : ViewModel() {
    private val _isLoading = MutableStateFlow(true) // Add a loading state
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _helpRequests = MutableStateFlow<List<HelpRequest>>(emptyList())
    val helpRequests = _helpRequests.asStateFlow()

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
}