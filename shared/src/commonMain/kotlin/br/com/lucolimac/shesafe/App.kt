package br.com.lucolimac.shesafe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Shared application seam for the incremental Compose Multiplatform migration.
 *
 * Platform-specific services and the existing Android navigation remain outside
 * this shell until their common interfaces are ready to be migrated.
 */
@Composable
fun SheSafeSharedApp() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "SheSafe",
                    style = MaterialTheme.typography.h4,
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Your personal safety companion",
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}
