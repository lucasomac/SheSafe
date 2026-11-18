package br.com.lucolimac.shesafe.feature.home.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.lucolimac.shesafe.core.designsystem.component.DialogModel
import br.com.lucolimac.shesafe.core.designsystem.component.HomeHeader
import br.com.lucolimac.shesafe.core.designsystem.component.RoundedButton
import br.com.lucolimac.shesafe.core.designsystem.component.SheSafeDialog
import br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme
import br.com.lucolimac.shesafe.core.platform.EmergencyMapView
import br.com.lucolimac.shesafe.core.platform.rememberLocationPermissionControl
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeDialogType
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeEvent
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeUiState
import br.com.lucolimac.shesafe.feature.home.presentation.viewmodel.HomeViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.default_message_danger_user
import shesafe.shared.generated.resources.dialog_confirm_help_message
import shesafe.shared.generated.resources.dialog_confirm_help_send
import shesafe.shared.generated.resources.dialog_confirm_help_title
import shesafe.shared.generated.resources.dialog_dont_ask_again
import shesafe.shared.generated.resources.dialog_no_contacts_confirm
import shesafe.shared.generated.resources.dialog_no_contacts_message
import shesafe.shared.generated.resources.dialog_no_contacts_title
import shesafe.shared.generated.resources.label_cancel
import shesafe.shared.generated.resources.label_grant_location
import shesafe.shared.generated.resources.message_location_permission_rationale
import shesafe.shared.generated.resources.request_help
import shesafe.shared.generated.resources.title_home

@Composable
fun HomeScreen(
    onNavigateToRegisterContact: () -> Unit,
    onShowMessage: (StringResource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val defaultDangerMessage = stringResource(Res.string.default_message_danger_user)

    val locationPermission = rememberLocationPermissionControl { granted ->
        if (granted) {
            viewModel.refreshLocation()
        }
    }

    LaunchedEffect(locationPermission.isGranted) {
        if (locationPermission.isGranted) {
            viewModel.refreshLocation()
        } else {
            locationPermission.requestPermission()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HomeEvent.NavigateToRegisterContact -> onNavigateToRegisterContact()
                is HomeEvent.ShowMessage -> onShowMessage(event.messageRes)
            }
        }
    }

    HomeContent(
        uiState = uiState,
        hasLocationPermission = locationPermission.isGranted,
        onRequestLocationPermission = locationPermission.requestPermission,
        onRequestHelpClick = { viewModel.onRequestHelpClicked(defaultDangerMessage) },
        onConfirmNoContacts = viewModel::onConfirmNoContactsDialog,
        onConfirmSendAlert = { viewModel.onConfirmSendAlert(defaultDangerMessage) },
        onDontAskAgainChanged = viewModel::onDontAskAgainChanged,
        onDismissDialog = viewModel::onDismissDialog,
        modifier = modifier,
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    hasLocationPermission: Boolean,
    onRequestLocationPermission: () -> Unit,
    onRequestHelpClick: () -> Unit,
    onConfirmNoContacts: () -> Unit,
    onConfirmSendAlert: () -> Unit,
    onDontAskAgainChanged: (Boolean) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
    mapContent: @Composable () -> Unit = {
        EmergencyMapView(
            userLocation = uiState.userLocation,
            hasLocationPermission = hasLocationPermission,
            modifier = Modifier.fillMaxSize(),
        )
    },
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeHeader(
            title = stringResource(Res.string.title_home),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        if (!hasLocationPermission) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(Res.string.message_location_permission_rationale),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                    )
                    TextButton(onClick = onRequestLocationPermission) {
                        Text(text = stringResource(Res.string.label_grant_location))
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            mapContent()

            RoundedButton(
                text = stringResource(Res.string.request_help),
                enabled = !uiState.isSending,
                onClick = onRequestHelpClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            )
        }
    }

    when (uiState.activeDialog) {
        HomeDialogType.NONE -> Unit
        HomeDialogType.NO_CONTACTS -> {
            SheSafeDialog(
                dialogModel = DialogModel(
                    title = stringResource(Res.string.dialog_no_contacts_title),
                    message = stringResource(Res.string.dialog_no_contacts_message),
                    confirmButtonText = stringResource(Res.string.dialog_no_contacts_confirm),
                    dismissButtonText = stringResource(Res.string.label_cancel),
                    onConfirm = onConfirmNoContacts,
                    onDismiss = onDismissDialog,
                ),
                onDismissRequest = onDismissDialog,
            )
        }
        HomeDialogType.CONFIRM_SEND -> {
            SheSafeDialog(
                dialogModel = DialogModel(
                    title = stringResource(Res.string.dialog_confirm_help_title),
                    message = stringResource(Res.string.dialog_confirm_help_message),
                    confirmButtonText = stringResource(Res.string.dialog_confirm_help_send),
                    dismissButtonText = stringResource(Res.string.label_cancel),
                    onConfirm = onConfirmSendAlert,
                    onDismiss = onDismissDialog,
                    showCheckbox = true,
                    checkboxText = stringResource(Res.string.dialog_dont_ask_again),
                    initialCheckboxValue = uiState.dontAskAgainChecked,
                    onCheckboxCheckedChange = onDontAskAgainChanged,
                ),
                onDismissRequest = onDismissDialog,
            )
        }
    }
}

@Preview
@Composable
private fun HomeContentLightPreview() {
    SheSafeTheme(darkTheme = false) {
        HomeContent(
            uiState = HomeUiState(),
            hasLocationPermission = false,
            onRequestLocationPermission = {},
            onRequestHelpClick = {},
            onConfirmNoContacts = {},
            onConfirmSendAlert = {},
            onDontAskAgainChanged = {},
            onDismissDialog = {},
            mapContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                )
            },
        )
    }
}

@Preview
@Composable
private fun HomeContentDarkPreview() {
    SheSafeTheme(darkTheme = true) {
        HomeContent(
            uiState = HomeUiState(),
            hasLocationPermission = true,
            onRequestLocationPermission = {},
            onRequestHelpClick = {},
            onConfirmNoContacts = {},
            onConfirmSendAlert = {},
            onDontAskAgainChanged = {},
            onDismissDialog = {},
            mapContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                )
            },
        )
    }
}
