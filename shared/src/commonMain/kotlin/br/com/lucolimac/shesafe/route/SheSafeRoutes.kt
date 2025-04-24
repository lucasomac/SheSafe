package br.com.lucolimac.shesafe.route


sealed class SheSafeDestination(val route: SheSafeRoute) {
    enum class SheSafeRoute(val title: String) {
        HOME("Home"), LOGIN("Login"), CONTACTS("Meus Contatos"), PROFILE("Profile"), REGISTER_CONTACT(
            "Contato"
        ),
        SETTINGS("Configurações"), ERROR(
            "Error"
        )
    }

    object Home : SheSafeDestination(SheSafeRoute.HOME)
    object Login : SheSafeDestination(SheSafeRoute.LOGIN)
    object Contacts : SheSafeDestination(SheSafeRoute.CONTACTS)
    object Profile : SheSafeDestination(SheSafeRoute.PROFILE)
    object RegisterContact : SheSafeDestination(SheSafeRoute.REGISTER_CONTACT)
    object Settings : SheSafeDestination(SheSafeRoute.SETTINGS)
    object Error : SheSafeDestination(SheSafeRoute.ERROR)
}