package br.com.lucolimac.shesafe.android.presentation.utils

import br.com.lucolimac.shesafe.R

object StringExtensions {
    fun String.validateName(): Int? = if (this.isBlank()) R.string.error_name_blank else null
    fun String.validatePhone(): Int? =
        if (this.isBlank() || !this.all { it.isDigit() }) R.string.error_phone_digits else null
}