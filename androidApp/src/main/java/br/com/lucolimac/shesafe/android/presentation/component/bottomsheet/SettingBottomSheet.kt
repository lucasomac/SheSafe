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
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import br.com.lucolimac.shesafe.enum.SettingsEnum
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingBottomSheet(
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    sheetState: SheetState,
) {
    val scope = rememberCoroutineScope()
    val mapOfSettings by settingsViewModel.mapOfSettings.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp),
        )

//        SettingItem(
//            text = stringResource(R.string.label_setting_heatmap),
//            checked = mapOfSettings[SettingsEnum.HEATMAP] == true,
//            onCheckedChange = { heatMapEnabled = it },
//        )
        SettingItem(
            text = stringResource(R.string.label_setting_send_confirmation),
            checked = mapOfSettings[SettingsEnum.SEND_CONFIRMATION] == true,
            onCheckedChange = { settingsViewModel.setToggleSetting(SettingsEnum.SEND_CONFIRMATION, it) },
        )
//        SettingItem(
//            text = stringResource(R.string.label_setting_search_location),
//            checked = locationSearchEnabled,
//            onCheckedChange = { locationSearchEnabled = it },
//        )
//        SettingItem(
//            text = stringResource(R.string.label_setting_button_shortcut),
//            checked = buttonShortcutEnabled,
//            onCheckedChange = { buttonShortcutEnabled = it },
//        )

        Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom if needed

        Button(
            onClick = {
                scope.launch { sheetState.hide() }
                onDismiss()
            },
            modifier = Modifier
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
    val settingsViewModel: SettingsViewModel by inject<SettingsViewModel>(SettingsViewModel::class.java)
    MaterialTheme {
        SettingBottomSheet(settingsViewModel, onDismiss = {}, sheetState = sheetState)
    }
}
