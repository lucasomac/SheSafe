package br.com.lucolimac.shesafe.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.lucolimac.shesafe.SheSafeSharedApp

/**
 * Single-activity Android entry point hosting the shared Compose Multiplatform UI ([SheSafeSharedApp]).
 */
class MainActivity : ComponentActivity() {
    /**
     * Enables edge-to-edge system bar rendering and mounts [SheSafeSharedApp] as the activity content.
     *
     * @param savedInstanceState Previously saved instance state bundle, or `null` on cold start.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SheSafeSharedApp()
        }
    }
}
