package br.com.lucolimac.shesafe.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.presentation.component.BottomBarItems
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeBottomBar
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.BASE_SECURE_CONTACT_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.HELP_REQUESTS_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.NavigationItem
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.REGISTER_SECURE_CONTACT_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.SECURE_CONTACT_PHONE_NUMBER_ARGUMENT
import br.com.lucolimac.shesafe.android.presentation.navigation.SheSafeNavHost
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.navigateToRegisterSecureContact
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HomeViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheSafeApp(
    navController: NavHostController,
    secureContactViewModel: SecureContactViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    isShownBottomBar: Boolean = true,
    isShowFab: Boolean = false,
    isShowTopBar: Boolean = false,
    bottomAppBarItemSelected: NavigationItem = BottomBarItems[1],
    onBottomAppBarItemSelectedChange: (NavigationItem) -> Unit = {},
) {
    val isUpdateSecureContact = navController.currentDestination?.route?.startsWith(
        BASE_SECURE_CONTACT_ROUTE
    ) == true && navController.currentDestination?.arguments[SECURE_CONTACT_PHONE_NUMBER_ARGUMENT] != null
    val startDestination by authViewModel.startDestination.collectAsState()
    Scaffold(
        topBar = {
            if (isShowTopBar) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (navController.currentDestination?.route) {
                                REGISTER_SECURE_CONTACT_ROUTE -> if (isUpdateSecureContact) {
                                    stringResource(R.string.title_edit_secure_contact)
                                } else {
                                    stringResource(
                                        R.string.title_new_secure_contact
                                    )
                                }

                                HELP_REQUESTS_ROUTE -> stringResource(R.string.title_help_request_sent)
                                else -> ""
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navController.popBackStack()
                            secureContactViewModel.resetSelectedSecureContact()
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
                    menus = BottomBarItems,
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
        SheSafeNavHost(
            navController,
            startDestination, profileViewModel,
            homeViewModel,
            secureContactViewModel,
            authViewModel,
            helpRequestViewModel,
            settingsViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
