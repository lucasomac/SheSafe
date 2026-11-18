package br.com.lucolimac.shesafe

import androidx.compose.ui.window.ComposeUIViewController
import br.com.lucolimac.shesafe.di.initKoin
import platform.UIKit.UIViewController

@Suppress("FunctionNaming")
fun MainViewController(): UIViewController {
    initKoin()
    AuthProviderBootstrap.initializeGoogleAuth()
    return ComposeUIViewController {
        SheSafeSharedApp()
    }
}
