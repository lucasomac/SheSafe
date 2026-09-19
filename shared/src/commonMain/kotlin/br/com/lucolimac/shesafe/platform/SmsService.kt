package br.com.lucolimac.shesafe.platform

data class SmsRequest(
    val recipients: List<String>,
    val message: String,
)

sealed interface SmsResult {
    data object Sent : SmsResult
    data object UserConfirmationRequired : SmsResult
    data object Unsupported : SmsResult
    data class Failed(val reason: String) : SmsResult
}

interface SmsService {
    fun send(request: SmsRequest, onResult: (SmsResult) -> Unit)
}
