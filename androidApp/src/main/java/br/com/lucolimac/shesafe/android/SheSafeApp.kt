package br.com.lucolimac.shesafe.android

import android.telephony.SmsManager
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeBottomBar
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.screen.HelpRequestsScreen
import br.com.lucolimac.shesafe.android.presentation.screen.HomeScreen
import br.com.lucolimac.shesafe.android.presentation.screen.LoginScreen
import br.com.lucolimac.shesafe.android.presentation.screen.ProfileScreen
import br.com.lucolimac.shesafe.android.presentation.screen.RegisterSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.screen.SecureContactsScreen
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
                    {
                        navigateTo(
                            navController,
                            SheSafeDestination.RegisterContact,
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
            startDestination = startDestination.route.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(SheSafeDestination.Login.route.name) { LoginScreen(navController) }
            composable(SheSafeDestination.Home.route.name) {
                HomeScreen(
                    onOrderHelp = { message, phoneNumber, context ->
                        try {
                            val smsManager = ContextCompat.getSystemService<SmsManager>(
                                context,
                                SmsManager::class.java,
                            )
                            smsManager?.sendTextMessage(phoneNumber, null, message, null, null)
                            true
                        } catch (_: Exception) {
                            false
                        }
                    },
                    onNoContacts = {
                        navigateTo(
                            navController,
                            SheSafeDestination.Contacts,
                        )
                    },
                    secureContactViewModel = secureContactViewModel,
                    authViewModel = authViewModel,
                )
            }
            composable(SheSafeDestination.Contacts.route.name) {
                SecureContactsScreen(
                    onEditAction = { secureContact ->
                        navigateTo(
                            navController,
                            SheSafeDestination.RegisterContact,
                            secureContact.phoneNumber,
                        )
                    },
                    onDeleteAction = {
                        secureContactViewModel.deleteSecureContact(it.phoneNumber)
                    },
                    secureContactViewModel,
                )
            }
            composable(SheSafeDestination.Profile.route.name) {
                ProfileScreen(
                    helpRequestViewModel, settingsViewModel, authViewModel,
                    onHelpRequestsShowClick = {
                        navigateTo(
                            navController,
                            SheSafeDestination.HelpRequests,
                        )
                    },
                    onLogoutClick = {
                        // Clear the secure contact list
                        secureContactViewModel.resetAllStates()
                        // Clear the help request list
                        helpRequestViewModel.resetAllStates()
                        // Clear the settings
                        settingsViewModel.resetAllStates()
                        // Clear the auth state
                        authViewModel.resetAllStates()
                        // Logout the user
                        authViewModel.logoutUser()
                        // Navigate to the login screen
                        navigateTo(
                            navController,
                            SheSafeDestination.Login,
                            navigationToPopUpTo = SheSafeDestination.Home,
                        )
                    },
                )
            }
            composable(SheSafeDestination.RegisterContact.route.name + "/{secureContactPhoneNumber}") {
                val secureContactPhoneNumber =
                    it.arguments?.getString("secureContactPhoneNumber") ?: ""
                RegisterSecureContactScreen(
                    secureContactPhoneNumber = secureContactPhoneNumber, secureContactViewModel,
                    onBack = {
                        navigateTo(
                            navController,
                            SheSafeDestination.Contacts,
                        )
                        secureContactViewModel.resetSecureContact()
                    },
                )
            }
            composable(SheSafeDestination.HelpRequests.route.name) {
                HelpRequestsScreen(
                    helpRequestViewModel = helpRequestViewModel
                )
            }
        }
    }
}

fun navigateTo(
    navController: NavHostController,
    destination: SheSafeDestination,
    vararg navArgs: String = emptyArray(),
    navigationToPopUpTo: SheSafeDestination? = null,
) {
    val route = if (destination == SheSafeDestination.RegisterContact) {
        "${destination.route.name}/${
            navArgs.joinToString("/")
        }"
    } else {
        destination.route.name
    }
    navController.navigate(route) {
        navigationToPopUpTo?.let {
            popUpTo(it.route.name) {
                saveState = true
            }
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}
