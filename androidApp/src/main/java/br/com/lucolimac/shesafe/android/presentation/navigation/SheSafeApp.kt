package br.com.lucolimac.shesafe.android.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.screen.LoginScreen
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme
import br.com.lucolimac.shesafe.route.NavigationItem


@Composable
fun SheSafeApp() {
    val navController = rememberNavController()
    SheSafeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = NavigationItem.Login.route) {
                composable(NavigationItem.Login.route) { LoginScreen(navController) }
                composable(NavigationItem.Home.route) { HomeScreen(navController) }
//                composable(NavigationItem.Contacts.route) { ContactsScreen(navController) }
//                composable(NavigationItem.Settings.route) { SettingsScreen(navController) }) {}
//                composable(NavigationItem.Profile.route) { ProfileScreen(navController) }
            }
        }
    }
}