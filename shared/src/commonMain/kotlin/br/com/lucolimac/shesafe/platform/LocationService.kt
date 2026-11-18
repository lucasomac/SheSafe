package br.com.lucolimac.shesafe.platform

import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Geographic coordinates expressed in WGS-84 decimal degrees.
 *
 * @property latitude Latitude in decimal degrees (`-90.0` to `90.0`).
 * @property longitude Longitude in decimal degrees (`-180.0` to `180.0`).
 */
data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
)

/**
 * Outcome of a single-shot device location lookup requested via [LocationService].
 */
sealed interface LocationResult {
    /**
     * Indicates that a valid device location fix was obtained.
     *
     * @property coordinates Resolved [LocationCoordinates] of the device.
     */
    data class Available(val coordinates: LocationCoordinates) : LocationResult

    /** Indicates that foreground location permission has not yet been granted by the user. */
    data object PermissionRequired : LocationResult

    /** Indicates that location services are enabled and permitted, but no fix could be resolved. */
    data object Unavailable : LocationResult

    /**
     * Indicates that reading the current location failed due to a platform or provider error.
     *
     * @property reason Diagnostic description of the failure.
     */
    data class Failed(val reason: String) : LocationResult
}

/**
 * Platform abstraction for retrieving a single current geographic location fix from the host OS.
 */
interface LocationService {
    /**
     * Asynchronously requests the device's current location and delivers the outcome to [onResult].
     *
     * @param onResult Callback invoked with the resulting [LocationResult].
     */
    fun requestCurrentLocation(onResult: (LocationResult) -> Unit)
}

/**
 * Suspends until [LocationService.requestCurrentLocation] delivers a [LocationResult].
 *
 * @return Resolved [LocationResult] from the underlying platform location provider.
 */
suspend fun LocationService.awaitCurrentLocation(): LocationResult =
    suspendCancellableCoroutine { cont ->
        requestCurrentLocation { result ->
            if (cont.isActive) {
                cont.resume(result)
            }
        }
    }
