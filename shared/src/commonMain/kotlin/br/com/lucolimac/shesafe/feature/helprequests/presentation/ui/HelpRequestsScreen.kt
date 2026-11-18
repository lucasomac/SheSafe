package br.com.lucolimac.shesafe.feature.helprequests.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.lucolimac.shesafe.core.designsystem.component.ErrorStateBanner
import br.com.lucolimac.shesafe.core.designsystem.component.LastSentCard
import br.com.lucolimac.shesafe.core.designsystem.component.SheSafeLoading
import br.com.lucolimac.shesafe.feature.helprequests.presentation.viewmodel.HelpRequestsUiState
import br.com.lucolimac.shesafe.feature.helprequests.presentation.viewmodel.HelpRequestsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.message_nothing_send_yet

@Composable
fun HelpRequestsScreen(
    modifier: Modifier = Modifier,
    viewModel: HelpRequestsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HelpRequestsContent(
        uiState = uiState,
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

@Composable
fun HelpRequestsContent(
    uiState: HelpRequestsUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            uiState.isLoading -> {
                SheSafeLoading()
            }
            uiState.errorResource != null && uiState.helpRequests.isEmpty() -> {
                ErrorStateBanner(
                    message = stringResource(uiState.errorResource),
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            uiState.helpRequests.isEmpty() -> {
                Text(
                    text = stringResource(Res.string.message_nothing_send_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = uiState.helpRequests,
                        key = { it.id.ifBlank { "${it.createdAt.toEpochMilliseconds()}" } },
                    ) { request ->
                        LastSentCard(lastSent = request)
                    }
                }
            }
        }
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun HelpRequestsLightPreview() {
    br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme(darkTheme = false) {
        HelpRequestsContent(
            uiState = HelpRequestsUiState(
                isLoading = false,
                helpRequests = listOf(
                    br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest(
                        id = "1",
                        phoneNumbers = listOf("11999998888", "21988887777"),
                        location = null,
                        createdAt = kotlin.time.Instant.fromEpochMilliseconds(1_700_000_000_000L),
                    ),
                ),
            ),
            onRetry = {},
        )
    }
}

@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun HelpRequestsEmptyDarkPreview() {
    br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme(darkTheme = true) {
        HelpRequestsContent(
            uiState = HelpRequestsUiState(isLoading = false),
            onRetry = {},
        )
    }
}
