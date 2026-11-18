package br.com.lucolimac.shesafe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme
import br.com.lucolimac.shesafe.navigation.SheSafeNavigation

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
