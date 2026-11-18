package br.com.lucolimac.shesafe.core.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.lucolimac.shesafe.platform.LocationCoordinates

/**
 * Renders a platform-native interactive map centered on [userLocation] when available.
 *
 * Uses Google Maps Compose on Android and `MKMapView` via `UIKitView` on iOS, falling back to a
 * country-level overview of Brazil when [userLocation] is `null`.
 *
 * @param userLocation Current geographic coordinates of the user, or `null` if not yet resolved.
 * @param hasLocationPermission Whether foreground location permission has been granted for blue-dot rendering.
 * @param modifier Optional [Modifier] applied to the map container.
 */
@Composable
expect fun EmergencyMapView(
    userLocation: LocationCoordinates?,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier,
)

/**
 * State holder exposing current location permission status and an action to trigger the OS permission prompt.
 *
 * @property isGranted `true` when foreground location access is currently authorized on the host platform.
 * @property requestPermission Lambda that launches the platform runtime permission request flow.
 */
data class LocationPermissionControl(
    val isGranted: Boolean,
    val requestPermission: () -> Unit,
)

/**
 * Remembers a platform-specific [LocationPermissionControl] that tracks and requests foreground location access.
 *
 * @param onPermissionResult Callback invoked with `true` if permission was granted or `false` otherwise.
 * @return Remembered [LocationPermissionControl] reflecting live authorization state.
 */
@Composable
expect fun rememberLocationPermissionControl(
    onPermissionResult: (Boolean) -> Unit = {},
): LocationPermissionControl
