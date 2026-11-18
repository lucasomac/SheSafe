package br.com.lucolimac.shesafe.feature.helprequests.data.mapper

import br.com.lucolimac.shesafe.core.database.entity.HelpRequestEntity
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import dev.gitlive.firebase.firestore.GeoPoint
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable
import kotlin.time.Instant

private const val MILLIS_PER_SECOND = 1000L
private const val NANOS_PER_MILLI = 1_000_000

@Serializable
data class HelpRequestFirestoreDto(
    val phoneNumbers: List<String> = emptyList(),
    val location: GeoPoint? = null,
    val createdAt: Timestamp? = null,
)

fun HelpRequestEntity.toDomain(): HelpRequest = HelpRequest(
    id = id,
    phoneNumbers = phoneNumbersCsv.split(",").map(String::trim).filter(String::isNotBlank),
    location = if (latitude != null && longitude != null) {
        LocationCoordinates(latitude = latitude, longitude = longitude)
    } else {
        null
    },
    createdAt = Instant.fromEpochMilliseconds(createdAtEpochMs),
)

fun HelpRequest.toEntity(userEmail: String): HelpRequestEntity = HelpRequestEntity(
    id = id.ifBlank { "hr_${createdAt.toEpochMilliseconds()}" },
    userEmail = userEmail,
    phoneNumbersCsv = phoneNumbers.joinToString(","),
    latitude = location?.latitude,
    longitude = location?.longitude,
    createdAtEpochMs = createdAt.toEpochMilliseconds(),
)

fun HelpRequestFirestoreDto.toDomain(documentId: String): HelpRequest {
    val epochMs = createdAt?.let { ts ->
        (ts.seconds * MILLIS_PER_SECOND) + (ts.nanoseconds / NANOS_PER_MILLI)
    } ?: 0L
    val coords = location?.takeIf { !(it.latitude == 0.0 && it.longitude == 0.0) }?.let {
        LocationCoordinates(latitude = it.latitude, longitude = it.longitude)
    }
    return HelpRequest(
        id = documentId,
        phoneNumbers = phoneNumbers,
        location = coords,
        createdAt = Instant.fromEpochMilliseconds(epochMs),
    )
}

fun HelpRequest.toFirestoreDto(): HelpRequestFirestoreDto {
    val epochMillis = createdAt.toEpochMilliseconds()
    val seconds = epochMillis / MILLIS_PER_SECOND
    val nanos = ((epochMillis % MILLIS_PER_SECOND) * NANOS_PER_MILLI).toInt()
    return HelpRequestFirestoreDto(
        phoneNumbers = phoneNumbers,
        location = location?.let { GeoPoint(it.latitude, it.longitude) },
        createdAt = Timestamp(seconds = seconds, nanoseconds = nanos),
    )
}
