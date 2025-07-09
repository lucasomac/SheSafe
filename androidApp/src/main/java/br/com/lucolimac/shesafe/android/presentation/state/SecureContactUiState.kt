package br.com.lucolimac.shesafe.android.presentation.state

import br.com.lucolimac.shesafe.android.domain.entity.SecureContact

data class SecureContactUiState(
    val secureContacts: List<SecureContact> = emptyList(),
    val isLoading: Boolean = false,
    val hasBeenRegisteredSecureContact: Boolean = false,
    val hasBeenDeletedSecureContact: Boolean = false,
    val selectedContact: SecureContact? = null
) {
    companion object {
        val Empty = SecureContactUiState()
    }
}
