package br.com.lucolimac.shesafe.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import br.com.lucolimac.shesafe.feature.auth.domain.usecase.GetCurrentUserUseCase
import br.com.lucolimac.shesafe.feature.auth.presentation.ui.LoginScreen
import br.com.lucolimac.shesafe.feature.contacts.presentation.ui.RegisterSecureContactScreen
import br.com.lucolimac.shesafe.feature.contacts.presentation.ui.SecureContactsScreen
import br.com.lucolimac.shesafe.feature.helprequests.presentation.ui.HelpRequestsScreen
import br.com.lucolimac.shesafe.feature.home.presentation.ui.HomeScreen
import br.com.lucolimac.shesafe.feature.profile.presentation.ui.ProfileScreen
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.cd_add_contact
import shesafe.shared.generated.resources.cd_back
import shesafe.shared.generated.resources.home_24px
import shesafe.shared.generated.resources.list_24px
import shesafe.shared.generated.resources.person_24px
import shesafe.shared.generated.resources.title_edit_secure_contact
import shesafe.shared.generated.resources.title_help_request_sent
import shesafe.shared.generated.resources.title_home
import shesafe.shared.generated.resources.title_new_secure_contact
import shesafe.shared.generated.resources.title_profile
import shesafe.shared.generated.resources.title_secure_contacts

/**
 * Top-level navigation host and application chrome scaffold for SheSafe.
 *
 * Resolves the initial destination based on authentication status via [getCurrentUserUseCase],
 * coordinates top app bar, bottom navigation bar, floating action button, and snackbar feedback,
 * and registers all [SheSafeRoute] destinations.
 *
 * @param navController [NavHostController] managing the navigation back stack.
 * @param getCurrentUserUseCase Use case queried once at startup to choose between [SheSafeRoute.Home]
 *   and [SheSafeRoute.Login].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheSafeNavigation(
    navController: NavHostController = rememberNavController(),
    getCurrentUserUseCase: GetCurrentUserUseCase = koinInject(),
) {
    val initialRoute = remember {
        if (getCurrentUserUseCase() != null) SheSafeRoute.Home else SheSafeRoute.Login
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val showSnackbar: (StringResource) -> Unit = { res ->
        coroutineScope.launch {
            snackbarHostState.showSnackbar(getString(res))
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val isHome = destination?.hasRoute<SheSafeRoute.Home>() == true
    val isContacts = destination?.hasRoute<SheSafeRoute.SecureContacts>() == true
    val isProfile = destination?.hasRoute<SheSafeRoute.Profile>() == true
    val isHelpRequests = destination?.hasRoute<SheSafeRoute.HelpRequests>() == true
    val isRegisterContact = destination?.hasRoute<SheSafeRoute.RegisterSecureContact>() == true

    val isEditContact = remember(backStackEntry, isRegisterContact) {
        val entry = backStackEntry
        isRegisterContact &&
            entry != null &&
            !entry.toRoute<SheSafeRoute.RegisterSecureContact>().phoneNumber.isNullOrBlank()
    }

    val showBottomBar = isHome || isContacts || isProfile
    val showTopBar = isHelpRequests || isRegisterContact

    val selectedTab = when {
        isContacts -> BottomNavTab.SECURE_CONTACTS
        isProfile -> BottomNavTab.PROFILE
        else -> BottomNavTab.HOME
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (showTopBar) {
                val titleText = when {
                    isHelpRequests -> stringResource(Res.string.title_help_request_sent)
                    isEditContact -> stringResource(Res.string.title_edit_secure_contact)
                    else -> stringResource(Res.string.title_new_secure_contact)
                }
                TopAppBar(
                    title = { Text(text = titleText) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.cd_back),
                            )
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                SheSafeBottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            if (isContacts) {
                FloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    onClick = {
                        navController.navigate(SheSafeRoute.RegisterSecureContact(phoneNumber = null))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.cd_add_contact),
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = initialRoute,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<SheSafeRoute.Login> {
                LoginScreen(
                    onNavigateToHome = {
                        navController.navigate(SheSafeRoute.Home) {
                            popUpTo<SheSafeRoute.Login> { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onShowMessage = showSnackbar,
                )
            }

            composable<SheSafeRoute.Home> {
                HomeScreen(
                    onNavigateToRegisterContact = {
                        navController.navigate(SheSafeRoute.RegisterSecureContact(phoneNumber = null))
                    },
                    onShowMessage = showSnackbar,
                )
            }

            composable<SheSafeRoute.SecureContacts> {
                SecureContactsScreen(
                    onNavigateToEditContact = { phoneNumber ->
                        navController.navigate(SheSafeRoute.RegisterSecureContact(phoneNumber = phoneNumber))
                    },
                    onShowMessage = showSnackbar,
                )
            }

            composable<SheSafeRoute.RegisterSecureContact> { entry ->
                val route = entry.toRoute<SheSafeRoute.RegisterSecureContact>()
                RegisterSecureContactScreen(
                    phoneNumberArg = route.phoneNumber,
                    onNavigateBack = { navController.popBackStack() },
                    onShowMessage = showSnackbar,
                )
            }

            composable<SheSafeRoute.Profile> {
                ProfileScreen(
                    onNavigateToHelpRequests = {
                        navController.navigate(SheSafeRoute.HelpRequests)
                    },
                    onNavigateToLogin = {
                        navController.navigate(SheSafeRoute.Login) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onShowMessage = showSnackbar,
                )
            }

            composable<SheSafeRoute.HelpRequests> {
                HelpRequestsScreen()
            }
        }
    }
}

/**
 * Renders the Material 3 bottom navigation bar displaying all top-level [BottomNavTab] destinations.
 *
 * @param selectedTab Currently active [BottomNavTab] highlighted in the bar.
 * @param onTabSelected Callback invoked when the user selects a tab item.
 * @param modifier Optional [Modifier] applied to the [NavigationBar].
 */
@Composable
private fun SheSafeBottomNavigationBar(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        BottomNavTab.entries.forEach { tab ->
            val (iconRes, labelRes) = when (tab) {
                BottomNavTab.SECURE_CONTACTS -> Res.drawable.list_24px to Res.string.title_secure_contacts
                BottomNavTab.HOME -> Res.drawable.home_24px to Res.string.title_home
                BottomNavTab.PROFILE -> Res.drawable.person_24px to Res.string.title_profile
            }
            val labelText = stringResource(labelRes)
            NavigationBarItem(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = labelText,
                    )
                },
                label = { Text(text = labelText) },
            )
        }
    }
}
