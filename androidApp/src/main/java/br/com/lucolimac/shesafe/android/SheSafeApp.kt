package br.com.lucolimac.shesafe.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeBottomBar
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.navigation.helpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.homeScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.loginScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.navigateToRegisterSecureContact
import br.com.lucolimac.shesafe.android.presentation.navigation.profileScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.registerSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.secureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import br.com.lucolimac.shesafe.route.SheSafeDestination

val MenuItems = listOf(
    NavigationItem(
        icon = Icons.AutoMirrored.Filled.List,
        sheSafeDestination = SheSafeDestination.Contacts,
    ),
    NavigationItem(
        icon = Icons.Filled.Home,
        sheSafeDestination = SheSafeDestination.Home,
    ),
    NavigationItem(
        icon = Icons.Filled.Person,
        sheSafeDestination = SheSafeDestination.Profile,
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheSafeApp(
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    isShownBottomBar: Boolean = true,
    isShowFab: Boolean = false,
    isShowTopBar: Boolean = false,
    titleTopBar: String = "",
    bottomAppBarItemSelected: NavigationItem = MenuItems[1],
    onBottomAppBarItemSelectedChange: (NavigationItem) -> Unit = {},
) {
    val startDestination by authViewModel.startDestination.collectAsState()
    Scaffold(
        topBar = {
            if (isShowTopBar) {
                TopAppBar(
                    title = { Text(text = titleTopBar) },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                            secureContactViewModel.resetSecureContact()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Navigation Icon",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                )
            }
        },
        bottomBar = {
            if (isShownBottomBar) {
                SheSafeBottomBar(
                    selected = bottomAppBarItemSelected,
                    menus = MenuItems,
                    onBottomAppBarItemSelectedChange = onBottomAppBarItemSelectedChange,
                )
            }
        },
        floatingActionButton = {
            if (isShowFab) {
                FloatingActionButton(
                    navController::navigateToRegisterSecureContact,
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
            startDestination = startDestination.route.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            loginScreen(navController)
            homeScreen(navController, secureContactViewModel, authViewModel)
            secureContactsScreen(navController, secureContactViewModel)
            profileScreen(
                helpRequestViewModel,
                settingsViewModel,
                authViewModel,
                navController,
                secureContactViewModel
            )
            registerSecureContactScreen(secureContactViewModel, navController)
            helpRequestsScreen(helpRequestViewModel)
        }
    }
}
