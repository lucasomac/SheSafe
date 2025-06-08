package br.com.lucolimac.shesafe.android.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.lucolimac.shesafe.android.presentation.navigation.HOME_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.PROFILE_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.SECURE_CONTACTS_ROUTE

sealed class NavigationItem(
    val icon: ImageVector, val route: String
) {
    object SecureContacts : NavigationItem(
        icon = Icons.AutoMirrored.Filled.List,
        route = SECURE_CONTACTS_ROUTE,
    )

    object Home : NavigationItem(
        icon = Icons.Filled.Home,
        route = HOME_ROUTE,
    )

    object Profile : NavigationItem(
        icon = Icons.Filled.Person,
        route = PROFILE_ROUTE,
    )
}