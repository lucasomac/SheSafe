package br.com.lucolimac.shesafe.android.presentation.state

sealed class SmsStatusState {
    object Idle : SmsStatusState()
    data class Result(val sent: Boolean) : SmsStatusState()
}