package br.com.lucolimac.shesafe.feature.helprequests.data.mapper

import br.com.lucolimac.shesafe.core.database.entity.HelpRequestEntity
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import dev.gitlive.firebase.firestore.GeoPoint
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable
import kotlin.time.Instant

/** Number of milliseconds in one second, used for Firestore [Timestamp] conversion. */
private const val MILLIS_PER_SECOND = 1000L

/** Number of nanoseconds in one millisecond, used for Firestore [Timestamp] conversion. */
private const val NANOS_PER_MILLI = 1_000_000

/**
 * Firestore document transfer object representing a persisted emergency help request.
 *
 * @property phoneNumbers Recipient phone numbers notified during the alert.
 * @property location Geographic coordinates captured when the alert was triggered, or `null` if unavailable.
 * @property createdAt Firestore server or client timestamp indicating when the request was recorded.
 */
@Serializable
data class HelpRequestFirestoreDto(
    val phoneNumbers: List<String> = emptyList(),
    val location: GeoPoint? = null,
    val createdAt: Timestamp? = null,
)

/**
 * Maps a local Room [HelpRequestEntity] into the domain [HelpRequest] model.
 *
 * Splits the comma-separated phone numbers and reconstructs [LocationCoordinates] only when both
 * latitude and longitude are present.
 *
 * @return The corresponding [HelpRequest] domain instance.
 */
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

/**
 * Maps a domain [HelpRequest] into a Room [HelpRequestEntity] scoped to [userEmail].
 *
 * Generates a deterministic fallback identifier based on [HelpRequest.createdAt] when [HelpRequest.id] is blank.
 *
 * @param userEmail Email address of the authenticated user who owns the record.
 * @return A [HelpRequestEntity] ready for local Room persistence.
 */
fun HelpRequest.toEntity(userEmail: String): HelpRequestEntity = HelpRequestEntity(
    id = id.ifBlank { "hr_${createdAt.toEpochMilliseconds()}" },
    userEmail = userEmail,
    phoneNumbersCsv = phoneNumbers.joinToString(","),
    latitude = location?.latitude,
    longitude = location?.longitude,
    createdAtEpochMs = createdAt.toEpochMilliseconds(),
)

/**
 * Maps a [HelpRequestFirestoreDto] and its Firestore [documentId] into a domain [HelpRequest].
 *
 * Filters out `(0.0, 0.0)` "Null Island" coordinates so unavailable locations are represented as `null`.
 *
 * @param documentId Identifier of the source Firestore document.
 * @return The hydrated [HelpRequest] domain model.
 */
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

/**
 * Maps a domain [HelpRequest] into a [HelpRequestFirestoreDto] suitable for Firestore serialization.
 *
 * @return A [HelpRequestFirestoreDto] containing the recipients, optional [GeoPoint], and [Timestamp].
 */
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
