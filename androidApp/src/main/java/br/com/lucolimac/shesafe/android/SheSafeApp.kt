package br.com.lucolimac.shesafe.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeBottomBar
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.screen.ContactsScreen
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.screen.LoginScreen
import br.com.lucolimac.shesafe.android.presentation.screen.ProfileScreen
import br.com.lucolimac.shesafe.route.SheSafeDestination

val MenuItems = listOf(
    NavigationItem(
        icon = Icons.AutoMirrored.Filled.List, sheSafeDestination = SheSafeDestination.Contacts
    ), NavigationItem(
        icon = Icons.Filled.Home, sheSafeDestination = SheSafeDestination.Home
    ), NavigationItem(
        icon = Icons.Filled.Person, sheSafeDestination = SheSafeDestination.Profile
    )
)

@Composable
fun SheSafeApp(
    navController: NavHostController,
    bottomAppBarItemSelected: NavigationItem = MenuItems[1],
    onBottomAppBarItemSelectedChange: (NavigationItem) -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            SheSafeBottomBar(
                selected = bottomAppBarItemSelected,
                menus = MenuItems,
                onBottomAppBarItemSelectedChange = onBottomAppBarItemSelectedChange
            )
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SheSafeDestination.Home.route.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(SheSafeDestination.Login.route.name) { LoginScreen(navController) }
            composable(SheSafeDestination.Home.route.name) { HomeScreen(navController) }
            composable(SheSafeDestination.Contacts.route.name) { ContactsScreen(navController) }
            composable(SheSafeDestination.Profile.route.name) { ProfileScreen(navController) }
//                composable(NavigationItem.Settings.route) { SettingsScreen(navController) }) {}
        }
    }
}