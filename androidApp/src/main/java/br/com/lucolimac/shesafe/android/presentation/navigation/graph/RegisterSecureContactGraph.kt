package br.com.lucolimac.shesafe.android.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.REGISTER_SECURE_CONTACT_ROUTE
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.registerSecureContactScreen
import br.com.lucolimac.shesafe.android.presentation.navigation.destination.secureContactsScreen
import br.com.lucolimac.shesafe.android.presentation.viewModel.RegisterSecureContactViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SecureContactViewModel

internal const val REGISTER_SECURE_CONTACT_GRAPH_ROUTE = "registerSecureContactGraph"
fun NavGraphBuilder.registerSecureContactGraph(
    secureContactViewModel: SecureContactViewModel,
    registerSecureContactViewModel: RegisterSecureContactViewModel,
    navController: NavHostController
) {
    navigation(
        startDestination = REGISTER_SECURE_CONTACT_ROUTE,
        route = REGISTER_SECURE_CONTACT_GRAPH_ROUTE
    ) {
        registerSecureContactScreen(
            registerSecureContactViewModel, navController
        )
        secureContactsScreen(navController, secureContactViewModel)
    }
}

fun NavHostController.navigateToRegisterSecureContactGraph() {
    navigate(REGISTER_SECURE_CONTACT_GRAPH_ROUTE) {
        popUpTo(REGISTER_SECURE_CONTACT_GRAPH_ROUTE) {
            inclusive = true
        }
    }
}