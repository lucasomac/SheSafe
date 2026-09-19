package br.com.lucolimac.shesafe.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlinx.cinterop.useContents

@OptIn(ExperimentalForeignApi::class)
class IosLocationService : LocationService {
    private val manager = CLLocationManager()
    private var result: ((LocationResult) -> Unit)? = null

    init {
        manager.delegate = Delegate()
    }

    override fun requestCurrentLocation(onResult: (LocationResult) -> Unit) {
        result = onResult
        when (manager.authorizationStatus) {
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted,
            -> onResult(LocationResult.Failed("Location permission was denied"))
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse,
            -> manager.requestLocation()
            else -> manager.requestWhenInUseAuthorization()
        }
    }

    private inner class Delegate : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(
            manager: CLLocationManager,
            didChangeAuthorizationStatus: CLAuthorizationStatus,
        ) {
            if (
                didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways ||
                didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse
            ) {
                manager.requestLocation()
            } else if (
                didChangeAuthorizationStatus == kCLAuthorizationStatusDenied ||
                didChangeAuthorizationStatus == kCLAuthorizationStatusRestricted
            ) {
                result?.invoke(LocationResult.Failed("Location permission was denied"))
                result = null
            }
        }

        override fun locationManager(
            manager: CLLocationManager,
            didUpdateLocations: List<*>,
        ) {
            val location = didUpdateLocations.lastOrNull() as? CLLocation
            result?.invoke(
                location?.let {
                    val coordinate = it.coordinate.useContents {
                        LocationCoordinates(latitude, longitude)
                    }
                    LocationResult.Available(coordinate)
                } ?: LocationResult.Unavailable,
            )
            result = null
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            result?.invoke(LocationResult.Failed(didFailWithError.localizedDescription))
            result = null
        }
    }
}
