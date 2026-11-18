package br.com.lucolimac.shesafe.feature.profile.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.lucolimac.shesafe.core.designsystem.component.ActionIcon
import br.com.lucolimac.shesafe.core.designsystem.component.HomeHeader
import br.com.lucolimac.shesafe.core.designsystem.component.LastSentCard
import br.com.lucolimac.shesafe.core.designsystem.component.SettingsBottomSheet
import br.com.lucolimac.shesafe.core.designsystem.component.SheSafeLoading
import br.com.lucolimac.shesafe.core.designsystem.component.UserMessageBottomSheet
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileActiveSheet
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileEvent
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileUiState
import br.com.lucolimac.shesafe.feature.profile.presentation.viewmodel.ProfileViewModel
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.cd_edit_message
import shesafe.shared.generated.resources.cd_help_history
import shesafe.shared.generated.resources.cd_logout
import shesafe.shared.generated.resources.cd_profile_picture
import shesafe.shared.generated.resources.cd_settings
import shesafe.shared.generated.resources.default_message_danger_user
import shesafe.shared.generated.resources.default_user_name
import shesafe.shared.generated.resources.edit_24px
import shesafe.shared.generated.resources.logout_24
import shesafe.shared.generated.resources.menu_24px
import shesafe.shared.generated.resources.message_nothing_send_yet
import shesafe.shared.generated.resources.person_24px
import shesafe.shared.generated.resources.settings_24px
import shesafe.shared.generated.resources.title_last_sent
import shesafe.shared.generated.resources.title_profile

/**
 * Stateful destination composable for the user profile screen, wiring [ProfileViewModel] state and
 * one-shot [ProfileEvent] side effects to navigation and message callbacks.
 *
 * @param onNavigateToHelpRequests Callback invoked to open the full help requests history screen.
 * @param onNavigateToLogin Callback invoked after the user signs out to return to the login flow.
 * @param onShowMessage Callback invoked to surface transient user-facing feedback from a [StringResource].
 * @param modifier Optional [Modifier] applied to the root surface.
 * @param viewModel [ProfileViewModel] instance resolved via Koin by default.
 */
@Composable
fun ProfileScreen(
    onNavigateToHelpRequests: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onShowMessage: (StringResource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileEvent.NavigateToHelpRequests -> onNavigateToHelpRequests()
                ProfileEvent.NavigateToLogin -> onNavigateToLogin()
                is ProfileEvent.ShowMessage -> onShowMessage(event.messageRes)
            }
        }
    }

    ProfileContent(
        uiState = uiState,
        onOpenSettings = viewModel::onOpenSettingsSheet,
        onOpenHelpRequests = viewModel::onShowHelpRequestsClicked,
        onOpenEditMessage = viewModel::onOpenEditMessageSheet,
        onLogout = viewModel::onLogoutClicked,
        onToggleSetting = viewModel::onToggleSetting,
        onSaveHelpMessage = viewModel::onSaveHelpMessage,
        onDismissSheet = viewModel::onDismissSheet,
        modifier = modifier,
    )
}

/**
 * Stateless UI content for the profile screen, rendering user identity, active SOS message, quick
 * action icons, recent help requests, and modal bottom sheets for settings or message editing.
 *
 * @param uiState Current [ProfileUiState] snapshot driving the UI.
 * @param onOpenSettings Callback invoked when the settings action button is tapped.
 * @param onOpenHelpRequests Callback invoked when the help history action button is tapped.
 * @param onOpenEditMessage Callback invoked when the edit SOS message action button is tapped.
 * @param onLogout Callback invoked when the logout action button is tapped.
 * @param onToggleSetting Callback invoked when a [SettingKey] switch is toggled in the settings sheet.
 * @param onSaveHelpMessage Callback invoked when the user confirms a new custom SOS message.
 * @param onDismissSheet Callback invoked when any active modal bottom sheet is dismissed.
 * @param modifier Optional [Modifier] applied to the root [Surface].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onOpenSettings: () -> Unit,
    onOpenHelpRequests: () -> Unit,
    onOpenEditMessage: () -> Unit,
    onLogout: () -> Unit,
    onToggleSetting: (SettingKey, Boolean) -> Unit,
    onSaveHelpMessage: (String) -> Unit,
    onDismissSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()
    val defaultMessage = stringResource(Res.string.default_message_danger_user)
    val effectiveMessage = uiState.customHelpMessage.ifBlank { defaultMessage }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            HomeHeader(
                title = stringResource(Res.string.title_profile),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            val photoUrl = uiState.user?.photoUrl
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = stringResource(Res.string.cd_profile_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.person_24px),
                        contentDescription = stringResource(Res.string.cd_profile_picture),
                        modifier = Modifier.size(64.dp),
                    )
                }
            }

            Text(
                textAlign = TextAlign.Center,
                text = uiState.user?.displayName?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.default_user_name),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp),
            )

            Text(
                text = effectiveMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                ActionIcon(
                    painter = painterResource(Res.drawable.settings_24px),
                    description = stringResource(Res.string.cd_settings),
                    onClick = onOpenSettings,
                )
                ActionIcon(
                    painter = painterResource(Res.drawable.menu_24px),
                    description = stringResource(Res.string.cd_help_history),
                    onClick = onOpenHelpRequests,
                )
                ActionIcon(
                    painter = painterResource(Res.drawable.edit_24px),
                    description = stringResource(Res.string.cd_edit_message),
                    onClick = onOpenEditMessage,
                )
                ActionIcon(
                    painter = painterResource(Res.drawable.logout_24),
                    description = stringResource(Res.string.cd_logout),
                    onClick = onLogout,
                )
            }

            Text(
                text = stringResource(Res.string.title_last_sent),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
                    .semantics { heading() },
            )

            when {
                uiState.isLoading -> {
                    SheSafeLoading()
                }
                uiState.recentHelpRequests.isEmpty() -> {
                    Text(
                        text = stringResource(Res.string.message_nothing_send_yet),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(
                            items = uiState.recentHelpRequests,
                            key = { it.id.ifBlank { "${it.createdAt.toEpochMilliseconds()}" } },
                        ) { helpRequest ->
                            LastSentCard(lastSent = helpRequest)
                        }
                    }
                }
            }
        }
    }

    when (uiState.activeSheet) {
        ProfileActiveSheet.NONE -> Unit
        ProfileActiveSheet.SETTINGS -> {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = onDismissSheet,
            ) {
                SettingsBottomSheet(
                    settings = uiState.settings,
                    sheetState = sheetState,
                    onToggleSetting = onToggleSetting,
                    onDismiss = onDismissSheet,
                )
            }
        }
        ProfileActiveSheet.EDIT_MESSAGE -> {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = onDismissSheet,
            ) {
                UserMessageBottomSheet(
                    actualMessage = effectiveMessage,
                    sheetState = sheetState,
                    onDismiss = onDismissSheet,
                    onConfirm = onSaveHelpMessage,
                )
            }
        }
    }
}

/**
 * Light-theme Compose preview of [ProfileContent] displaying a signed-in user profile.
 */
@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ProfileContentLightPreview() {
    br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme(darkTheme = false) {
        ProfileContent(
            uiState = ProfileUiState(
                isLoading = false,
                user = br.com.lucolimac.shesafe.feature.auth.domain.model.UserProfile(
                    uid = "u1",
                    email = "maria@example.com",
                    displayName = "Maria Silva",
                    photoUrl = null,
                ),
            ),
            onOpenSettings = {},
            onOpenHelpRequests = {},
            onOpenEditMessage = {},
            onLogout = {},
            onToggleSetting = { _, _ -> },
            onSaveHelpMessage = {},
            onDismissSheet = {},
        )
    }
}

/**
 * Dark-theme Compose preview of [ProfileContent] with default placeholder user details.
 */
@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun ProfileContentDarkPreview() {
    br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme(darkTheme = true) {
        ProfileContent(
            uiState = ProfileUiState(isLoading = false),
            onOpenSettings = {},
            onOpenHelpRequests = {},
            onOpenEditMessage = {},
            onLogout = {},
            onToggleSetting = { _, _ -> },
            onSaveHelpMessage = {},
            onDismissSheet = {},
        )
    }
}
