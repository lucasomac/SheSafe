package br.com.lucolimac.shesafe.android.presentation.utils

import br.com.lucolimac.shesafe.R

object StringExtensions {
    fun String.validateName(): Int? = if (this.isBlank()) R.string.error_name_blank else null
    fun String.validatePhone(): Int? = when {
        isBlank() -> R.string.error_phone_blank
        length < 11 -> R.string.error_phone_less_eleven_digits
        else -> null
    }
}