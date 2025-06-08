package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val icon: ImageVector
) {
    object SecureContacts : NavigationItem(
        icon = Icons.AutoMirrored.Filled.List
    )

    object Home : NavigationItem(
        icon = Icons.Filled.Home
    )

    object Profile : NavigationItem(
        icon = Icons.Filled.Person
    )
}

