package br.com.lucolimac.shesafe.route


sealed class NavigationItem(val route: String) {
    enum class SheSafeRoute(val title: String) {
        HOME("Home"), LOGIN("Login"), CONTACTS("Meus Contatos"), PROFILE("Profile"), SETTINGS("Configurações"), ERROR(
            "Error"
        )
    }

    object Home : NavigationItem(SheSafeRoute.HOME.name)
    object Login : NavigationItem(SheSafeRoute.LOGIN.name)
    object Contacts : NavigationItem(SheSafeRoute.CONTACTS.name)
    object Profile : NavigationItem(SheSafeRoute.PROFILE.name)
    object Settings : NavigationItem(SheSafeRoute.SETTINGS.name)
    object Error : NavigationItem(SheSafeRoute.ERROR.name)
}