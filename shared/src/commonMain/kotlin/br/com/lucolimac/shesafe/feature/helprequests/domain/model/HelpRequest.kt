package br.com.lucolimac.shesafe.feature.helprequests.domain.model

import br.com.lucolimac.shesafe.platform.LocationCoordinates
import kotlin.time.Instant

data class HelpRequest(
    val id: String = "",
    val phoneNumbers: List<String>,
    val location: LocationCoordinates?,
    val createdAt: Instant,
) {
    val mapLink: String?
        get() = location?.let {
            "https://www.google.com/maps/search/?api=1&query=${it.latitude},${it.longitude}"
        }
}

enum class SmsProviderType {
    SMS_DEV,
    INFO_BIP,
}

sealed interface AlertDeliveryOutcome {
    data class SentViaGateway(val provider: SmsProviderType, val recipientCount: Int) : AlertDeliveryOutcome
    data object SentViaDeviceSms : AlertDeliveryOutcome
    data object UserConfirmationRequired : AlertDeliveryOutcome
}
