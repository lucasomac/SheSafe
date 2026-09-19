package br.com.lucolimac.shesafe.platform

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
