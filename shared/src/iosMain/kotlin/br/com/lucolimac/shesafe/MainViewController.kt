package br.com.lucolimac.shesafe

import androidx.compose.ui.window.ComposeUIViewController
import br.com.lucolimac.shesafe.di.initKoin
import platform.UIKit.UIViewController

/**
 * iOS entry point invoked from Swift to create the root [UIViewController] hosting [SheSafeSharedApp].
 *
 * Initializes the Koin dependency graph and Google authentication provider before constructing
 * the Compose Multiplatform view controller.
 *
 * @return Root [UIViewController] rendering the shared Compose UI on iOS.
 */
@Suppress("FunctionNaming")
fun MainViewController(): UIViewController {
    initKoin()
    AuthProviderBootstrap.initializeGoogleAuth()
    return ComposeUIViewController {
        SheSafeSharedApp()
    }
}
