package br.com.lucolimac.shesafe.android.presentation.component.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.android.presentation.component.SettingItem
import br.com.lucolimac.shesafe.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
) {
    val scope = rememberCoroutineScope()

    var heatMapEnabled by remember { mutableStateOf(false) }
    var sendConfirmationEnabled by remember { mutableStateOf(true) }
    var locationSearchEnabled by remember { mutableStateOf(false) }
    var buttonShortcutEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        SettingItem(
            text = stringResource(R.string.label_setting_heatmap),
            checked = heatMapEnabled,
            onCheckedChange = { heatMapEnabled = it },
        )
        SettingItem(
            text = stringResource(R.string.label_setting_send_confirmation),
            checked = sendConfirmationEnabled,
            onCheckedChange = { sendConfirmationEnabled = it },
        )
        SettingItem(
            text = stringResource(R.string.label_setting_search_location),
            checked = locationSearchEnabled,
            onCheckedChange = { locationSearchEnabled = it },
        )
        SettingItem(
            text = stringResource(R.string.label_setting_button_shortcut),
            checked = buttonShortcutEnabled,
            onCheckedChange = { buttonShortcutEnabled = it },
        )

        Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom if needed

        Button(
            onClick = {
                scope.launch { sheetState.hide() }
                onDismiss()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
        ) {
            Text(text = "Fechar")
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SettingsBottomSheetPreview() {
    val sheetState = rememberModalBottomSheetState()
    MaterialTheme {
        SettingBottomSheet(onDismiss = {}, sheetState = sheetState)
    }
}
