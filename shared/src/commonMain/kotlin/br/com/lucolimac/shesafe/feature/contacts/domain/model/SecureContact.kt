package br.com.lucolimac.shesafe.feature.contacts.domain.model

/**
 * Domain model representing a trusted emergency contact designated to receive SOS alerts.
 *
 * @property name Full or display name of the trusted contact.
 * @property phoneNumber Normalized digits-only phone number of the trusted contact.
 */
data class SecureContact(
    val name: String,
    val phoneNumber: String,
)
