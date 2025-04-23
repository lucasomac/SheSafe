package br.com.lucolimac.shesafe.android.presentation.model

data class DialogModel(
    val title: String,
    val message: String,
    val confirmButtonText: String,
    val dismissButtonText: String,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit,
    val showCheckbox: Boolean = false,
    val checkboxText: String? = null,
    val onCheckboxCheckedChange: ((Boolean) -> Unit)? = null,
    val initialCheckboxValue: Boolean = false,
    )