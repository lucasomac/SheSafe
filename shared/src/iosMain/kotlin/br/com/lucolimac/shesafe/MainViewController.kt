package br.com.lucolimac.shesafe

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    SheSafeSharedApp()
}
