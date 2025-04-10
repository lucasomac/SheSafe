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

class MainActivity : ComponentActivity() {
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

                    SheSafeApp(
                        navController = navController,
                        isShownBottomBar = isShownBottomBar,
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