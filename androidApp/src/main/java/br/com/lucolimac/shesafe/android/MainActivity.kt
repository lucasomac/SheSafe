package br.com.lucolimac.shesafe.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.navigation.BASE_SECURE_CONTACT_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.HELP_REQUESTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.HOME_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.PROFILE_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.REGISTER_CONTACT_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.SECURE_CONTACTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {
    private val secureContactViewModel by inject<SecureContactViewModel>(SecureContactViewModel::class.java)
    private val helpRequestViewModel by inject<HelpRequestViewModel>(HelpRequestViewModel::class.java)
    private val settingsViewModel by inject<SettingsViewModel>(SettingsViewModel::class.java)
    private val authViewModel by inject<AuthViewModel>(AuthViewModel::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            LaunchedEffect(Unit) {
                navController.addOnDestinationChangedListener { _, _, _ ->
                    val routes = navController.visibleEntries.value.map {
                        it.destination.route
                    }
                    Log.i("MainActivity", "onCreate: back stack - $routes")
                }
            }
            val backStackEntryState by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntryState?.destination

            SheSafeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val currentRoute = currentDestination?.route
                    val selectedItem by remember(currentDestination) {
                        val item = when (currentRoute) {
                            SECURE_CONTACTS_ROUTE -> NavigationItem.SecureContacts
                            HOME_ROUTE -> NavigationItem.Home
                            PROFILE_ROUTE -> NavigationItem.Profile
                            else -> {
                                Log.w(
                                    "MainActivity",
                                    "onCreate: current destination is not a valid menu item"
                                )
                                NavigationItem.Home // Default to the first item if no match
                            }
                        }
                        mutableStateOf(item)
                    }
                    val isShownBottomBar = when (currentRoute) {
                        SECURE_CONTACTS_ROUTE, HOME_ROUTE, PROFILE_ROUTE -> true
                        else -> false
                    }
                    val isShownFab = when (currentRoute) {
                        SECURE_CONTACTS_ROUTE -> true
                        else -> false
                    }
                    // Should be show appBar only in RegisterSecureContactScreen and HelpRequestsScreen
                    val isShowAppBar = when (currentRoute) {
                        HELP_REQUESTS_ROUTE, REGISTER_CONTACT_ROUTE -> true
                        else -> false
                    }
                    // Should verify if current destination has arguments
                    SheSafeApp(
                        navController = navController,
                        secureContactViewModel = secureContactViewModel,
                        helpRequestViewModel = helpRequestViewModel,
                        settingsViewModel = settingsViewModel,
                        authViewModel = authViewModel,
                        isShownBottomBar = isShownBottomBar,
                        isShowFab = isShownFab,
                        isShowTopBar = isShowAppBar,
                        bottomAppBarItemSelected = selectedItem,
                        onBottomAppBarItemSelectedChange = {
                            val route = it.route
                            navController.navigate(route) {
                                launchSingleTop = true
                                popUpTo(route)
                            }
                        })
                }
            }
        }
    }
}