package br.com.lucolimac.shesafe.feature.helprequests.domain.model

import br.com.lucolimac.shesafe.platform.LocationCoordinates
import kotlin.time.Instant

/**
 * Domain record representing an emergency SOS help request dispatched by the user.
 *
 * @property id Unique identifier of the help request (may be blank prior to persistence).
 * @property phoneNumbers Phone numbers of the trusted contacts notified during this alert.
 * @property location Geographic coordinates captured when the alert was sent, or `null` if unavailable.
 * @property createdAt Timestamp indicating when the help request was created.
 */
data class HelpRequest(
    val id: String = "",
    val phoneNumbers: List<String>,
    val location: LocationCoordinates?,
    val createdAt: Instant,
) {
    /**
     * Shareable Google Maps search URL pointing to [location], or `null` when coordinates were not captured.
     */
    val mapLink: String?
        get() = location?.let {
            "https://www.google.com/maps/search/?api=1&query=${it.latitude},${it.longitude}"
        }
}

/**
 * Supported remote HTTP SMS gateway providers used to dispatch emergency alerts.
 */
enum class SmsProviderType {
    /** Brazilian SMSDev HTTP gateway provider. */
    SMS_DEV,

    /** Global InfoBip HTTP SMS gateway provider. */
    INFO_BIP,
}

/**
 * Describes how an emergency SMS alert was delivered or handed off to the device.
 */
sealed interface AlertDeliveryOutcome {
    /**
     * Alert was accepted by a remote HTTP SMS gateway.
     *
     * @property provider The [SmsProviderType] that accepted the request.
     * @property recipientCount Number of recipient messages accepted by the gateway.
     */
    data class SentViaGateway(val provider: SmsProviderType, val recipientCount: Int) : AlertDeliveryOutcome

    /** Alert was dispatched directly through the platform's silent device SMS API. */
    data object SentViaDeviceSms : AlertDeliveryOutcome

    /** Platform opened the native messaging UI and requires explicit user confirmation to send. */
    data object UserConfirmationRequired : AlertDeliveryOutcome
}
