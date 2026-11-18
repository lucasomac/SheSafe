package br.com.lucolimac.shesafe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme
import br.com.lucolimac.shesafe.navigation.SheSafeNavigation

/**
 * Root multiplatform composable entry point for the SheSafe application.
 *
 * Applies [SheSafeTheme], provides a full-screen background [Surface], and hosts
 * the main [SheSafeNavigation] graph shared across target platforms.
 */
@Composable
fun SheSafeSharedApp() {
    SheSafeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            SheSafeNavigation()
        }
    }
}
