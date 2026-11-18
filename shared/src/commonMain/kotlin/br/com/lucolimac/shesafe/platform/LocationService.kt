package br.com.lucolimac.shesafe.platform

import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double,
)

sealed interface LocationResult {
    data class Available(val coordinates: LocationCoordinates) : LocationResult
    data object PermissionRequired : LocationResult
    data object Unavailable : LocationResult
    data class Failed(val reason: String) : LocationResult
}

interface LocationService {
    fun requestCurrentLocation(onResult: (LocationResult) -> Unit)
}

suspend fun LocationService.awaitCurrentLocation(): LocationResult =
    suspendCancellableCoroutine { cont ->
        requestCurrentLocation { result ->
            if (cont.isActive) {
                cont.resume(result)
            }
        }
    }
