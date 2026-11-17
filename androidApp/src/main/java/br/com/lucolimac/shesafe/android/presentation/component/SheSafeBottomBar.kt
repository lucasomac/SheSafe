package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import br.com.lucolimac.shesafe.android.presentation.navigation.NavigationItem

val BottomBarItems = listOf(
    NavigationItem.SecureContacts, NavigationItem.Home, NavigationItem.Profile
)

@Composable
fun SheSafeBottomBar(
    selected: NavigationItem,
    modifier: Modifier = Modifier,
    menus: List<NavigationItem> = BottomBarItems,
    onBottomAppBarItemSelectedChange: (NavigationItem) -> Unit = {},
) {
    NavigationBar(
        modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        menus.forEach { item ->
            val description = when (item) {
                NavigationItem.Home -> "Início"
                NavigationItem.SecureContacts -> "Contatos Seguros"
                NavigationItem.Profile -> "Perfil"
            }
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(item.iconRes),
                        contentDescription = description
                    )
                },
                selected = selected == item,
                onClick = { onBottomAppBarItemSelectedChange(item) }
            )
        }
    }
}
