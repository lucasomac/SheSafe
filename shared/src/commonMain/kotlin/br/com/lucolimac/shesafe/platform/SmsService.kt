package br.com.lucolimac.shesafe.platform

/**
 * Payload describing an outbound emergency SMS dispatch to one or more recipients.
 *
 * @property recipients Destination phone numbers that should receive the SMS.
 * @property message Text body to send to each recipient.
 */
data class SmsRequest(
    val recipients: List<String>,
    val message: String,
)

/**
 * Outcome of an SMS dispatch attempt executed via [SmsService].
 */
sealed interface SmsResult {
    /** Indicates that the SMS messages were handed off directly to the cellular telephony stack. */
    data object Sent : SmsResult

    /**
     * Indicates that silent background delivery was not possible and user confirmation is required
     * (for example, missing `SEND_SMS` permission on Android or system composer presentation on iOS).
     */
    data object UserConfirmationRequired : SmsResult

    /** Indicates that the host device lacks cellular SMS messaging hardware or capability. */
    data object Unsupported : SmsResult

    /**
     * Indicates that dispatching the SMS failed due to an unexpected platform error.
     *
     * @property reason Diagnostic description of the failure.
     */
    data class Failed(val reason: String) : SmsResult
}

/**
 * Platform abstraction for sending emergency SMS messages to trusted contacts.
 */
interface SmsService {
    /**
     * Dispatches [request] using the platform's SMS mechanism and reports the outcome via [onResult].
     *
     * @param request [SmsRequest] containing recipient phone numbers and message text.
     * @param onResult Callback invoked with the resulting [SmsResult].
     */
    fun send(request: SmsRequest, onResult: (SmsResult) -> Unit)
}
