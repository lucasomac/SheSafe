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

/**
 * iOS implementation of [LocationService] backed by Apple's CoreLocation [CLLocationManager].
 *
 * Manages authorization transitions and single-fix location callbacks via an internal
 * [CLLocationManagerDelegateProtocol] delegate.
 */
@OptIn(ExperimentalForeignApi::class)
class IosLocationService : LocationService {
    /** Underlying CoreLocation manager configured with [Delegate]. */
    private val manager = CLLocationManager()

    /** Pending completion callback awaiting the next authorization or location event. */
    private var result: ((LocationResult) -> Unit)? = null

    init {
        manager.delegate = Delegate()
    }

    /**
     * Requests a single current location fix from [CLLocationManager], prompting for when-in-use
     * authorization first if the authorization status is not yet determined.
     *
     * @param onResult Callback invoked with the resolved [LocationResult].
     */
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

    /**
     * CoreLocation delegate bridging Objective-C authorization, location update, and failure callbacks
     * into [LocationResult] emissions.
     */
    private inner class Delegate : NSObject(), CLLocationManagerDelegateProtocol {
        /**
         * Triggers a location fix once authorization is granted or reports failure if denied or restricted.
         *
         * @param manager Reporting [CLLocationManager] instance.
         * @param didChangeAuthorizationStatus Updated [CLAuthorizationStatus] value.
         */
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

        /**
         * Extracts the most recent [CLLocation] from [didUpdateLocations] and completes [result].
         *
         * @param manager Reporting [CLLocationManager] instance.
         * @param didUpdateLocations List of [CLLocation] fixes delivered by CoreLocation.
         */
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

        /**
         * Propagates CoreLocation lookup errors as [LocationResult.Failed].
         *
         * @param manager Reporting [CLLocationManager] instance.
         * @param didFailWithError Native [NSError] describing the failure.
         */
        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            result?.invoke(LocationResult.Failed(didFailWithError.localizedDescription))
            result = null
        }
    }
}
