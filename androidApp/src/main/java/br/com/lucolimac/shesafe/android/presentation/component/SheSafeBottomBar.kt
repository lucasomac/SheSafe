package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.lucolimac.shesafe.android.presentation.model.NavigationItem

@Composable
fun SheSafeBottomBar(
    selected: NavigationItem,
    modifier: Modifier = Modifier,
    menus: List<NavigationItem> = emptyList(),
    onBottomAppBarItemSelectedChange: (NavigationItem) -> Unit = {},
) {
    NavigationBar(modifier, containerColor = MaterialTheme.colorScheme.background, contentColor = MaterialTheme.colorScheme.onBackground) {
        menus.forEach {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.sheSafeDestination.route.title
                    )
                },
                selected = selected.sheSafeDestination.route.title == it.sheSafeDestination.route.title,
                onClick = { onBottomAppBarItemSelectedChange(it) })
        }
    }
}
