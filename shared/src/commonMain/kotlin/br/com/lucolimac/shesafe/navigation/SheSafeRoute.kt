package br.com.lucolimac.shesafe.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes supported by the SheSafe navigation graph.
 */
sealed interface SheSafeRoute {
    /** Route for the authentication / Google Sign-In screen. */
    @Serializable
    data object Login : SheSafeRoute

    /** Route for the main emergency SOS home screen with live map preview. */
    @Serializable
    data object Home : SheSafeRoute

    /** Route for the screen listing all registered trusted contacts. */
    @Serializable
    data object SecureContacts : SheSafeRoute

    /** Route for the user profile, emergency message customization, and settings screen. */
    @Serializable
    data object Profile : SheSafeRoute

    /**
     * Route for creating a new trusted contact or editing an existing one.
     *
     * @property phoneNumber Existing contact phone number when editing, or `null` when registering a new contact.
     */
    @Serializable
    data class RegisterSecureContact(
        val phoneNumber: String? = null,
    ) : SheSafeRoute

    /** Route for the history screen listing previously sent emergency help requests. */
    @Serializable
    data object HelpRequests : SheSafeRoute
}

/**
 * Top-level destinations accessible from the bottom navigation bar.
 *
 * @property route Type-safe [SheSafeRoute] associated with the tab.
 */
enum class BottomNavTab(val route: SheSafeRoute) {
    /** Trusted contacts management tab. */
    SECURE_CONTACTS(SheSafeRoute.SecureContacts),

    /** Primary emergency SOS home tab. */
    HOME(SheSafeRoute.Home),

    /** User profile and preferences tab. */
    PROFILE(SheSafeRoute.Profile),
}
