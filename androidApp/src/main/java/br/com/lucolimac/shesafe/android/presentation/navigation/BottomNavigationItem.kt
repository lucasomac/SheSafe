package br.com.lucolimac.shesafe.android.presentation.navigation

import br.com.lucolimac.shesafe.R

sealed class NavigationItem(
    val iconRes: Int
) {
    object SecureContacts : NavigationItem(
        iconRes = R.drawable.list_24px
    )

    object Home : NavigationItem(
        iconRes = R.drawable.home_24px
    )

    object Profile : NavigationItem(
        iconRes = R.drawable.person_24px
    )
}

