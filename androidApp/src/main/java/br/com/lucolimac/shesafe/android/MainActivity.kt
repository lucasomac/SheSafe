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
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import br.com.lucolimac.shesafe.route.SheSafeDestination
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
                    val selectedItem by remember(currentDestination) {
                        val item = currentDestination?.let { destination ->
                            MenuItems.find {
                                it.sheSafeDestination.route.name == destination.route
                            }
                        } ?: MenuItems[1]
                        mutableStateOf(item)
                    }
                    val isShownBottomBar = currentDestination?.route.let { route ->
                        MenuItems.find {
                            it.sheSafeDestination.route.name == route
                        }
                    } != null
                    val isShownFab =
                        currentDestination?.route.let { route -> MenuItems.find { it.sheSafeDestination.route.name == route }?.sheSafeDestination == SheSafeDestination.Contacts }
                    // Should be show appBar only in RegisterSecureContactScreen and HelpRequestsScreen
                    val isShowAppBar = currentDestination?.route.let { route ->
                        route?.contains(SheSafeDestination.RegisterContact.route.name) == true || route == SheSafeDestination.HelpRequests.route.name
                    }
                    SheSafeApp(
                        navController = navController,
                        secureContactViewModel = secureContactViewModel,
                        helpRequestViewModel = helpRequestViewModel,
                        settingsViewModel = settingsViewModel,
                        authViewModel = authViewModel,
                        isShownBottomBar = isShownBottomBar,
                        isShowFab = isShownFab,
                        isShowTopBar = isShowAppBar,
                        titleTopBar = SheSafeDestination.SheSafeRoute.entries.find {
                            currentDestination?.route.toString().contains(it.name)
                        }?.title ?: "",
                        bottomAppBarItemSelected = selectedItem,
                        onBottomAppBarItemSelectedChange = {
                            val route = it.sheSafeDestination.route.name
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