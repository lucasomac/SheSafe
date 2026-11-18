package br.com.lucolimac.shesafe.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.MapKit.MKCoordinateRegionMakeWithDistance
import platform.MapKit.MKMapView

/** Latitudinal and longitudinal span in meters when centering the map on a known user location. */
private const val REGION_METERS = 800.0

/** Country-wide span in meters used when [EmergencyMapView] falls back to the Brazil overview. */
private const val FALLBACK_METERS = 3_500_000.0

/** Default fallback latitude centered on Brasilia, Brazil when no user fix is available. */
private const val DEFAULT_BR_LAT = -15.793889

/** Default fallback longitude centered on Brasilia, Brazil when no user fix is available. */
private const val DEFAULT_BR_LNG = -47.882778

/**
 * iOS implementation of [EmergencyMapView] hosting a native MapKit [MKMapView] inside [UIKitView].
 *
 * Updates the visible region smoothly whenever [userLocation] changes and reflects
 * [hasLocationPermission] on [MKMapView.showsUserLocation].
 *
 * @param userLocation Current geographic coordinates of the user, or `null` to display the Brazil overview.
 * @param hasLocationPermission Whether CoreLocation authorization permits showing the user location indicator.
 * @param modifier [Modifier] applied to the interop [UIKitView].
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun EmergencyMapView(
    userLocation: LocationCoordinates?,
    hasLocationPermission: Boolean,
    modifier: Modifier,
) {
    UIKitView(
        factory = {
            MKMapView().apply {
                showsUserLocation = hasLocationPermission
                val initialCoord = CLLocationCoordinate2DMake(
                    userLocation?.latitude ?: DEFAULT_BR_LAT,
                    userLocation?.longitude ?: DEFAULT_BR_LNG,
                )
                val distance = if (userLocation != null) REGION_METERS else FALLBACK_METERS
                setRegion(
                    MKCoordinateRegionMakeWithDistance(initialCoord, distance, distance),
                    animated = false,
                )
            }
        },
        modifier = modifier,
        update = { mapView ->
            mapView.showsUserLocation = hasLocationPermission
            if (userLocation != null) {
                val coord = CLLocationCoordinate2DMake(userLocation.latitude, userLocation.longitude)
                mapView.setRegion(
                    MKCoordinateRegionMakeWithDistance(coord, REGION_METERS, REGION_METERS),
                    animated = true,
                )
            }
        },
    )
}

/**
 * iOS implementation of [rememberLocationPermissionControl] backed by [CLLocationManager].
 *
 * Evaluates `AuthorizedWhenInUse` and `AuthorizedAlways` statuses and requests when-in-use
 * authorization when invoked prior to grant.
 *
 * @param onPermissionResult Callback invoked with the current authorization boolean status.
 * @return Remembered [LocationPermissionControl] bound to a retained [CLLocationManager] instance.
 */
@Composable
actual fun rememberLocationPermissionControl(
    onPermissionResult: (Boolean) -> Unit,
): LocationPermissionControl {
    val manager = remember { CLLocationManager() }

    fun isAuthorized(): Boolean {
        val status = manager.authorizationStatus
        return status == kCLAuthorizationStatusAuthorizedAlways ||
            status == kCLAuthorizationStatusAuthorizedWhenInUse
    }

    var granted by remember { mutableStateOf(isAuthorized()) }

    return remember(granted, manager) {
        LocationPermissionControl(
            isGranted = granted,
            requestPermission = {
                if (isAuthorized()) {
                    granted = true
                    onPermissionResult(true)
                } else {
                    manager.requestWhenInUseAuthorization()
                    onPermissionResult(isAuthorized())
                }
            },
        )
    }
}
