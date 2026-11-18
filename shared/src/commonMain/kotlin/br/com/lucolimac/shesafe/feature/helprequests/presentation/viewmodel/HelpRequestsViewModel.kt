package br.com.lucolimac.shesafe.feature.helprequests.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.lucolimac.shesafe.core.domain.model.onError
import br.com.lucolimac.shesafe.core.domain.model.onSuccess
import br.com.lucolimac.shesafe.core.presentation.toStringResource
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.ObserveHelpRequestsUseCase
import br.com.lucolimac.shesafe.feature.helprequests.domain.usecase.RefreshHelpRequestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

data class HelpRequestsUiState(
    val isLoading: Boolean = true,
    val helpRequests: List<HelpRequest> = emptyList(),
    val errorResource: StringResource? = null,
)

class HelpRequestsViewModel(
    observeHelpRequestsUseCase: ObserveHelpRequestsUseCase,
    private val refreshHelpRequestsUseCase: RefreshHelpRequestsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HelpRequestsUiState())
    val uiState: StateFlow<HelpRequestsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeHelpRequestsUseCase().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        helpRequests = list,
                        errorResource = if (list.isNotEmpty()) null else state.errorResource,
                    )
                }
            }
        }
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = _uiState.value.helpRequests.isEmpty(), errorResource = null) }
            refreshHelpRequestsUseCase()
                .onSuccess {
                    _uiState.update { state -> state.copy(isLoading = false, errorResource = null) }
                }
                .onError { error ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorResource = if (state.helpRequests.isEmpty()) error.toStringResource() else null,
                        )
                    }
                }
        }
    }
}
