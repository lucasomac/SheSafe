package br.com.lucolimac.shesafe.feature.contacts.data.mapper

import br.com.lucolimac.shesafe.core.database.entity.SecureContactEntity
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import kotlinx.serialization.Serializable

@Serializable
data class SecureContactFirestoreDto(
    val name: String = "",
    val phoneNumber: String = "",
)

fun SecureContactEntity.toDomain(): SecureContact = SecureContact(
    name = name,
    phoneNumber = phoneNumber,
)

fun SecureContact.toEntity(userEmail: String): SecureContactEntity = SecureContactEntity(
    phoneNumber = phoneNumber,
    name = name,
    userEmail = userEmail,
)

fun SecureContactFirestoreDto.toDomain(): SecureContact = SecureContact(
    name = name,
    phoneNumber = phoneNumber,
)

fun SecureContact.toFirestoreDto(): SecureContactFirestoreDto = SecureContactFirestoreDto(
    name = name,
    phoneNumber = phoneNumber,
)
