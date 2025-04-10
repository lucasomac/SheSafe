package br.com.lucolimac.shesafe.android.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector
import br.com.lucolimac.shesafe.route.SheSafeDestination

data class NavigationItem(
    val icon: ImageVector,
    val sheSafeDestination: SheSafeDestination
)