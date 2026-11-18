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

/**
 * Immutable UI state for the help requests history screen.
 *
 * @property isLoading Whether initial data or a retry refresh is currently in progress.
 * @property helpRequests Ordered list of recorded [HelpRequest] items to display.
 * @property errorResource Localized error message resource shown when loading fails with an empty cache.
 */
data class HelpRequestsUiState(
    val isLoading: Boolean = true,
    val helpRequests: List<HelpRequest> = emptyList(),
    val errorResource: StringResource? = null,
)

/**
 * ViewModel managing state and remote refresh operations for the help requests history screen.
 *
 * @param observeHelpRequestsUseCase Use case streaming cached [HelpRequest] items.
 * @param refreshHelpRequestsUseCase Use case synchronizing remote help requests into the local cache.
 */
class HelpRequestsViewModel(
    observeHelpRequestsUseCase: ObserveHelpRequestsUseCase,
    private val refreshHelpRequestsUseCase: RefreshHelpRequestsUseCase,
) : ViewModel() {

    /** Backing mutable state flow holding the latest [HelpRequestsUiState]. */
    private val _uiState = MutableStateFlow(HelpRequestsUiState())

    /** Publicly observable [StateFlow] of [HelpRequestsUiState] for UI consumption. */
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

    /**
     * Triggers a remote synchronization of the user's help request history.
     *
     * Displays a loading indicator only when the local list is empty, and surfaces an error banner
     * only if synchronization fails while no cached items exist.
     */
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
