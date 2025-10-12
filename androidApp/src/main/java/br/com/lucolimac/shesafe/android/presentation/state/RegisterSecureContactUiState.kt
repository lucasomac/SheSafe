package br.com.lucolimac.shesafe.android.presentation.state

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact

sealed class RegisterSecureContactUiState {
    data class Success(
        val secureContact: SecureContact?
    ) : RegisterSecureContactUiState()

    data class Error(val message: String) : RegisterSecureContactUiState()
    object Loading : RegisterSecureContactUiState()
    object Empty : RegisterSecureContactUiState()
}