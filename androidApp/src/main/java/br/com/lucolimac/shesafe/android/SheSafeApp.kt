package br.com.lucolimac.shesafe.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeBottomBar
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.screen.LoginScreen
import br.com.lucolimac.shesafe.android.presentation.screen.ProfileScreen
import br.com.lucolimac.shesafe.android.presentation.screen.RegisterSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.screen.SecureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
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
    secureContactViewModel: SecureContactViewModel,
    isShownBottomBar: Boolean = true,
    isShowFab: Boolean = false,
    bottomAppBarItemSelected: NavigationItem = MenuItems[1],
    onBottomAppBarItemSelectedChange: (NavigationItem) -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            if (isShownBottomBar) {
                SheSafeBottomBar(
                    selected = bottomAppBarItemSelected,
                    menus = MenuItems,
                    onBottomAppBarItemSelectedChange = onBottomAppBarItemSelectedChange
                )
            }
        },
        floatingActionButton = {
            if (isShowFab) {
                FloatingActionButton(
                    {
                        navigateTo(
                            navController, SheSafeDestination.RegisterContact
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    content = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Icon",
                        )
                    },
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SheSafeDestination.Login.route.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(SheSafeDestination.Login.route.name) { LoginScreen(navController) }
            composable(SheSafeDestination.Home.route.name) {
                HomeScreen({
                    navigateTo(
                        navController, SheSafeDestination.Contacts
                    )
                })
            }
            composable(SheSafeDestination.Contacts.route.name) {
                SecureContactsScreen(
                    secureContactViewModel
                )
            }
            composable(SheSafeDestination.Profile.route.name) { ProfileScreen() }
            composable(SheSafeDestination.RegisterContact.route.name) {
                RegisterSecureContactScreen(secureContactViewModel) {
                    navigateTo(
                        navController, SheSafeDestination.Contacts
                    )
                    secureContactViewModel.resetRegistered()
                }
            }
//                composable(NavigationItem.Settings.route) { SettingsScreen(navController) }) {}
        }
    }
}

fun navigateTo(
    navController: NavHostController,
    destination: SheSafeDestination,
    popUpTo: SheSafeDestination? = null
) {
    navController.navigate(destination.route.name) {
        popUpTo?.let {
            popUpTo(it.route.name) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
}