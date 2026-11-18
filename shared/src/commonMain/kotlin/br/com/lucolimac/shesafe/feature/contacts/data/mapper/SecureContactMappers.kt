package br.com.lucolimac.shesafe.feature.contacts.data.mapper

import br.com.lucolimac.shesafe.core.database.entity.SecureContactEntity
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import kotlinx.serialization.Serializable

/**
 * Serializable Data Transfer Object representing a trusted contact document in Cloud Firestore.
 *
 * @property name Display name of the trusted contact stored in Firestore.
 * @property phoneNumber Normalized digits-only phone number stored in Firestore.
 */
@Serializable
data class SecureContactFirestoreDto(
    val name: String = "",
    val phoneNumber: String = "",
)

/**
 * Maps a local Room [SecureContactEntity] into a domain [SecureContact] model.
 *
 * @return The corresponding domain [SecureContact].
 */
fun SecureContactEntity.toDomain(): SecureContact = SecureContact(
    name = name,
    phoneNumber = phoneNumber,
)

/**
 * Maps a domain [SecureContact] into a Room [SecureContactEntity] scoped to [userEmail].
 *
 * @param userEmail Email address of the authenticated user who owns this contact.
 * @return A [SecureContactEntity] ready for local database persistence.
 */
fun SecureContact.toEntity(userEmail: String): SecureContactEntity = SecureContactEntity(
    phoneNumber = phoneNumber,
    name = name,
    userEmail = userEmail,
)

/**
 * Maps a remote [SecureContactFirestoreDto] document payload into a domain [SecureContact] model.
 *
 * @return The corresponding domain [SecureContact].
 */
fun SecureContactFirestoreDto.toDomain(): SecureContact = SecureContact(
    name = name,
    phoneNumber = phoneNumber,
)

/**
 * Maps a domain [SecureContact] into a [SecureContactFirestoreDto] for Cloud Firestore persistence.
 *
 * @return A serializable [SecureContactFirestoreDto] representing the contact.
 */
fun SecureContact.toFirestoreDto(): SecureContactFirestoreDto = SecureContactFirestoreDto(
    name = name,
    phoneNumber = phoneNumber,
)
