package br.com.lucolimac.shesafe.route


sealed class SheSafeDestination(val route: SheSafeRoute) {
    enum class SheSafeRoute(val title: String) {
        HOME("Home"), LOGIN("Login"), CONTACTS("Meus Contatos"), PROFILE("Profile"), SETTINGS("Configurações"), ERROR(
            "Error"
        )
    }

    object Home : SheSafeDestination(SheSafeRoute.HOME)
    object Login : SheSafeDestination(SheSafeRoute.LOGIN)
    object Contacts : SheSafeDestination(SheSafeRoute.CONTACTS)
    object Profile : SheSafeDestination(SheSafeRoute.PROFILE)
    object Settings : SheSafeDestination(SheSafeRoute.SETTINGS)
    object Error : SheSafeDestination(SheSafeRoute.ERROR)
}