package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.presentation.component.ActionIcon
import br.com.lucolimac.shesafe.android.presentation.component.HomeHeader
import br.com.lucolimac.shesafe.android.presentation.component.LastSentCard
import br.com.lucolimac.shesafe.android.presentation.component.SheSafeLoading
import br.com.lucolimac.shesafe.android.presentation.component.bottomsheet.SettingBottomSheet
import br.com.lucolimac.shesafe.android.presentation.component.bottomsheet.UserMessageBottomSheet
import br.com.lucolimac.shesafe.android.presentation.viewModel.AuthViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.ProfileViewModel
import br.com.lucolimac.shesafe.android.presentation.viewModel.SettingsViewModel
import coil.compose.rememberAsyncImagePainter
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    helpRequestViewModel: HelpRequestViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel,
    onHelpRequestsShowClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lastSentList by helpRequestViewModel.helpRequests.collectAsState()
    val userMessage by profileViewModel.helpMessage.collectAsState()
    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    var showUserMessageBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val isLoading = helpRequestViewModel.isLoading.collectAsState().value
    val userName = authViewModel.userName.collectAsState().value
    val userPhotoUrl = authViewModel.userPhotoUrl.collectAsState().value
    LaunchedEffect(lastSentList) {
        helpRequestViewModel.fetchHelpRequests()
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) // Background color
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            // Profile Section
            HomeHeader(
                stringResource(R.string.title_profile),
                modifier = modifier.align(Alignment.CenterHorizontally),
            )

            Image(
                painter = rememberAsyncImagePainter(
                    model = userPhotoUrl,
                ),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape),
            )

            Text(
                text = userName ?: stringResource(R.string.default_user_name),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = userMessage.takeIf { it.isNotEmpty() }
                    ?: stringResource(R.string.default_message_danger_user),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            )

            // Action Icons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            ) {
                ActionIcon(
                    Icons.Default.Settings,
                    description = "Settings",
                ) { showSettingsBottomSheet = true }
                ActionIcon(
                    Icons.Default.Menu,
                    description = "Menu",
                    onClick = { onHelpRequestsShowClick() })
                ActionIcon(Icons.Default.Edit, description = "Edit") {
                    showUserMessageBottomSheet = true
                }
                ActionIcon(
                    painter = painterResource(R.drawable.logout_24),
                    description = "Logout",
                    onClick = onLogoutClick,
                )
            }

            // Last Sent Section
            Text(
                text = "Últimos envios",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp),
            )
            when {
                isLoading -> {
                    SheSafeLoading()
                }

                lastSentList.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.message_nothing_send_yet),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                    )
                }

                else -> {
                    LazyColumn {
                        items(lastSentList.take(5).size) { index ->
                            LastSentCard(lastSent = lastSentList[index])
                        }
                    }
                }
            }
        }
    }
    if (showSettingsBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            content = {
                SettingBottomSheet(
                    settingsViewModel,
                    onDismiss = {
                        showSettingsBottomSheet = false
                    },
                    sheetState = sheetState,
                )
            },
            onDismissRequest = {
                showSettingsBottomSheet = false
            },
        )
    }
    if (showUserMessageBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            content = {
                UserMessageBottomSheet(
                    actualMessage = userMessage,
                    onDismiss = {
                        showSettingsBottomSheet = false
                    },
                    onConfirm = { newMessage ->
                        profileViewModel.registerHelpMessage(newMessage)
                        showUserMessageBottomSheet = false
                    },
                    sheetState = sheetState,
                )
            },
            onDismissRequest = {
                showSettingsBottomSheet = false
            },
        )
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenPreview() {
    val profileViewModel: ProfileViewModel by inject(ProfileViewModel::class.java)
    val helpRequestViewModel: HelpRequestViewModel by inject(HelpRequestViewModel::class.java)
    val settingsViewModel: SettingsViewModel by inject(SettingsViewModel::class.java)
    val authViewModel: AuthViewModel by inject(AuthViewModel::class.java)
    ProfileScreen(
        profileViewModel = profileViewModel,
        helpRequestViewModel = helpRequestViewModel,
        settingsViewModel = settingsViewModel,
        authViewModel = authViewModel,
        onHelpRequestsShowClick = {},
        onLogoutClick = {})
}

