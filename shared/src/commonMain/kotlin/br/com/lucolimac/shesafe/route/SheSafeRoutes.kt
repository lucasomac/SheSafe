package br.com.lucolimac.shesafe.route


//sealed class SheSafeDestination(val route: SheSafeRoute) {
//    enum class SheSafeRoute(val title: String) {
//        HOME("Home"), LOGIN("Login"), CONTACTS("Meus Contatos"), PROFILE("Profile"), REGISTER_CONTACT(
//            "Secure Contact"
//        ),
//
//        HELP_REQUESTS("Pedidos de ajuda enviados"), ERROR(
//            "Error"
//        )
//    }
//
//    object Home : SheSafeDestination(SheSafeRoute.HOME)
//    object Login : SheSafeDestination(SheSafeRoute.LOGIN)
//    object Contacts : SheSafeDestination(SheSafeRoute.CONTACTS)
//    object Profile : SheSafeDestination(SheSafeRoute.PROFILE)
//    object RegisterContact : SheSafeDestination(SheSafeRoute.REGISTER_CONTACT)
//    object HelpRequests : SheSafeDestination(SheSafeRoute.HELP_REQUESTS)
//    object Error : SheSafeDestination(SheSafeRoute.ERROR)
//}