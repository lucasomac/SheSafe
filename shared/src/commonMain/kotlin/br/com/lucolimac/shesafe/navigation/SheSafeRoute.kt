package br.com.lucolimac.shesafe.navigation

import kotlinx.serialization.Serializable

sealed interface SheSafeRoute {
    @Serializable
    data object Login : SheSafeRoute

    @Serializable
    data object Home : SheSafeRoute

    @Serializable
    data object SecureContacts : SheSafeRoute

    @Serializable
    data object Profile : SheSafeRoute

    @Serializable
    data class RegisterSecureContact(
        val phoneNumber: String? = null,
    ) : SheSafeRoute

    @Serializable
    data object HelpRequests : SheSafeRoute
}

enum class BottomNavTab(val route: SheSafeRoute) {
    SECURE_CONTACTS(SheSafeRoute.SecureContacts),
    HOME(SheSafeRoute.Home),
    PROFILE(SheSafeRoute.Profile),
}
