package br.com.lucolimac.shesafe.android.presentation.state

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact

sealed class SecureContactUiState {
    data class Success(val secureContacts: List<SecureContact>) : SecureContactUiState()
    data class Error(val message: String) : SecureContactUiState()
    object Loading : SecureContactUiState()
    object Empty : SecureContactUiState()
    object Idle : SecureContactUiState()
}