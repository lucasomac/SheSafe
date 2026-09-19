package br.com.lucolimac.shesafe

import androidx.compose.runtime.Composable
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import br.com.lucolimac.shesafe.platform.LocationService
import br.com.lucolimac.shesafe.platform.platformLocationService
import br.com.lucolimac.shesafe.presentation.HomeScreen
import br.com.lucolimac.shesafe.presentation.HomeScreenState
import br.com.lucolimac.shesafe.presentation.LoginScreen

/**
 * Shared application seam for the incremental Compose Multiplatform migration.
 *
 * Platform-specific services are supplied through common interfaces.
 */
@Composable
fun SheSafeSharedApp(
    state: HomeScreenState = HomeScreenState(),
    locationService: LocationService = platformLocationService(),
    onHelpRequested: (LocationCoordinates?) -> Unit = {},
    showHome: Boolean = false,
) {
    if (showHome) {
        HomeScreen(
            state = state,
            locationService = locationService,
            onHelpRequested = onHelpRequested,
        )
    } else {
        LoginScreen()
    }
}
